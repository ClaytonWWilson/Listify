import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class ListPOST implements RequestHandler<Map<String,Object>, String>{


    public String handleRequest(Map<String, Object> inputMap, Context unfilled) {
        String cognitoID = InputUtils.getCognitoIDFromBody(inputMap);
        try {
            DBConnector connector = new DBConnector();
            try {
                new ListAdder(connector, cognitoID).add(InputUtils.getBody(inputMap));
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                connector.close();
            }
        } catch (IOException|SQLException|ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return null;
    }
}
