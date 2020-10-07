
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TestInputUtils {

    @Test
    public void testGetBody() {
        Map<String, Object> testMap = new HashMap<>();
        Map<String, String> bodyMap = addBody(testMap);
        assert (InputUtils.getBody(testMap).equals(bodyMap));
    }

    @Test
    public void testGetQueryParams() {
        Map<String, Object> testMap = new HashMap<>();
        Map<String, String> queryMap = addQueryParams(testMap);
        assert (InputUtils.getQueryParams(testMap).equals(queryMap));
    }

    @Test
    public void testGetCognitoID() {
        Map<String, Object> testMap = new HashMap<>();
        String cognitoID = addCognitoID(testMap);
        assert (InputUtils.getCognitoIDFromBody(testMap).equals(cognitoID));
    }

    @Test
    public void testGetBadMapKey() {
        Map<String, Object> testMap = new HashMap<>();
        try {
            InputUtils.getMap(testMap, "missing");
            assert (false);
        } catch (IllegalArgumentException e) {

        }
    }

    public static  String addCognitoID(Map<String, Object> testMap) {
        HashMap<String, String> contextMap = new HashMap<>();
        String cognitoID = "Example id";
        contextMap.put("sub", cognitoID);
        testMap.put("context", contextMap);
        return cognitoID;
    }

    public static Map<String, String> addQueryParams(Map<String, Object> testMap) {
        HashMap<String, Object> paramsMap = new HashMap<>();
        HashMap<String, String> queryMap = new HashMap<>();
        paramsMap.put("querystring", queryMap);
        testMap.put("params", paramsMap);
        return queryMap;
    }

    public static Map<String, String> addBody(Map<String, Object> testMap) {
        HashMap<String, String> bodyMap = new HashMap<>();
        testMap.put("body", bodyMap);
        return bodyMap;
    }
}
