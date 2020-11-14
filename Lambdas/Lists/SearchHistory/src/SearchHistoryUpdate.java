import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;

import java.util.InputMismatchException;
import java.util.Map;

public class SearchHistoryUpdate implements RequestHandler<Map<String,Object>, Object> {

    public Object handleRequest(Map<String, Object> inputMap, Context unfilled) {
        String cognitoID = (String) inputMap.get("cognitoID");
        System.out.println(cognitoID);
        String newSearch = (String) inputMap.get("newSearch");
        System.out.println(newSearch);
        InvokeRequest invokeRequest = new InvokeRequest();
        invokeRequest.setFunctionName("SearchHistoryGET");
        invokeRequest.setPayload("{" +
                "  \"body\": {" +
                "  }," +
                "  \"params\": {" +
                "      \"querystring\": {" +
                "      }" +
                "  }," +
                "  \"context\": {" +
                "    \"sub\": \""+ cognitoID + "\"" +
                "  }" +
                "}");
        System.out.println(invokeRequest);
        InvokeResult searchHistoryResult = AWSLambdaClientBuilder.defaultClient().invoke(invokeRequest);
        System.out.println(searchHistoryResult);
        if (searchHistoryResult.getStatusCode() != 200) {
            throw new InputMismatchException("Could not find a search history for the specified usr");
        }
        System.out.println(new String(searchHistoryResult.getPayload().array()));
        SearchHistory priorSearchHistory = new Gson().fromJson(new String(searchHistoryResult.getPayload().array()), SearchHistory.class);
        priorSearchHistory.addSearch(newSearch);
        invokeRequest.setFunctionName("SearchHistoryPUT");
        System.out.println("New search history: " + new Gson().toJson(priorSearchHistory));
        invokeRequest.setPayload("{" +
                "  \"body\":" + new Gson().toJson(priorSearchHistory) + "," +
                "  \"params\": {" +
                "      \"querystring\": {" +
                "      }" +
                "  }," +
                "  \"context\": {" +
                "    \"sub\": \""+ cognitoID + "\"" +
                "  }" +
                "}");
        invokeRequest.setInvocationType("Event");
        System.out.println(invokeRequest);
        AWSLambdaClientBuilder.defaultClient().invoke(invokeRequest);
        return null;    }
}
