import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.Mockito.*;

import java.security.AccessControlException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TestListSharer {

    @Test
    public void testListSharerWOAccess() {
        testListEntryAdderCoreMock(false, false);
    }

    @Test
    public void testListSharerError() {
        testListEntryAdderCoreMock(true, true);
    }

    public void testListEntryAdderCoreMock(boolean shouldThrow, boolean hasAccess) {
        StatementInjector injector;
        try {
            injector = new StatementInjector(null, null, shouldThrow);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            assert false; //Error in test infrastructure
            return;
        }
        ListSharer listSharer = Mockito.spy(new ListSharer(injector, "cognitoID"));
        Map<String, Object> ignore = new HashMap<>();
        Map<String, Object> body = TestInputUtils.addBody(ignore);
        body.put("listID", 49);

        try {
            listSharer.conductAction(body, TestInputUtils.addQueryParams(ignore), "cognitoID");
            assert !shouldThrow;
            assert (injector.getStatementString().contains("INSERT INTO ListProduct (productID, listID, quantity, addedDate, purchased) VALUES (?, ?, ?, ?, ?)[16, 15, 14, "));
            assert (injector.getStatementString().contains(", false]"));
        } catch (SQLException throwables) {
            assert shouldThrow;
        } catch (AccessControlException accessControlException) {
            assert !hasAccess;
        }
    }
}
