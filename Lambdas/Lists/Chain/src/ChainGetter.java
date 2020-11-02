import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ChainGetter implements CallHandler {
    private final Connection connection;

    private final String GET_CHAIN = "SELECT * FROM Chain WHERE chainID = ?;";

    public ChainGetter(Connection connection, String cognitoID) {
        this.connection = connection;
    }

    @Override
    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryMap, String cognitoID) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(GET_CHAIN);
        statement.setInt(1, Integer.parseInt(queryMap.get("id")));
        System.out.println(statement);
        ResultSet queryResults = statement.executeQuery();
        queryResults.first();
        System.out.println(queryResults);
        Chain retrievedChain = new Chain(queryResults);
        System.out.println(retrievedChain);
        return retrievedChain;
    }
}