import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

public class TestDBConnector {

    @Test
    public void testLoadProperties() {
        Properties testProperties;
        try {
            testProperties = DBConnector.loadProperties(getClass().getResource("testDBProperties.json").getPath());
        } catch (IOException e) {
            System.out.println(e);
            assert false;
            return;
        }
        Properties testPropertiesKey = new Properties();
        testPropertiesKey.put("url", "jdbc:mariadb://listify.id.us-east-2.rds.amazonaws.com:3306/ListifySchema");
        testPropertiesKey.put("user", "auser");
        testPropertiesKey.put("password", "apassword");
        assert (testProperties.equals(testPropertiesKey));
    }

}
