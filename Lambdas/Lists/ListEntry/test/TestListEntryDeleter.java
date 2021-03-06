import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.Mockito.*;

import java.security.AccessControlException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TestListEntryDeleter {

    @Test
    public void testListEntryDeleterValid() { testListEntryDeleterCoreMock(true); }

    @Test
    public void testListEntryDeleterError() {
        testListEntryDeleterCoreMock(false);
    }

    public void testListEntryDeleterCoreMock(boolean shouldThrow) {
        StatementInjector injector;
        try {
            injector = new StatementInjector(null, null, shouldThrow);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            assert false; //Error in test infrastructure
            return;
        }
        ListEntryDeleter listEntryDeleter = Mockito.spy(new ListEntryDeleter(injector, "cognitoID"));
        Map<String, Object> ignore = new HashMap<>();
        Map<String, Object> body = TestInputUtils.addBody(ignore);
        body.put("productID", 16);
        body.put("listID", 15);

        try {
            Object rawIDReturn = listEntryDeleter.conductAction(body, TestInputUtils.addQueryParams(ignore), "cognitoID");
            assert !shouldThrow;
            assert (rawIDReturn == null);
            assert (injector.getStatementString().equals("DELETE FROM ListProduct WHERE (ProductID = ? AND ListID = ?);[16, 15]"));
        } catch (SQLException throwables) {
            assert shouldThrow;
            throwables.printStackTrace();
        } catch (AccessControlException throwables) {
            assert !shouldThrow;
            throwables.printStackTrace();
        }
    }
}
