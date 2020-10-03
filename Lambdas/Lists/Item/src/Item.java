import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class Item {
    Integer productID;
    Integer chainID;
    String upc;
    String description;
    BigDecimal price;
    String imageURL;
    String department;
    LocalDateTime retrievedDate;
    Integer fetchCounts;

    Item(ResultSet itemRow) throws SQLException {
        this.productID = itemRow.getInt(1);
        System.out.println(this.productID);
        this.chainID = itemRow.getInt(2);
        System.out.println(this.chainID);
        this.upc = itemRow.getString(3);
        System.out.println(this.upc);
        this.description = itemRow.getString(4);
        System.out.println(this.description);
        this.price = itemRow.getBigDecimal(5);
        System.out.println(this.price);
        this.imageURL = itemRow.getString(6);
        System.out.println(imageURL);
        this.department = itemRow.getString(7);
        System.out.println(department);
        this.retrievedDate = itemRow.getObject(8, LocalDateTime.class);
        System.out.println(retrievedDate);
        this.fetchCounts = itemRow.getInt(9);
        System.out.println(fetchCounts);
    }

    @Override
    public String toString() {
        return "Item{" +
                "productID=" + productID +
                ", chainID=" + chainID +
                ", upc='" + upc + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", imageURL='" + imageURL + '\'' +
                ", department='" + department + '\'' +
                ", retrievedDate=" + retrievedDate +
                ", fetchCounts=" + fetchCounts +
                '}';
    }
}
