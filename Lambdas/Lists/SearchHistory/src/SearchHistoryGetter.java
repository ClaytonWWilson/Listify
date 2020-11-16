import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SearchHistoryGetter implements CallHandler {

    private Connection connection;
    private String cognitoID;

    public SearchHistoryGetter(Connection connection, String cognitoID) {
        this.connection = connection;
        this.cognitoID = cognitoID;
    }

    final private String SELECT_HISTORY = "SELECT * from SearchHistory WHERE userID = ?;";

    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryString, String cognitoID) throws SQLException {
        PreparedStatement select_history = connection.prepareStatement(SELECT_HISTORY);
        select_history.setString(1, cognitoID);
        System.out.println(select_history);
        ResultSet searchHistory = select_history.executeQuery();
        if (!searchHistory.first()) {
            return new SearchHistory();
        }
        System.out.println(new SearchHistory(searchHistory));
        return new SearchHistory(searchHistory);
    }
}