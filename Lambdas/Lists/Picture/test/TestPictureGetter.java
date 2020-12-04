import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TestPictureGetter {

    @Test
    public void testPictureGetterValid() {
        testPictureGetter(false);
    }

    @Test
    public void testPictureGetterError() {
        testPictureGetter(true);
    }

    public void testPictureGetter(boolean shouldThrow) {
        StatementInjector injector;
        try {
            injector = new StatementInjector(null, null, shouldThrow);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            assert false; //Error in test infrastructure
            return;
        }
        PictureGetter pictureGetter = Mockito.spy(new PictureGetter(injector, "cognitoID"));
        Map<String, Object> ignore = new HashMap<>();
        Map<String, Object> body = TestInputUtils.addBody(ignore);
        body.put("id", 1);
        body.put("profile", 1);

        try {
            Object rawIDReturn = pictureGetter.conductAction(body, TestInputUtils.addQueryParams(ignore), "cognitoID");
            assert (rawIDReturn != null);
        } catch (SQLException throwables) {
            assert shouldThrow;
            throwables.printStackTrace();
        }
    }
}
