import java.sql.ResultSet;
import java.sql.SQLException;

public class Picture {
    String base64EncodedImage;

    public Picture(ResultSet rs) {
        try {
            this.base64EncodedImage = rs.getString("base64image");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            this.base64EncodedImage = null;
        }
    }

    public Picture(String base64EncodedImage) {
        this.base64EncodedImage = base64EncodedImage;
    }

    @Override
    public String toString() {
        return "Picture{" +
                "base64EncodedImage='" + base64EncodedImage + '\'' +
                '}';
    }

    public String getBase64EncodedImage() {
        return base64EncodedImage;
    }

    public void setBase64EncodedImage(String base64EncodedImage) {
        this.base64EncodedImage = base64EncodedImage;
    }
}
