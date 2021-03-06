import java.security.AccessControlException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListGetter implements CallHandler{
    private final Connection connection;
    private final String cognitoID;

    private final String GET_LIST = "SELECT * FROM List WHERE listID = ?;";
    private final String GET_LISTS = "SELECT listID, permissionLevel FROM ListSharee WHERE userID = ? ORDER BY uiPosition;";
    private final String SHARE_CHECK = "SELECT * FROM ListSharee WHERE listID = ?;";
    private final String GET_ENTRIES = "SELECT * FROM ListProduct WHERE listID = ?;";

    public ListGetter(Connection connection, String cognitoID) {
        this.connection = connection;
        this.cognitoID = cognitoID;
    }

    @Override
    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryMap, String cognitoID) throws SQLException {
        Integer id = Integer.parseInt(queryMap.get("id"));
        if (id == -1) {
            PreparedStatement getLists = connection.prepareStatement(GET_LISTS);
            getLists.setString(1, cognitoID);
            System.out.println(getLists);
            ResultSet getListsResults = getLists.executeQuery();
            System.out.println(getListsResults);
            ArrayList<Integer> listIds = new ArrayList<>();
            while (getListsResults.next()) {
                Integer permissionLevel = getListsResults.getInt("permissionLevel");
                if (ListPermissions.hasPermission(permissionLevel, "Read")) {
                    listIds.add(getListsResults.getInt("listID"));
                }
            }
            return listIds;
        }
        PreparedStatement checkAccess = connection.prepareStatement(SHARE_CHECK);
        checkAccess.setInt(1, id);
        System.out.println(checkAccess);
        ResultSet accessResults = checkAccess.executeQuery();
        int sharees = 0;
        boolean verifiedAccess = false;
        int uiPosition = 1;
        while (accessResults.next() && (sharees < 2 || !verifiedAccess )) {
            int permissionLevel = accessResults.getInt("permissionLevel");
            if (accessResults.getString("userID").equals(cognitoID)) {
                verifiedAccess = true;
                if (!ListPermissions.hasPermission(permissionLevel, "Read")) {
                    throw new AccessControlException("User " + cognitoID + " does not have permission to read list " + id);
                }
                uiPosition = accessResults.getInt("uiPosition");
            }
            if (permissionLevel > 0) {
                sharees++;
            }
        }
        if (!verifiedAccess) {
            throw new AccessControlException("User " + cognitoID + " does not have ant permission for list " + id);
        }
        boolean shared = false;
        if (sharees > 1) {
            shared = true;
        }
        PreparedStatement getList = connection.prepareStatement(GET_LIST);
        getList.setInt(1, id);
        System.out.println(getList);
        ResultSet getListResults = getList.executeQuery();
        getListResults.first();
        System.out.println(getListResults);
        List retrievedList = new List(getListResults, shared, uiPosition);
        System.out.println(retrievedList);
        PreparedStatement getListEntries = connection.prepareStatement(GET_ENTRIES);
        getListEntries.setInt(1, id);
        ResultSet getEntryResults = getListEntries.executeQuery();
        while (getEntryResults.next()) {
            retrievedList.addItemEntry(new ItemEntry(id, getEntryResults));
        }
        System.out.println(retrievedList);
        return retrievedList;
    }
}
