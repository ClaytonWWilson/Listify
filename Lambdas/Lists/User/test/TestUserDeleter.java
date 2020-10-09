import org.junit.Test;

import java.sql.SQLException;

public class TestUserDeleter {
    @Test
    public void TestUserDelete(){
        try {
            testUserDeleter(false);
            assert(false);
        } catch (Exception e) {}
    }

    public void testUserDeleter(boolean shouldThrow) {
        StatementInjector si = null;
        try {
            si = new StatementInjector(null, null, shouldThrow);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String cognitoID = "adbe8b88-9df4-4900-90e1-58dc48b82612";
        UserDeleter userDeleter = new UserDeleter(si, cognitoID);

        try {
            userDeleter.conductAction(null, null, cognitoID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
