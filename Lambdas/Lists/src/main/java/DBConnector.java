import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnector {

    Connection connection;

    DBConnector() throws IOException, SQLException {
        this(loadProperties("dbProperties.json"));
    }

    DBConnector(Properties dbProperties) throws SQLException {
        System.out.println(dbProperties);
        connection = DriverManager.getConnection(dbProperties.get("url").toString(), dbProperties);

    }

    public static Properties loadProperties(String path) throws IOException {
        Properties toReturn = new Properties();
        String propertiesJSONString = Files.readString(Path.of(path));
        JSONObject propertiesJSON = new JSONObject(propertiesJSONString);
        propertiesJSON.keys().forEachRemaining(key -> toReturn.setProperty(key, propertiesJSON.get(key).toString()));
        return toReturn;
    }
}
