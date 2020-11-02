import java.sql.ResultSet;
import java.sql.SQLException;

public class Chain {
    String name;
    String website;

    public Chain (ResultSet chainRow) throws SQLException {
        this.name = chainRow.getString("name");
        System.out.println(this.name);
        this.website = chainRow.getString("website");
        System.out.println(this.website);
    }

    public Chain(String name, String website) {
        this.name = name;
        this.website = website;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
