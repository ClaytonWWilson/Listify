import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.ListUsersRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersResult;
import com.amazonaws.services.cognitoidp.model.UserType;

import java.io.IOException;
import java.sql.Connection;
import java.util.*;

public class UserGetter implements CallHandler {
    private String cognitoID;

    public UserGetter(Connection connection, String cognitoID) {
        this.cognitoID = cognitoID;
    }
    @Override
    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryMap, String cognitoID) {
        Properties cognitoProperties;
        try {
            cognitoProperties = DBConnector.loadProperties("cognitoProperties.json");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        String userPoolId = cognitoProperties.get("userPoolId").toString();
        System.out.println(userPoolId);
        ListUsersRequest checkRequest = new ListUsersRequest().withUserPoolId(userPoolId);
        Object emailObject = bodyMap.get("emailToCheck");
        if (emailObject != null) {
            checkRequest.setFilter("email=\"" + emailObject.toString() +"\"");
        } else {
            //            checkRequest.setFilter("sub=\"" + cognitoID + "\"");
            return cognitoID;
        }
        System.out.println(checkRequest);
        AWSCognitoIdentityProvider awsCognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.defaultClient();
        ListUsersResult foundUsersResult = awsCognitoIdentityProvider.listUsers(checkRequest);
        List<UserType> foundUsers = foundUsersResult.getUsers();
        if (foundUsers.size() != 1) {
            System.out.println(foundUsers);
            if (foundUsers.size() == 0) {
                throw new InputMismatchException("Not user with given email");
            }
            throw new InputMismatchException("Found more than one user with supposedly unique email");
        }
        UserType foundUser = foundUsers.get(0);
        System.out.println(foundUser.getAttributes());
        String sub = "";
        for (AttributeType attribute : foundUser.getAttributes()) {
            if (attribute.getName().equals("sub")) {
                sub = attribute.getValue();
                break;
            }
            System.out.println(attribute.getName() + ": " + attribute.getValue());
        }
        return sub;
    }
}
