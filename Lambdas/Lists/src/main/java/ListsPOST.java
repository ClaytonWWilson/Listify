import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class ListsPOST implements RequestHandler<Map<String,Object>, String>{


    public String handleRequest(Map<String, Object> inputMap, Context unfilled) {
        String cognitoID = InputUtils.getCognitoIDFromBody(inputMap);
        try {
            System.out.println(new DBConnector());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
