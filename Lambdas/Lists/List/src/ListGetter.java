import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListGetter implements CallHandler{
    private final DBConnector connector;
    private final String cognitoID;

    private final String GET_LIST = "SELECT * FROM List WHERE listID = ?;";
    private final String GET_LISTS = "SELECT listID FROM List WHERE owner = ?;";
    private final String GET_ENTRIES = "SELECT * FROM ListProduct WHERE listID = ?;";

    public ListGetter(DBConnector connector, String cognitoID) {
        this.connector = connector;
        this.cognitoID = cognitoID;
    }

    @Override
    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryMap, String cognitoID) throws SQLException {
        Connection connection = connector.getConnection();
        Integer id = Integer.parseInt(queryMap.get("id"));
        try {
            if (id == -1) {
                PreparedStatement getLists = connection.prepareStatement(GET_LISTS);
                getLists.setString(1, cognitoID);
                ResultSet getListsResults = getLists.executeQuery();
                ArrayList<Integer> listIds = new ArrayList<>();
                while (getListsResults.next()) {
                    listIds.add(getListsResults.getInt(1));
                }
                return listIds;
            }
            PreparedStatement getList = connection.prepareStatement(GET_LIST);
            getList.setInt(1, id);
            System.out.println(getList);
            ResultSet getListResults = getList.executeQuery();
            getListResults.first();
            System.out.println(getListResults);
            List retrievedList = new List(getListResults);
            System.out.println(retrievedList);
            PreparedStatement getListEntries = connection.prepareStatement(GET_ENTRIES);
            getListEntries.setInt(1, id);
            ResultSet getEntryResults = getListEntries.executeQuery();
            while (getEntryResults.next()) {
                retrievedList.addItemEntry(new ItemEntry(getEntryResults));
            }
            return retrievedList;
        } finally {
            connection.close();
        }
    }
}
