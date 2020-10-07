import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputUtils {
    public static String getCognitoIDFromBody(Map<String, Object> inputMap) {
        System.out.println(inputMap.keySet());
        System.out.println(inputMap.entrySet());
        Map<String, Object> contextMap = getMap(inputMap, "context");
        System.out.println(inputMap.get("context"));
        System.out.println(contextMap.get("sub"));
        return contextMap.get("sub").toString();
    }

    public static Map<String, Object> getBody(Map<String, Object> inputMap) {
        return getMap(inputMap, "body");
    }

    public static HashMap<String, String> getQueryParams(Map<String, Object> inputMap) {
        return (HashMap<String, String>) (getMap(inputMap, "params").get("querystring"));
    }



    public static Map<String, Object> getMap(Map<String, Object> parentMap, String childKey) {
        if ((parentMap.get(childKey) != null) && (parentMap.get(childKey) instanceof Map<?, ?>)) {
            return ((Map<String, Object>) parentMap.get(childKey));
        }
        throw new IllegalArgumentException("The key \"" + childKey + "\" must exist and be a map");
    }
}
