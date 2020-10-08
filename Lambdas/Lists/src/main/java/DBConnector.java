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

    public DBConnector() throws IOException, SQLException, ClassNotFoundException {
        this(loadProperties(DBConnector.class.getResource("dbProperties.json").getPath()));
    }

    public DBConnector(Properties dbProperties) throws SQLException, ClassNotFoundException {
        Class.forName("org.mariadb.jdbc.Driver");
        System.out.println(dbProperties);
        connection = DriverManager.getConnection(dbProperties.get("url").toString(), dbProperties.get("user").toString(), dbProperties.get("password").toString());
        System.out.println(connection);
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static Properties loadProperties(String path) throws IOException {
        Properties toReturn = new Properties();
        String propertiesJSONString = Files.readString(Path.of(path));
        JSONObject propertiesJSON = new JSONObject(propertiesJSONString);
        propertiesJSON.keys().forEachRemaining(key -> toReturn.setProperty(key, propertiesJSON.get(key).toString()));
        return toReturn;
    }
}
