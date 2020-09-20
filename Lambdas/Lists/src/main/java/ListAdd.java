import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class ListAdd implements RequestHandler<Map<String,Object>, String>{


    public String handleRequest(Map<String, Object> inputMap, Context unfilled) {
        System.out.println(inputMap.keySet());
        System.out.println(inputMap.entrySet());
        Map<String, Object> contextMap;
        if ((inputMap.get("context") != null) && (inputMap.get("context") instanceof Map<?, ?>)) {
            contextMap = ((Map<String, Object>) inputMap.get("context"));
        } else {
            throw new IllegalArgumentException("The key \"Context\" must exist and be a map");
        }
        System.out.println(inputMap.get("context"));
        System.out.println(contextMap.get("sub"));
        return null;
    }
}
