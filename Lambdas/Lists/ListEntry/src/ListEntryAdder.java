import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

public class ListEntryAdder implements CallHandler {

    private Connection connection;
    private String cognitoID;

    private final String ITEM_TO_LIST = "INSERT INTO ListProduct (productID, listID, quantity, addedDate, purchased) VALUES (?, ?, ?, ?, ?)";

    public ListEntryAdder(Connection connection, String cognitoID) {
        this.connection = connection;
        this.cognitoID = cognitoID;
    }

    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryString, String cognitoID) throws SQLException {
        try {
            PreparedStatement statement = connection.prepareStatement(ITEM_TO_LIST);
            statement.setInt(1, (Integer) bodyMap.get("productID"));
            statement.setInt(2, (Integer) bodyMap.get("listID"));
            statement.setInt(3, (Integer) bodyMap.get("quantity"));
            statement.setObject(4, Instant.now().atZone(ZoneOffset.UTC).toLocalDateTime());
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
