import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class List {
    Integer itemID;
    String name;
    String owner;
    LocalDateTime lastUpdated;

    public List(ResultSet listRow) throws SQLException {
        itemID = listRow.getInt(1);
        name = listRow.getString(2);
        owner = listRow.getString(3);
        lastUpdated = listRow.getObject(8, LocalDateTime.class);
    }

}
