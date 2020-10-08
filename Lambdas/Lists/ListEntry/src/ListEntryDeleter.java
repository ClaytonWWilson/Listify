import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ListEntryDeleter implements CallHandler {

    private Connection connection;
    private String cognitoID;

    private final String REMOVE_FROM_LIST = "DELETE FROM ListProduct WHERE (ProductID = ? AND ListID = ?);";

    public ListEntryDeleter(Connection connection, String cognitoID) {
        this.connection = connection;
        this.cognitoID = cognitoID;
    }

    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryString, String cognitoID) throws SQLException {
        try {
            PreparedStatement statement = connection.prepareStatement(REMOVE_FROM_LIST);
            statement.setInt(1, (Integer) bodyMap.get("ProductID"));
            statement.setInt(2, (Integer) bodyMap.get("ListID"));
            System.out.println(statement);
            statement.executeUpdate();
            connection.commit();
        } finally {
            connection.close();
        }
        return null;
    }
}
