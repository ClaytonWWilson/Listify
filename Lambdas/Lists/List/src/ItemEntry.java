import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemEntry {
    Integer listID;
    Integer productID;
    Integer quantity;
    long addedDate;
    Boolean purchased;
    public ItemEntry(Integer listID, ResultSet listRow) throws SQLException {
        this.listID = listID;
        productID = listRow.getInt(2);
        quantity = listRow.getInt(3);
        addedDate = listRow.getTimestamp(4).toInstant().toEpochMilli();
        purchased = listRow.getBoolean(5);
    }

    @Override
    public String toString() {
        return "ItemEntry{" +
                "listID=" + listID +
                ", productID=" + productID +
                ", quantity=" + quantity +
                ", addedDate=" + addedDate +
                ", purchased=" + purchased +
                '}';
    }

    public Integer getListID() {
        return listID;
    }

    public void setListID(Integer listID) {
        this.listID = listID;
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

    public long getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(long addedDate) {
        this.addedDate = addedDate;
    }

    public Boolean getPurchased() {
        return purchased;
    }

    public void setPurchased(Boolean purchased) {
        this.purchased = purchased;
    }
}
