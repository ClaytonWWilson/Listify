import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminDeleteUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminUserGlobalSignOutRequest;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class UserDeleter implements CallHandler {

    private Connection connection;
    private String cognitoID;

    private final String GET_LISTS = "SELECT * FROM List WHERE (owner = ?);";
    private final String DELETE_LIST_PRODUCT = "DELETE FROM ListProduct WHERE (listID = ?);";
    private final String DELETE_LISTS = "DELETE FROM List WHERE (owner = ?);";

    public UserDeleter(Connection connection, String cognitoID) {
        this.connection = connection;
        this.cognitoID = cognitoID;
    }

    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryString, String cognitoID) throws SQLException {
        AWSCognitoIdentityProvider awsCognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.defaultClient();
        Properties cognitoProperties;
        try {
            cognitoProperties = DBConnector.loadProperties("cognitoProperties.json");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        String userPoolId = cognitoProperties.get("userPoolId").toString();
        System.out.println(userPoolId);
        AdminUserGlobalSignOutRequest adminUserGlobalSignOutRequest = new AdminUserGlobalSignOutRequest().withUserPoolId(userPoolId);
        adminUserGlobalSignOutRequest.setUsername(cognitoID);
        System.out.println(adminUserGlobalSignOutRequest);
        awsCognitoIdentityProvider.adminUserGlobalSignOut(adminUserGlobalSignOutRequest);
        AdminDeleteUserRequest adminDeleteUserRequest = new AdminDeleteUserRequest().withUserPoolId(userPoolId);
        adminDeleteUserRequest.setUsername(cognitoID);
        System.out.println(adminDeleteUserRequest);
        awsCognitoIdentityProvider.adminDeleteUser(adminDeleteUserRequest);


        try {
            PreparedStatement statement = connection.prepareStatement(GET_LISTS);
            statement.setString(1, cognitoID);
            System.out.println(statement);
            ResultSet userLists = statement.executeQuery();
            while (userLists.next()) {
                int listID = userLists.getInt("listID");

                statement = connection.prepareStatement(DELETE_LIST_PRODUCT);
                statement.setInt(1, listID);
                System.out.println(statement);
                statement.executeQuery();
            }

            statement = connection.prepareStatement(DELETE_LISTS);
            statement.setString(1, cognitoID);
            System.out.println(statement);
            statement.executeQuery();
            connection.commit();
        } finally {
            connection.close();
        }
        return null;
    }
}
