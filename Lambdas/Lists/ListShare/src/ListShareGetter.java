import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;

public class ListShareGetter implements CallHandler{
    private final Connection connection;
    private final String cognitoID;

    private final String GET_LISTS = "SELECT * FROM ListSharee WHERE listID = ?;";

    public ListShareGetter(Connection connection, String cognitoID) {
        this.connection = connection;
        this.cognitoID = cognitoID;
    }

    @Override
    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryMap, String cognitoID) throws SQLException {
        Integer listID = Integer.parseInt(queryMap.get("id"));

        PreparedStatement getList = connection.prepareStatement(GET_LISTS);
        getList.setInt(1, listID);

        ResultSet getListResults = getList.executeQuery();
        System.out.println(getListResults);

        ListShare first = null;
        while (first == null && getListResults.next()) {
            InvokeRequest invokeRequest = new InvokeRequest();
            invokeRequest.setFunctionName("UserGET");
            invokeRequest.setPayload("{" +
                    "  \"body\": {" +
                    "  }," +
                    "  \"params\": {" +
                    "      \"querystring\": {" +
                    "           \"id\": \"" + getListResults.getString("userID") + "\"" +
                    "      }" +
                    "  }," +
                    "  \"context\": {" +
                    "    \"sub\": \"not used\"" +
                    "  }" +
                    "}");
            InvokeResult invokeResult = AWSLambdaClientBuilder.defaultClient().invoke(invokeRequest);
            if (invokeResult.getStatusCode() != 200) {
                throw new InputMismatchException("Could not find specified user to share with");
            }
            String shareWithEmail = new Gson().fromJson(new String(invokeResult.getPayload().array()), User.class).email;
            first = new ListShare(getListResults, shareWithEmail);
            if (first.permissionLevel == 0 || first.permissionLevel == 1) {
                first = null;
            }
        }

        //Insert the ListShare objects to hold the data of the remaining rows into first's ListShare list
        while (getListResults.next()) {
            InvokeRequest invokeRequest = new InvokeRequest();
            invokeRequest.setFunctionName("UserGET");
            invokeRequest.setPayload("{" +
                    "  \"body\": {" +
                    "  }," +
                    "  \"params\": {" +
                    "      \"querystring\": {" +
                    "           \"id\": \"" + getListResults.getString("userID") + "\"" +
                    "      }" +
                    "  }," +
                    "  \"context\": {" +
                    "    \"sub\": \"not used\"" +
                    "  }" +
                    "}");
            InvokeResult invokeResult = AWSLambdaClientBuilder.defaultClient().invoke(invokeRequest);
            if (invokeResult.getStatusCode() != 200) {
                throw new InputMismatchException("Could not find specified user to share with");
            }
            String shareWithEmail = new Gson().fromJson(new String(invokeResult.getPayload().array()), User.class).email;
            ListShare newShare = new ListShare(getListResults, shareWithEmail);
            System.out.println(newShare);
            if (newShare.permissionLevel != 0 && newShare.permissionLevel != 1) {
                first.addtoList(newShare);
            }
        }

        return first;
    }
}
