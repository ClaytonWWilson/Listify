import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ItemGetter implements CallHandler {
    private final Connection connection;

    private final String GET_ITEM = "SELECT * FROM Product WHERE productID = ?;";

    public ItemGetter(Connection connection, String cognitoID) {
        this.connection = connection;
    }

    @Override
    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryMap, String cognitoID) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(GET_ITEM);
        statement.setInt(1, Integer.parseInt(queryMap.get("id")));
        System.out.println(statement);
        ResultSet queryResults = statement.executeQuery();
        queryResults.first();
        System.out.println(queryResults);
        Item retrievedItem = new Item(queryResults);
        System.out.println(retrievedItem);
        return retrievedItem;
    }
}
