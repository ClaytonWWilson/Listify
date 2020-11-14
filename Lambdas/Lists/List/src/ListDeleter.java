import java.security.AccessControlException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ListDeleter implements CallHandler {
    private final Connection connection;
    private final String cognitoID;

    private final String ACCESS_CHECK = "SELECT * from ListSharee WHERE userID = ? and listID = ?;";
    private final String DELETE_LIST = "DELETE FROM List WHERE listID = ?;";
    private final String DELETE_REQUESTOR_ACCESS = "DELETE FROM ListSharee where listID = ? AND userID = ?;";
    private final String DELETE_LIST_ACCESS = "DELETE FROM ListSharee where listID = ?;";
    private final String DELETE_LIST_ENTRIES = "DELETE FROM ListProduct where listID = ?;";

    public ListDeleter(Connection connection, String cognitoID) {
        this.connection = connection;
        this.cognitoID = cognitoID;
    }

    @Override
    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryMap, String cognitoID) throws SQLException {
        Integer listID = Integer.parseInt(queryMap.get("id"));

        PreparedStatement accessCheck = connection.prepareStatement(ACCESS_CHECK);
        accessCheck.setString(1, cognitoID);
        accessCheck.setInt(2, listID);
        System.out.println(accessCheck);
        ResultSet userLists = accessCheck.executeQuery();
        if (!userLists.next()) {
            throw new AccessControlException("User does not have access to list");
        } else {
            Integer permissionLevel = userLists.getInt("permissionLevel");
            if (!ListPermissions.hasPermission(permissionLevel, "Delete")) {
                PreparedStatement cleanRequestorAccess = connection.prepareStatement(DELETE_REQUESTOR_ACCESS);
                cleanRequestorAccess.setInt(1, listID);
                cleanRequestorAccess.setString(2, cognitoID);
                System.out.println(cleanRequestorAccess);
                cleanRequestorAccess.executeUpdate();
                return null;
            }
        }
        PreparedStatement cleanAccess = connection.prepareStatement(DELETE_LIST_ACCESS);
        cleanAccess.setInt(1, listID);
        System.out.println(cleanAccess);
        cleanAccess.executeUpdate();
        PreparedStatement deleteEntries = connection.prepareStatement(DELETE_LIST_ENTRIES);
        deleteEntries.setInt(1, listID);
        System.out.println(deleteEntries);
        deleteEntries.executeUpdate();
        PreparedStatement cleanList = connection.prepareStatement(DELETE_LIST);
        cleanList.setInt(1, listID);
        System.out.println(cleanList);
        cleanList.executeUpdate();
        connection.commit();
        return null;
    }
}
