import java.security.AccessControlException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ListShareDeleter implements CallHandler {
    private final Connection connection;
    private final String cognitoID;

    private final String GET_LIST_ACCESS = "SELECT * FROM List WHERE (owner = ? AND listID = ?);";
    private final String REMOVE_SHAREE = "DELETE FROM ListSharee WHERE listID = ? AND user = ?;";

    public ListShareDeleter(Connection connection, String cognitoID) {
        this.connection = connection;
        this.cognitoID = cognitoID;
    }

    @Override
    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryMap, String cognitoID) throws SQLException {
        Integer listID = Integer.parseInt(queryMap.get("id"));

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
        
        String shareeID = new String(invokeResult.getPayload().array()).replace("\"", "");
        
        //Ensure that the user who is unsharing a list is the owner of that list
        PreparedStatement accessCheck = connection.prepareStatement(GET_LIST_ACCESS);
        accessCheck.setString(1, cognitoID);
        accessCheck.setInt(2, listID);

        ResultSet userLists = accessCheck.executeQuery();

        //User does not own the list; unshare attempt fails
        if (!userLists.next()) {
            throw new AccessControlException("User does not have access to list");
        }

        //Unshare the list with the specified sharee
        PreparedStatement unshareList = connection.prepareStatement(REMOVE_SHAREE);
        unshareList.setInt(1, listID);
        unshareList.setInt(2, shareeID);

        cleanAccess.executeUpdate();
        connection.commit();

        return null;
    }
}
