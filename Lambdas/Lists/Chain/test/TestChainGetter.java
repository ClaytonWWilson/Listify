import org.junit.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TestChainGetter {

    @Test
    public void testChainGetterValid() {
        testChainGetter(false);
    }

    @Test
    public void testChainGetterError() {
        testChainGetter(true);
    }

    public void testChainGetter(boolean shouldThrow) {
        StatementInjector injector;
        try {
            injector = new StatementInjector(null, null, shouldThrow);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            assert false; //Error in test infrastructure
            return;
        }
        ChainGetter chainGetter = Mockito.spy(new ChainGetter(injector, "cognitoID"));
        Map<String, Object> ignore = new HashMap<>();
        Map<String, Object> body = TestInputUtils.addBody(ignore);
        ignore.put("id", 1);    //in ChainGetter.java uses ignore map for id parameter

        try {
            Object rawIDReturn = chainGetter.conductAction(body, TestInputUtils.addQueryParams(ignore), "cognitoID");
            assert !shouldThrow;
            assert (rawIDReturn != null);
        } catch (SQLException throwables) {
            assert shouldThrow;
            throwables.printStackTrace();
        }
    }
}
