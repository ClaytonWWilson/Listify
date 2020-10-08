import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ItemGetter implements CallHandler{
    private final DBConnector connector;

    private final String GET_ITEM = "SELECT * FROM Product WHERE productID = ?;";

    public ItemGetter(DBConnector connector, String cognitoID) {
        this.connector = connector;
    }

    @Override
    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryMap, String cognitoID) throws SQLException {
        Connection connection = connector.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(GET_ITEM);
            statement.setInt(1, Integer.parseInt(queryMap.get("id")));
            System.out.println(statement);
            ResultSet queryResults = statement.executeQuery();
            queryResults.first();
            System.out.println(queryResults);
            Item retrievedItem = new Item(queryResults);
            System.out.println(retrievedItem);
            return retrievedItem;
        } finally {
            connection.close();
        }
    }
}
