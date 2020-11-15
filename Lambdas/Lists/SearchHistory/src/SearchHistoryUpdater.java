import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SearchHistoryUpdater implements CallHandler {

    private Connection connection;
    private String cognitoID;

    public SearchHistoryUpdater(Connection connection, String cognitoID) {
        this.connection = connection;
        this.cognitoID = cognitoID;
    }

    final private String UPDATE_HISTORY = "INSERT INTO SearchHistory(userID, search) VALUES(?, ?);";

    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryString, String cognitoID) throws SQLException {
        PreparedStatement store_history = connection.prepareStatement(UPDATE_HISTORY);
        store_history.setString(1, cognitoID);
        store_history.setObject(2, bodyMap.get("searchTerm"));
        System.out.println(store_history);
        store_history.executeUpdate();
        connection.commit();
        return null;
    }
}