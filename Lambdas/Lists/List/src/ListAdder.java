import java.sql.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ListAdder implements CallHandler {

    private Connection connection;
    private String cognitoID;

    private final String LIST_CREATE = "INSERT INTO List (name, owner, lastUpdated) VALUES (?, ?, ?);";
    private final String LIST_ACCESS_GRANT = "INSERT INTO ListSharee(listID, userID, permissionLevel, uiPosition) VALUES(?, ?, ?, ?);";
    private final String UI_POSITION_CHECK = "SELECT Max(uiPosition) as maxUIPosition FROM ListSharee WHERE userID = ?;";

    public ListAdder(Connection connection, String cognitoID) {
        this.connection = connection;
        this.cognitoID = cognitoID;
    }

    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryString, String cognitoID) throws SQLException {
        String listName = bodyMap.get("name").toString();//Needs safe checking

        PreparedStatement uiPositionCheck = connection.prepareStatement(UI_POSITION_CHECK);
        uiPositionCheck.setString(1, cognitoID);
        ResultSet uiPositionCheckRS = uiPositionCheck.executeQuery();
        int nextPosition = 1;
        if (uiPositionCheckRS.next()) {
            nextPosition = uiPositionCheckRS.getInt("maxUIPosition") + 1;
        }

        PreparedStatement statement = connection.prepareStatement(LIST_CREATE, Statement.RETURN_GENERATED_KEYS);
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
        accessGrant.setInt(3, ListPermissions.getAll());
        accessGrant.setInt(4, nextPosition);
        System.out.println(accessGrant);
        accessGrant.executeUpdate();
        connection.commit();
        System.out.println(newID);
        return newID;
    }
}
