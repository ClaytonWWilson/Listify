import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TestListReposition {

    @Test
    public void testListRepositionValid() {
        testListRepositioneMock(false);
    }

    @Test
    public void testListRepositionError() {
        testListRepositioneMock(true);
    }

    public void testListRepositioneMock(boolean shouldThrow) {
        StatementInjector injector;
        try {
            injector = new StatementInjector(null, null, shouldThrow);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            assert false; //Error in test infrastructure
            return;
        }
        ListRepositionActor listRepositionActor = Mockito.spy(new ListRepositionActor(injector, "cognitoID"));
        Map<String, Object> ignore = new HashMap<>();
        Map<String, Object> body = TestInputUtils.addBody(ignore);
        body.put("listID", 1);
        body.put("newPosition", 2);

        try {
            Object rawIDReturn = listRepositionActor.conductAction(body, TestInputUtils.addQueryParams(ignore), "cognitoID");
            assert (rawIDReturn == null);
        } catch (SQLException throwables) {
            assert shouldThrow;
            throwables.printStackTrace();
        }
    }
}
