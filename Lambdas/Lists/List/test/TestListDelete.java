import org.junit.Test;

import java.security.AccessControlException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestListDelete {


    @Test
    public void testListDeleterWOAccess() {
        testListDeleterCore(false, false);
    }

    @Test
    public void testListDeleterError() {
        testListDeleterCore(true, true);
    }

    public void testListDeleterCore(boolean shouldThrow, boolean hasAccess) {
        StatementInjector injector;
        ArrayList<Object> rsReturns = new ArrayList<>();
        rsReturns.add("cognitoID");
        try {
            if (!hasAccess) {
                rsReturns = null;
            }
            injector = new StatementInjector(null, rsReturns, shouldThrow);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            assert false; //Error in test infrastructure
            return;
        }
        ListDeleter listDeleter = new ListDeleter(injector, "cognitoID");
        Map<String, Object> body = (Map<String, Object>) TestBasicHandler.buildFullSampleMap().get("body");
        HashMap<String, String> queryParams = (HashMap<String, String>) TestBasicHandler.buildFullSampleMap().get("body");
        queryParams.put("id", "30");

        try {
            Object rawIDReturn = listDeleter.conductAction(body, queryParams, "cognitoID");
            assert !shouldThrow;
            assert (rawIDReturn == null);
            System.out.println(injector.getStatementString());
            assert (injector.getStatementString().equals("SELECT * FROM List WHERE (owner = ? AND listID = ?);DELETE FROM ListSharee where listID = ?;DELETE FROM ListProduct where listID = ?;DELETE FROM List WHERE listID = ?;[30]"));
        } catch (SQLException throwables) {
            assert shouldThrow ;
        } catch (AccessControlException accessControlException) {
            assert !hasAccess;
        }
    }
}
