import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CallListener implements CallHandler {

    static int calls = 0;

    static int creates = 0;

    public CallListener(DBConnector connector, String cognitoID) {
        creates++;
    }

    @Override
    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryString, String cognitoID) throws SQLException {
        calls++;
        return null;
    }

    public static int getCalls() {
        return calls;
    }

    public static void clear() {
        creates = 0;
        calls = 0;
    }

    public static int getCreates() {
        return creates;
    }

}