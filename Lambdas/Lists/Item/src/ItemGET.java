import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;

public class ItemGET implements RequestHandler<Map<String,Object>, String> {

    public String handleRequest(Map<String, Object> inputMap, Context unfilled) {
        return BasicHandler.handleRequest(inputMap, unfilled, ItemGetter.class);
    }
}
