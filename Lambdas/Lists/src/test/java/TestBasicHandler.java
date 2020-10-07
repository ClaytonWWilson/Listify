import org.junit.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TestBasicHandler {

    @Test
    public void testClassCreatedAndCalled() {
        BasicHandler.handleRequest(buildFullSampleMap(), null, CallListener.class);
        assert (CallListener.getCreates() == 1);
        assert (CallListener.getCalls() == 1);
        CallListener.clear();
    }

    @Test
    public void testBadCalls() {
        BadCallListener.setRuntimeException(new RuntimeException());
        try {
            BasicHandler.handleRequest(buildFullSampleMap(), null, BadCallListener.class);
            assert false;
        } catch (RuntimeException ignored) {

        }
        BadCallListener.setSQLException(new SQLException());
        Object handlerReturn = BasicHandler.handleRequest(buildFullSampleMap(), null, Hidden.class);
        assert (handlerReturn == null);
        BadCallListener.clear();
    }

    @Test
    public void testBadClass() {
        try {
            assert (BasicHandler.handleRequest(buildFullSampleMap(), null, Hidden.class) == null);
        } catch (Exception ignored) {
            assert false;
        }
    }

    public Map<String, Object> buildFullSampleMap() {
        Map<String, Object> testMap = new HashMap<>();
        TestInputUtils.addBody(testMap);
        TestInputUtils.addCognitoID(testMap);
        TestInputUtils.addQueryParams(testMap);
        return testMap;
    }
    private class Hidden implements CallHandler{

        @Override
        public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryString, String cognitoID) throws SQLException {
            return null;
        }
    }
}
