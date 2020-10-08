import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

public class ListEntryAdder implements CallHandler {

    private DBConnector connector;
    private String cognitoID;

    private final String ITEM_TO_LIST = "INSERT INTO ListProduct (productID, listID, quantity, addedDate, purchased) VALUES (?, ?, ?, ?, ?)";

    public ListEntryAdder(DBConnector connector, String cognitoID) {
        this.connector = connector;
        this.cognitoID = cognitoID;
    }

    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryString, String cognitoID) throws SQLException {
        Connection connection = connector.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(ITEM_TO_LIST);
            statement.setInt(1, (Integer) bodyMap.get("productID"));
            statement.setInt(2, (Integer) bodyMap.get("listID"));
            statement.setInt(3, (Integer) bodyMap.get("quantity"));
            statement.setTimestamp(4, Timestamp.from(Instant.now()));
            statement.setBoolean(5, (Boolean) bodyMap.get("purchased"));
            System.out.println(statement);
            statement.executeUpdate();
            connection.commit();
        } finally {
            connection.close();
        }
        return null;
    }
}
