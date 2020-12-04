import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PicturePutter implements CallHandler {

    private Connection connection;
    private String cognitoID;

    public PicturePutter(Connection connection, String cognitoID) {
        this.connection = connection;
        this.cognitoID = cognitoID;
    }

    final private String STORE_PICTURE_SQL = "REPLACE INTO Pictures(cognitoID, base64image) VALUES(?, ?);";

    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryString, String cognitoID) throws SQLException {
        PreparedStatement storePicture = connection.prepareStatement(STORE_PICTURE_SQL);
//        if(!bodyMap.containsKey("base64EncodedImage")) {
//            throw new IllegalArgumentException("Base64EncodedImage not found");
//        }
        storePicture.setString(1, cognitoID);
        storePicture.setString(2, bodyMap.get("base64EncodedImage").toString());
        System.out.println(storePicture);
        storePicture.executeUpdate();
        connection.commit();
        return null;
    }
}