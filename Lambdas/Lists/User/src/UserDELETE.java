import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;

public class UserDELETE implements RequestHandler<Map<String,Object>, Object> {

    public Object handleRequest(Map<String, Object> inputMap, Context unfilled) {
        return BasicHandler.handleRequest(inputMap, unfilled, UserDeleter.class);
    }
}