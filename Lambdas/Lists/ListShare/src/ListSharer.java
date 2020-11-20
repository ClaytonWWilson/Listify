import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;

import java.security.AccessControlException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;

public class ListSharer implements CallHandler {

    private Connection connection;
    private String cognitoID;

    public ListSharer(Connection connection, String cognitoID) {
        this.connection = connection;
        this.cognitoID = cognitoID;
    }

    final private String CHECK_ACCESS = "SELECT * from ListSharee WHERE listID = ? AND userID = ?;";
    private final String UI_POSITION_CHECK = "SELECT Max(uiPosition) as maxUIPosition FROM ListSharee WHERE userID = ?;";
    final private String SHARE_LIST = "INSERT INTO ListSharee(listID, userID, permissionLevel, uiPosition) VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE permissionLevel = ?;";

    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryString, String cognitoID) throws SQLException {
        PreparedStatement checkAccess = connection.prepareStatement(CHECK_ACCESS);
        Integer listID = Integer.parseInt(bodyMap.get("listID").toString());
        checkAccess.setInt(1, listID);
        checkAccess.setString(2, cognitoID);
        ResultSet checkAccessRS = checkAccess.executeQuery();
        if (checkAccessRS.next()) {
            if (!ListPermissions.hasPermission(checkAccessRS.getInt("permissionLevel"), "Share")) {
                throw new AccessControlException("User " + cognitoID + " does not have share permissions for list " + listID);
            }
        } else {
            throw new AccessControlException("User " + cognitoID + " does not have any permissions to access list " + listID);
        }
        InvokeRequest invokeRequest = new InvokeRequest();
        invokeRequest.setFunctionName("UserGET");
        invokeRequest.setPayload("{" +
                "  \"body\": {" +
                "      \"emailToCheck\": \"" + bodyMap.get("shareWithEmail").toString() + "\"" +
                "  }," +
                "  \"params\": {" +
                "      \"querystring\": {" +
                "      }" +
                "  }," +
                "  \"context\": {" +
                "    \"sub\": \"not used\"" +
                "  }" +
                "}");
        InvokeResult invokeResult = AWSLambdaClientBuilder.defaultClient().invoke(invokeRequest);
        if (invokeResult.getStatusCode() != 200) {
            throw new InputMismatchException("Could not find specified user to share with");
        }
        String shareWithSub = new String(invokeResult.getPayload().array()).replace("\"", "");
//        checkAccess.setString(2, shareWithSub);
//        checkAccessRS = checkAccess.executeQuery();
//        if (checkAccessRS.next()) {
//            throw new InputMismatchException("The specified user already has access");
//        }

        PreparedStatement uiPositionCheck = connection.prepareStatement(UI_POSITION_CHECK);
        uiPositionCheck.setString(1, shareWithSub);
        System.out.println(uiPositionCheck);
        ResultSet uiPositionCheckRS = uiPositionCheck.executeQuery();
        int nextPosition = 1;
        if (uiPositionCheckRS.next()) {
            nextPosition = uiPositionCheckRS.getInt("maxUIPosition") + 1;
        }

        PreparedStatement shareList = connection.prepareStatement(SHARE_LIST);
        shareList.setInt(1, listID);
        shareList.setString(2, shareWithSub);
        Integer permissionLevel = Integer.parseInt(bodyMap.get("permissionLevel").toString());
        shareList.setInt(3, permissionLevel);
        shareList.setInt(4, nextPosition);
        shareList.setInt(5, permissionLevel);
        System.out.println(shareList);
        shareList.executeUpdate();
        connection.commit();
        return null;
    }
}
