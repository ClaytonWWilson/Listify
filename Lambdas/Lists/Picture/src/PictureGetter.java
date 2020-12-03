import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PictureGetter implements CallHandler {
    private final Connection connection;
    private final String cognitoID;

    private final String GET_ITEM = "SELECT * FROM Pictures WHERE cognitoID = ?;";

    public PictureGetter(Connection connection, String cognitoID) {
        this.connection = connection;
        this.cognitoID = cognitoID;
    }

    @Override
    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryMap, String cognitoID) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(GET_ITEM);
        if (!queryMap.containsKey("id")) {
            throw new IllegalArgumentException("Must have id set.");
        }
        if (queryMap.get("id").equals("profile")) {
            statement.setString(1, cognitoID);
        } else {
            statement.setString(1, queryMap.get("id"));
        }
        System.out.println(statement);
        ResultSet queryResults = statement.executeQuery();
        queryResults.first();
        System.out.println(queryResults);
        Picture retrievedPicture = new Picture(queryResults);
//        System.out.println(retrievedPicture);
        return retrievedPicture;
    }
}