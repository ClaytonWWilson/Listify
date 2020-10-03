import com.amazonaws.services.lambda.runtime.Context;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Map;

public class BasicHandler {
        public static <T extends CallHandler> String handleRequest(Map<String, Object> inputMap, Context unfilled, Class<T> toCall) {
            String cognitoID = InputUtils.getCognitoIDFromBody(inputMap);
            try {
                DBConnector connector = new DBConnector();
                try {
                    Constructor<T> constructor = toCall.getConstructor(DBConnector.class, String.class);
                    CallHandler callHandler = constructor.newInstance(connector, cognitoID);
                    callHandler.conductAction(InputUtils.getBody(inputMap), "");
                } catch (SQLException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                } finally {
                    connector.close();
                }
            } catch (IOException |SQLException|ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
            return null;
        }
}
