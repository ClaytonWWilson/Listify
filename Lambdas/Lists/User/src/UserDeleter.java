import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminDeleteUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminUserGlobalSignOutRequest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class UserDeleter implements CallHandler {

    private DBConnector connector;
    private String cognitoID;

    //private final String REMOVE_FROM_LIST = "DELETE FROM ListProduct WHERE (ProductID = ? AND ListID = ?);";

    public UserDeleter(DBConnector connector, String cognitoID) {
        this.connector = connector;
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
        AdminDeleteUserRequest adminDeleteUserRequest = new AdminDeleteUserRequest().withUserPoolId(userPoolId);
        adminDeleteUserRequest.setUsername(cognitoID);
        System.out.println(adminDeleteUserRequest);
        awsCognitoIdentityProvider.adminDeleteUser(adminDeleteUserRequest);
        AdminUserGlobalSignOutRequest adminUserGlobalSignOutRequest = new AdminUserGlobalSignOutRequest().withUserPoolId(userPoolId);
        adminUserGlobalSignOutRequest.setUsername(cognitoID);
        System.out.println(adminUserGlobalSignOutRequest);
        awsCognitoIdentityProvider.adminUserGlobalSignOut(adminUserGlobalSignOutRequest);

        return null;

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
