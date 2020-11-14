import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SearchHistoryPutter implements CallHandler {

    private Connection connection;
    private String cognitoID;

    public SearchHistoryPutter(Connection connection, String cognitoID) {
        this.connection = connection;
        this.cognitoID = cognitoID;
    }

    final private String STORE_HISTORY = "REPLACE INTO SearchHistory(userID, historyObject) VALUES(?, ?);";

    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryString, String cognitoID) throws SQLException {
        final String searchHistoryJson = new Gson().toJson(bodyMap);
        System.out.println(searchHistoryJson);
        SearchHistory toStore = new Gson().fromJson(searchHistoryJson, SearchHistory.class);
        System.out.println(toStore);
        PreparedStatement store_history = connection.prepareStatement(STORE_HISTORY);
        store_history.setString(1, cognitoID);
        store_history.setObject(2, toStore.searches);
        System.out.println(store_history);
        store_history.executeUpdate();
        connection.commit();
        return null;
    }
}