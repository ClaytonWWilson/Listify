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
    final private String SHARE_LIST = "INSERT INTO ListSharee(listID, userID) VALUES(?, ?);";

    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryString, String cognitoID) throws SQLException {
        PreparedStatement checkAccess = connection.prepareStatement(CHECK_ACCESS);
        Integer listID = Integer.parseInt(bodyMap.get("listID").toString());
        checkAccess.setInt(1, listID);
        checkAccess.setString(2, cognitoID);
        ResultSet checkAccessRS = checkAccess.executeQuery();
        if (!checkAccessRS.next()) {
            throw new AccessControlException("The requesting user does not have access to the requested list");
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
        checkAccess.setString(2, shareWithSub);
        checkAccessRS = checkAccess.executeQuery();
        if (checkAccessRS.next()) {
            throw new InputMismatchException("The specified user already has access");
        }

        PreparedStatement shareList = connection.prepareStatement(SHARE_LIST);
        shareList.setInt(1, listID);
        shareList.setString(2, shareWithSub);
        shareList.executeUpdate();
        connection.commit();
        return null;
    }
}