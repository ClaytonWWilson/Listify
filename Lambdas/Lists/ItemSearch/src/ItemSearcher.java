import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ItemSearcher implements CallHandler {

    Connection connection;
    String cognitoID;

    private final String GET_ITEM_MATCHES = "SELECT * FROM Product WHERE description LIKE ? LIMIT 100;";

    public ItemSearcher(Connection connection, String cognitoID) {
        this.connection = connection;
        this.cognitoID = cognitoID;
    }

    @Override
    public Object conductAction(Map<String, Object> body, HashMap<String, String> queryParams, String cognitoID) throws SQLException {
        PreparedStatement getItemMatches = connection.prepareStatement(GET_ITEM_MATCHES);
        getItemMatches.setString(1, "%" + queryParams.get("id") + "%");
        System.out.println(getItemMatches);
        ResultSet searchResults = getItemMatches.executeQuery();
        ItemSearch searchResultsObject = new ItemSearch(searchResults);
        System.out.println(searchResultsObject);
        InvokeRequest invokeRequest = new InvokeRequest();
        invokeRequest.setFunctionName("SearchHistoryUpdate");
        invokeRequest.setPayload("{" +
                "  \"newSearch\": \"" + queryParams.get("id") + "\"," +
                "  \"cognitoID\": \""+ cognitoID + "\"" +
                "}");
        invokeRequest.setInvocationType("Event");
        System.out.println(invokeRequest);
        AWSLambdaClientBuilder.defaultClient().invoke(invokeRequest);
        return searchResultsObject;
    }
}
