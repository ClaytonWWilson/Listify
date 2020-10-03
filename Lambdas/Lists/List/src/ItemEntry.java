import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ItemEntry {
    Integer productID;
    Integer quantity;
    LocalDateTime addedDate;
    Boolean purchased;
    public ItemEntry(ResultSet listRow) throws SQLException {
        productID = listRow.getInt(1);
        quantity = listRow.getInt(2);
        addedDate = listRow.getObject(3, LocalDateTime.class);
        purchased = listRow.getBoolean(4);
    }
}
