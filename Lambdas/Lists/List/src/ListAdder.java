import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

public class ListAdder implements CallHandler {

    private DBConnector connector;
    private String cognitoID;

    private final String LIST_CREATE = "INSERT INTO List (name, owner, lastUpdated) VALUES (?, ?, ?)";

    public ListAdder(DBConnector connector, String cognitoID) {
        this.connector = connector;
        this.cognitoID = cognitoID;
    }

    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryString, String cognitoID) throws SQLException {
        Connection connection = connector.getConnection();
        PreparedStatement statement = connection.prepareStatement(LIST_CREATE);
        statement.setString(1, bodyMap.get("name").toString());//Needs safe checking
        statement.setString(2, cognitoID);
        statement.setObject(3, Instant.now().atZone(ZoneOffset.UTC).toLocalDateTime());
        System.out.println(statement);
        statement.executeUpdate();
        connection.commit();
        return null;
    }
}
