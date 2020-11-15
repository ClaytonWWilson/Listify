import java.security.AccessControlException;
import java.sql.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ListPutter implements CallHandler {
    private final Connection connection;
    private final String cognitoID;

    private final String ACCESS_CHECK = "SELECT * from ListSharee WHERE userID = ? and listID = ?;";
    private final String LIST_RENAME = "UPDATE List SET name = ?, lastUpdated = ? WHERE listID = ?;";

    public ListPutter(Connection connection, String cognitoID) {
        this.connection = connection;
        this.cognitoID = cognitoID;
    }

    @Override
    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryMap, String cognitoID) throws SQLException {
        Integer listID = Integer.parseInt(bodyMap.get("listID").toString());

        PreparedStatement accessCheck = connection.prepareStatement(ACCESS_CHECK);
        accessCheck.setString(1, cognitoID);
        accessCheck.setInt(2, listID);
        System.out.println(accessCheck);
        ResultSet userLists = accessCheck.executeQuery();
        if (!userLists.next()) {
            throw new AccessControlException("User does not have access to list");
        } else {
            if (!ListPermissions.hasPermission(userLists.getInt("permissionLevel"), "Delete")) {
                throw new AccessControlException("User " + cognitoID + " does not have permission to edit list " + listID);
            }
        }
        PreparedStatement renameList = connection.prepareStatement(LIST_RENAME);
        renameList.setString(1, bodyMap.get("name").toString());
        renameList.setTimestamp(2, Timestamp.from(Instant.now()));
        renameList.setInt(3, listID);
        System.out.println(renameList);
        renameList.executeUpdate();
        connection.commit();
        return null;
    }
}
