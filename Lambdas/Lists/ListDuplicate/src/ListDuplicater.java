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

public class ListDuplicater implements CallHandler {

    private Connection connection;
    private String cognitoID;

    private final String LIST_POPULATE = "INSERT INTO ListProduct(listID, productID, quantity, addedDate, purchased) SELECT ?, productID, quantity, addedDate, purchased FROM ListProduct WHERE listID = ?;";
    private final String ACCESS_CHECK = "SELECT * from ListSharee WHERE userID = ? and listID = ?;";

    public ListDuplicater(Connection connection, String cognitoID) {
        this.connection = connection;
        this.cognitoID = cognitoID;
    }

    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryString, String cognitoID) throws SQLException {
        String listName = bodyMap.get("name").toString();//Needs safe checking
        Integer copyListID = (Integer) bodyMap.get("listID");

        PreparedStatement accessCheck = connection.prepareStatement(ACCESS_CHECK);
        accessCheck.setString(1, cognitoID);
        accessCheck.setInt(2, copyListID);
        ResultSet access = accessCheck.executeQuery();
        if (access.next()) {
            if (!ListPermissions.hasPermission(access.getInt("permissionLevel"), "Read")) {
                throw new AccessControlException("User " + cognitoID + " does not have write permissions for list " + copyListID);
            }
        } else {
            throw new AccessControlException("User " + cognitoID + " does not have any permissions to access list " + copyListID);
        }


        InvokeRequest invokeRequest = new InvokeRequest();
        invokeRequest.setFunctionName("ListPOST");
        invokeRequest.setPayload("{" +
                "  \"body\": {" +
                "    \"name\": \"" + listName + "\"" +
                "  }," +
                "  \"params\": {" +
                "      \"querystring\": {" +
                "      }" +
                "  }," +
                "  \"context\": {" +
                "    \"sub\": \"" + cognitoID + "\"" +
                "  }" +
                "}");
        System.out.println(invokeRequest);
        InvokeResult listCreateResult = AWSLambdaClientBuilder.defaultClient().invoke(invokeRequest);
        if (listCreateResult.getStatusCode() != 200) {
            throw new InputMismatchException("Could not find specified user to share with");
        }
        Integer newListID = Integer.parseInt(new String(listCreateResult.getPayload().array()).replace("\"", ""));

        PreparedStatement populateList = connection.prepareStatement(LIST_POPULATE);
        populateList.setInt(1, newListID);
        populateList.setInt(2, copyListID);
        System.out.println(populateList);
        populateList.executeUpdate();
        connection.commit();
        return newListID;
    }
}
