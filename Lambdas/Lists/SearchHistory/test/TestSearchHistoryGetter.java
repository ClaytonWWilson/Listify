import org.junit.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class TestSearchHistoryGetter {

    @Test
    public void testSearchHistoryValid() {
        testSearchHistoryGetterMock(false);
    }

    @Test
    public void testSearchHistoryError() {
        testSearchHistoryGetterMock(true);
    }

    public void testSearchHistoryGetterMock(boolean shouldThrow) {
        StatementInjector injector;
        try {
            injector = new StatementInjector(null, null, shouldThrow);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            assert false; //Error in test infrastructure
            return;
        }
        SearchHistoryGetter searchHistoryGetter = Mockito.spy(new SearchHistoryGetter(injector, "cognitoID"));
        Map<String, Object> ignore = new HashMap<>();
        Map<String, Object> body = TestInputUtils.addBody(ignore);

        try {
            Object rawIDReturn = searchHistoryGetter.conductAction(body, TestInputUtils.addQueryParams(ignore), "cognitoID");
            assert (rawIDReturn != null);
        } catch (SQLException throwables) {
            assert shouldThrow;
            throwables.printStackTrace();
        }
    }
}
