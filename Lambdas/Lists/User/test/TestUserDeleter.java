import org.junit.Test;

import java.nio.file.NoSuchFileException;
import java.sql.SQLException;

public class TestUserDeleter {
    @Test
    public void testUserDeleteFileUsage(){
        testUserDeleter(false);
    }

    @Test
    public void testUserDeleteInvalid(){
        testUserDeleter(true);
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
            assert shouldThrow;
        } catch (Exception e) {
            assert e.getClass().equals(NoSuchFileException.class);
        }
    }
}
