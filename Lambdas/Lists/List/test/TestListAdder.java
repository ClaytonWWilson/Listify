import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestListAdder {

    @Test
    public void testListAdderValid() {
        testListAdderCore(false);
    }

    @Test
    public void testListAdderError() {
        testListAdderCore(true);
    }

    public void testListAdderCore(boolean shouldThrow) {
        StatementInjector injector;
        ArrayList<Object> rsReturns = new ArrayList<>();
        rsReturns.add(1); //new listID
        try {
            injector = new StatementInjector(null, null, shouldThrow);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            assert false; //Error in test infrastructure
            return;
        }
        ListAdder listAdder = new ListAdder(injector, "cognitoID");
        Map<String, Object> ignore = new HashMap<>();
        Map<String, Object> body = TestInputUtils.addBody(ignore);
        body.put("name", "aname");
        try {
            Object rawIDReturn = listAdder.conductAction(body, TestInputUtils.addQueryParams(ignore), "cognitoID");
            assert !shouldThrow;
            assert (rawIDReturn.getClass() == Integer.class);
            assert (injector.getStatementString().contains("INSERT INTO List (name, owner, lastUpdated) VALUES (?, ?, ?)[aname, cognitoID,"));
        } catch (SQLException throwables) {
            assert shouldThrow;
            throwables.printStackTrace();
        }
    }
}
