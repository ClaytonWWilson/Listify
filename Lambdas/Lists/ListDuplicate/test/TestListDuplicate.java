import org.junit.Test;
import org.mockito.Mockito;

import java.security.AccessControlException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TestListDuplicate {

    @Test
    public void testListDuplicateValid() {
        testListDuplicaterMock(true);
    }

    @Test
    public void testListDuplicateError() {
        testListDuplicaterMock(false);
    }

    public void testListDuplicaterMock(boolean shouldThrow) {
        StatementInjector injector;
        try {
            injector = new StatementInjector(null, null, shouldThrow);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            assert false; //Error in test infrastructure
            return;
        }
        ListDuplicater listDuplicater = Mockito.spy(new ListDuplicater(injector, "cognitoID"));
        Map<String, Object> ignore = new HashMap<>();
        Map<String, Object> body = TestInputUtils.addBody(ignore);
        body.put("name", "list1");
        body.put("listID", 1);

        try {
            Object rawIDReturn = listDuplicater.conductAction(body, TestInputUtils.addQueryParams(ignore), "cognitoID");
            assert (rawIDReturn != null);
        } catch (SQLException throwables) {
            assert shouldThrow;
            throwables.printStackTrace();
        } catch(AccessControlException throwables) {
            assert !shouldThrow;
            throwables.printStackTrace();
        }
    }
}
