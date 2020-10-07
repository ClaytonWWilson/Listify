import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class BadCallListener extends CallListener {

    static SQLException toThrowSQL = null;
    static RuntimeException toThrowRuntime = null;


    public BadCallListener(DBConnector connector, String cognitoID) {
        super(connector, cognitoID);
    }

    @Override
    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryString, String cognitoID) throws SQLException {
        if (toThrowSQL != null) {
            throw toThrowSQL;
        }
        throw toThrowRuntime;
    }

    public static void setRuntimeException(RuntimeException newException) {
        toThrowRuntime = newException;
        toThrowSQL = null;
    }

    public static void setSQLException(SQLException newException) {
        toThrowRuntime = null;
        toThrowSQL = newException;
    }

}
