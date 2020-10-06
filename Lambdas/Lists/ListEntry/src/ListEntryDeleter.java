import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ListEntryDeleter implements CallHandler {

    private DBConnector connector;
    private String cognitoID;

    private final String REMOVE_FROM_LIST = "DELETE FROM ListProduct WHERE (ProductID = ? AND ListID = ?);";

    public ListEntryDeleter(DBConnector connector, String cognitoID) {
        this.connector = connector;
        this.cognitoID = cognitoID;
    }

    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryString, String cognitoID) throws SQLException {
        Connection connection = connector.getConnection();
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
