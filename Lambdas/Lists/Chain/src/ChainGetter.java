import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChainGetter implements CallHandler {
    private final Connection connection;

    private final String GET_CHAIN = "SELECT * FROM Chain WHERE chainID = ?;";
    private final String GET_CHAINS = "SELECT chainID FROM Chain;";

    public ChainGetter(Connection connection, String cognitoID) {
        this.connection = connection;
    }

    @Override
    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryMap, String cognitoID) throws SQLException {
        Integer id = (Integer) bodyMap.get("id");
        if (id == -1) {
            PreparedStatement getChains = connection.prepareStatement(GET_CHAINS);
            System.out.println(getChains);
            ResultSet getChainsResults = getChains.executeQuery();
            System.out.println(getChainsResults);
            ArrayList<Integer> chainIDs = new ArrayList<>();
            while (getChainsResults.next()) {
                chainIDs.add(getChainsResults.getInt("chainID"));
            }
            return chainIDs;
        }

        PreparedStatement statement = connection.prepareStatement(GET_CHAIN);
        statement.setInt(1, id);
        System.out.println(statement);
        ResultSet queryResults = statement.executeQuery();
        queryResults.first();
        System.out.println(queryResults);
        Chain retrievedChain = new Chain(queryResults);
        System.out.println(retrievedChain);
        return retrievedChain;
    }
}