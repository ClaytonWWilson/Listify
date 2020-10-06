import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ItemEntry {
    Integer listID;
    Integer productID;
    Integer quantity;
    LocalDateTime addedDate;
    Boolean purchased;
    public ItemEntry(Integer listID, ResultSet listRow) throws SQLException {
        this.listID = listID;
        productID = listRow.getInt(1);
        quantity = listRow.getInt(2);
        addedDate = listRow.getObject(3, LocalDateTime.class);
        purchased = listRow.getBoolean(4);
    }

    public Integer getProductID() {
        return productID;
    }

    public void setProductID(Integer productID) {
        this.productID = productID;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDateTime addedDate) {
        this.addedDate = addedDate;
    }

    public Boolean getPurchased() {
        return purchased;
    }

    public void setPurchased(Boolean purchased) {
        this.purchased = purchased;
    }
}
