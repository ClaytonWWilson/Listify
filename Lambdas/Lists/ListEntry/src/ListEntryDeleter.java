import java.security.AccessControlException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ListEntryDeleter implements CallHandler {

    private Connection connection;
    private String cognitoID;

    private final String REMOVE_FROM_LIST = "DELETE FROM ListProduct WHERE (ProductID = ? AND ListID = ?);";
    private final String ACCESS_CHECK = "SELECT * from ListSharee WHERE userID = ? and listID = ?;";

    public ListEntryDeleter(Connection connection, String cognitoID) {
        this.connection = connection;
        this.cognitoID = cognitoID;
    }

    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryString, String cognitoID) throws SQLException {
        Integer productID = (Integer) bodyMap.get("productID");
        Integer listID = (Integer) bodyMap.get("listID");

        PreparedStatement accessCheck = connection.prepareStatement(ACCESS_CHECK);
        accessCheck.setString(1, cognitoID);
        accessCheck.setInt(2, listID);
        ResultSet access = accessCheck.executeQuery();
        if (access.next()) {
            if (!ListPermissions.hasPermission(access.getInt("permissionLevel"), "Write")) {
                throw new AccessControlException("User " + cognitoID + " does not have write permissions for list " + listID);
            }
        } else {
            throw new AccessControlException("User " + cognitoID + " does not have any permissions to access list " + listID);
        }


        PreparedStatement statement = connection.prepareStatement(REMOVE_FROM_LIST);
        statement.setInt(1, productID);
        statement.setInt(2, listID);

        System.out.println(statement);
        statement.executeUpdate();
        connection.commit();
        return null;
    }
}
