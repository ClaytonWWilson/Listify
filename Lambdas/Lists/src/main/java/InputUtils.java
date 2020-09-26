import java.util.Map;

public class InputUtils {
    public static String getCognitoIDFromBody(Map<String, Object> inputMap) {
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
    }
}
