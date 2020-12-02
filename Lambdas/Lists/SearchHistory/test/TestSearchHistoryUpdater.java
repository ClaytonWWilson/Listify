import org.junit.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TestSearchHistoryUpdater {

    @Test
    public void testSearchHistoryUpdaterValid() {
        testSearchHistoryUpdater(false);
    }

    @Test
    public void testSearchHistoryUpdaterError() {
        testSearchHistoryUpdater(true);
    }

    public void testSearchHistoryUpdater(boolean shouldThrow) {
        StatementInjector injector;
        try {
            injector = new StatementInjector(null, null, shouldThrow);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            assert false; //Error in test infrastructure
            return;
        }
        SearchHistoryUpdater testSearchHistoryUpdater = Mockito.spy(new SearchHistoryUpdater(injector, "cognitoID"));
        Map<String, Object> ignore = new HashMap<>();
        Map<String, Object> body = TestInputUtils.addBody(ignore);

        try {
            Object rawIDReturn = testSearchHistoryUpdater.conductAction(body, TestInputUtils.addQueryParams(ignore), "cognitoID");
            assert (rawIDReturn == null);
        } catch (SQLException throwables) {
            assert shouldThrow;
            throwables.printStackTrace();
        }
    }
}
