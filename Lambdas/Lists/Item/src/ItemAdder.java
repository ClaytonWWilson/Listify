import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class ItemAdder implements CallHandler {

    private DBConnector connector;
    private String cognitoID;

    private final String LIST_CREATE = "INSERT INTO Items (Name) VALUES (?)";

    public ItemAdder(DBConnector connector, String cognitoID) {
        this.connector = connector;
        this.cognitoID = cognitoID;
    }

    public String conductAction(Map<String, Object> bodyMap, String queryString) throws SQLException {
        Connection connection = connector.getConnection();
        PreparedStatement statement = connection.prepareStatement(LIST_CREATE);
        statement.setString(1, bodyMap.get("name").toString());//Needs safe checking
        System.out.println(statement);
        statement.executeUpdate();
        connection.commit();
        return null;
    }
}
