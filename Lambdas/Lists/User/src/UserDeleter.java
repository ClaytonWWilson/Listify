import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserDeleter implements CallHandler {

    private Connection connection;
    private String cognitoID;

    static final String DELETION_EMAIL_FROM = "merzn@purdue.edu";
    static final String CONFIGSET = "ConfigSet";
    static final String DELETION_EMAIL_SUBJECT = "Listify Account Deletion Confirmation";
    static final String HTMLBODY = "<h1>We're sad to see you go!</h1>"
            + "<p> This email is to confirm your Listify account has been deleted. If you have any questions"
            + " or did not request this action, please reply to this email. Thank you for being a Listify"
            + " user and best of luck in your other endeavours!"
            + "<p>"
            + "<p> - The Listify Team";
    static final String TEXTBODY = "This email is to confirm your Listify account has been deleted. If you have any questions"
                        + " or did not request this action, please reply to this email. Thank you for being a Listify"
                        + " user and best of luck in your other endeavours! - The Listify Team";

    private final String GET_LISTS = "SELECT * FROM List WHERE (owner = ?);";
    private final String DELETE_LIST_PRODUCT = "DELETE FROM ListProduct WHERE (listID = ?);";
    private final String DELETE_LISTS = "DELETE FROM List WHERE (owner = ?);";
    private final String DELETE_LIST_SHARES = "DELETE FROM ListSharee WHERE (listID = ?);";
    private final String DELETE_LIST_ACCESS = "DELETE FROM ListSharee WHERE (userID = ?);";
    private final String DELETE_PROFILE_PICTURE = "DELETE FROM Pictures WHERE (userID = ?);";

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

        ListUsersRequest checkRequest = new ListUsersRequest().withUserPoolId(userPoolId);
        checkRequest.setFilter("sub=\"" + cognitoID +"\"");

        ListUsersResult foundUsersResult = awsCognitoIdentityProvider.listUsers(checkRequest);
        List<UserType> foundUsers = foundUsersResult.getUsers();
        if (foundUsers.size() != 1) {
            System.out.println(foundUsers);
            if (foundUsers.size() == 0) {
                throw new InputMismatchException("No user with given sub");
            }
            throw new InputMismatchException("Found more than one user with supposedly unique sub");
        }
        UserType foundUser = foundUsers.get(0);
        System.out.println(foundUser.getAttributes());
        String email = "";
        for (AttributeType attribute : foundUser.getAttributes()) {
            if (attribute.getName().equals("email")) {
                email = attribute.getValue();
                break;
            }
            System.out.println(attribute.getName() + ": " + attribute.getValue());
        }

        AdminUserGlobalSignOutRequest adminUserGlobalSignOutRequest = new AdminUserGlobalSignOutRequest().withUserPoolId(userPoolId);
        adminUserGlobalSignOutRequest.setUsername(cognitoID);
        System.out.println(adminUserGlobalSignOutRequest);
        awsCognitoIdentityProvider.adminUserGlobalSignOut(adminUserGlobalSignOutRequest);
        AdminDeleteUserRequest adminDeleteUserRequest = new AdminDeleteUserRequest().withUserPoolId(userPoolId);
        adminDeleteUserRequest.setUsername(cognitoID);
        System.out.println(adminDeleteUserRequest);
        awsCognitoIdentityProvider.adminDeleteUser(adminDeleteUserRequest);




        AmazonSimpleEmailService client =
                AmazonSimpleEmailServiceClientBuilder.standard()
                        .withRegion(Regions.US_EAST_2).build();
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(
                        new Destination().withToAddresses(email))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset("UTF-8").withData(HTMLBODY))
                                .withText(new Content()
                                        .withCharset("UTF-8").withData(TEXTBODY)))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData(DELETION_EMAIL_SUBJECT)))
                .withSource(DELETION_EMAIL_FROM);
        client.sendEmail(request);

        PreparedStatement statement = connection.prepareStatement(GET_LISTS);
        statement.setString(1, cognitoID);
        System.out.println(statement);
        ResultSet userLists = statement.executeQuery();
        while (userLists.next()) {
            int listID = userLists.getInt("listID");

            statement = connection.prepareStatement(DELETE_LIST_PRODUCT);
            statement.setInt(1, listID);
            System.out.println(statement);
            statement.executeUpdate();

            statement = connection.prepareStatement(DELETE_LIST_SHARES);
            statement.setInt(1, listID);
            System.out.println(statement);
            statement.executeUpdate();
        }

        statement = connection.prepareStatement(DELETE_LISTS);
        statement.setString(1, cognitoID);
        System.out.println(statement);
        statement.executeUpdate();
        statement = connection.prepareStatement(DELETE_LIST_ACCESS);
        statement.setString(1, cognitoID);
        System.out.println(statement);
        statement.executeUpdate();

        statement = connection.prepareStatement(DELETE_PROFILE_PICTURE);
        statement.setString(1, cognitoID);
        System.out.println(statement);
        statement.executeUpdate();

        connection.commit();

        return null;
    }
}
