import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public interface CallHandler {
    Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryString, String cognitoID) throws SQLException;
}
