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

    DBConnector() throws IOException, SQLException, ClassNotFoundException {
        this(loadProperties("dbProperties.json"));
    }

    DBConnector(Properties dbProperties) throws SQLException, ClassNotFoundException {
        Class.forName("org.mariadb.jdbc.Driver");
        System.out.println(dbProperties);
        System.out.println(DBConnector.buildURL(dbProperties));
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

    public static String buildURL(Properties dbProperties) {
        String dbURL = dbProperties.get("url").toString();
        dbURL += "?user=" + dbProperties.get("user").toString();
        dbURL += "&password=" + dbProperties.get("password").toString();
        return dbURL;
    }
}
