import java.sql.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ListAdder implements CallHandler {

    private Connection connection;
    private String cognitoID;

    private final String LIST_CREATE = "INSERT INTO List (name, owner, lastUpdated) VALUES (?, ?, ?);";
    private final String LIST_ACCESS_GRANT = "INSERT INTO ListSharee(listID, userID) VALUES(?, ?);";

    public ListAdder(Connection connection, String cognitoID) {
        this.connection = connection;
        this.cognitoID = cognitoID;
    }

    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryString, String cognitoID) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(LIST_CREATE, Statement.RETURN_GENERATED_KEYS);

        String listName = bodyMap.get("name").toString();//Needs safe checking
        statement.setString(1, listName);
        statement.setString(2, cognitoID);
        statement.setTimestamp(3, Timestamp.from(Instant.now()));
        System.out.println(statement);
        statement.executeUpdate();
        ResultSet newIDRS = statement.getGeneratedKeys();
        newIDRS.first();
        Integer newID = newIDRS.getInt(1);
        PreparedStatement accessGrant = connection.prepareStatement(LIST_ACCESS_GRANT);
        accessGrant.setInt(1, newID);
        accessGrant.setString(2, cognitoID);
        System.out.println(accessGrant);
        accessGrant.executeUpdate();
        connection.commit();
        System.out.println(newID);
        return newID;
    }
}
