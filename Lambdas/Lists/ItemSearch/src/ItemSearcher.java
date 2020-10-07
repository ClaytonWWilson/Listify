import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ItemSearcher implements CallHandler {

    DBConnector connector;
    String cognitoID;

    private final String GET_ITEM_MATCHES = "SELECT * FROM Product WHERE description LIKE ?";

    public ItemSearcher(DBConnector connector, String cognitoID) {
        this.connector = connector;
        this.cognitoID = cognitoID;
    }

    @Override
    public Object conductAction(Map<String, Object> body, HashMap<String, String> queryParams, String s) throws SQLException {
        try (Connection connection = connector.getConnection()) {
            PreparedStatement getItemMatches = connection.prepareStatement(GET_ITEM_MATCHES);
            getItemMatches.setString(1, "%" + queryParams.get("id") + "%");
            System.out.println(getItemMatches);
            ResultSet searchResults = getItemMatches.executeQuery();
            ItemSearch searchResultsObject = new ItemSearch(searchResults);
            System.out.println(searchResultsObject);
            return searchResultsObject;
        }
    }
}
