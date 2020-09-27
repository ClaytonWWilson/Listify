import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class ListAdder {

    private DBConnector connector;
    private String cognitoID;

    private final String LIST_CREATE = "INSERT INTO Lists (Name, Owner) VALUES (?, ?)";

    ListAdder(DBConnector connector, String cognitoID) {
        this.connector = connector;
        this.cognitoID = cognitoID;
    }

    public void add(Map<String, Object> bodyMap) throws SQLException {
        Connection connection = connector.getConnection();
        PreparedStatement statement = connection.prepareStatement(LIST_CREATE);
        statement.setString(1, bodyMap.get("name").toString());//Needs safe checking
        statement.setString(2, cognitoID);
        System.out.println(statement);
        statement.executeUpdate();
        connection.commit();
    }
}
