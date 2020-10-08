import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserDeleter implements CallHandler {

    private DBConnector connector;
    private String cognitoID;

    //private final String REMOVE_FROM_LIST = "DELETE FROM ListProduct WHERE (ProductID = ? AND ListID = ?);";
    private final String GET_LISTS = "SELECT * FROM List WHERE (owner = ?);";

    public UserDeleter(DBConnector connector, String cognitoID) {
        this.connector = connector;
        this.cognitoID = cognitoID;
    }

    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryString, String cognitoID) throws SQLException {
//        AWSCognitoIdentityProvider awsCognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.defaultClient();
//        Properties cognitoProperties;
//        try {
//            cognitoProperties = DBConnector.loadProperties("cognitoProperties.json");
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//        String userPoolId = cognitoProperties.get("userPoolId").toString();
//        System.out.println(userPoolId);
//        AdminUserGlobalSignOutRequest adminUserGlobalSignOutRequest = new AdminUserGlobalSignOutRequest().withUserPoolId(userPoolId);
//        adminUserGlobalSignOutRequest.setUsername(cognitoID);
//        System.out.println(adminUserGlobalSignOutRequest);
//        awsCognitoIdentityProvider.adminUserGlobalSignOut(adminUserGlobalSignOutRequest);
//        AdminDeleteUserRequest adminDeleteUserRequest = new AdminDeleteUserRequest().withUserPoolId(userPoolId);
//        adminDeleteUserRequest.setUsername(cognitoID);
//        System.out.println(adminDeleteUserRequest);
//        awsCognitoIdentityProvider.adminDeleteUser(adminDeleteUserRequest);


        Connection connection = connector.getConnection();
//        try {
        PreparedStatement statement = connection.prepareStatement(GET_LISTS);
        statement.setString(1, cognitoID);
        System.out.println(statement);
        ResultSet userLists = statement.executeQuery();
        while (userLists.next()) {
            int listID = userLists.getInt("listID");
            System.out.println(String.format("%d", listID));
        }

//            connection.commit();
//        } finally {
//            connection.close();
//        }
        return null;


//        return null;

        //        Connection connection = connector.getConnection();
//        try {
//            PreparedStatement statement = connection.prepareStatement(REMOVE_FROM_LIST);
//            statement.setInt(1, (Integer) bodyMap.get("ProductID"));
//            statement.setInt(2, (Integer) bodyMap.get("ListID"));
//            System.out.println(statement);
//            statement.executeUpdate();
//            connection.commit();
//        } finally {
//            connection.close();
//        }
//        return null;
    }
}
