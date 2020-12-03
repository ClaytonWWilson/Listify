import org.junit.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TestItemSearcher {

    @Test
    public void testItemSearcherValid() {
        testItemSearcherMock(false);
    }

    @Test
    public void testItemSearcherError() {
        testItemSearcherMock(true);
    }

    public void testItemSearcherMock(boolean shouldThrow) {
        StatementInjector injector;
        try {
            injector = new StatementInjector(null, null, shouldThrow);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            assert false; //Error in test infrastructure
            return;
        }
        ItemSearcher listItemSearcher = Mockito.spy(new ItemSearcher(injector, "cognitoID"));
        Map<String, Object> ignore = new HashMap<>();
        Map<String, Object> body = TestInputUtils.addBody(ignore);
        body.put("id", 1);

        try {
            Object rawIDReturn = listItemSearcher.conductAction(body, TestInputUtils.addQueryParams(ignore), "cognitoID");
            assert !shouldThrow;
            assert (rawIDReturn != null);
        } catch (SQLException throwables) {
            assert shouldThrow;
            throwables.printStackTrace();
        }
    }
}
