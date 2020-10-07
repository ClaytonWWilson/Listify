import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Item {
    Integer productID;
    Integer chainID;
    String upc;
    String description;
    BigDecimal price;
    String imageURL;
    String department;
    long retrievedDate;
    Integer fetchCounts;

    public Item(ResultSet itemRow) throws SQLException {
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
        this.retrievedDate = itemRow.getTimestamp(8).toInstant().toEpochMilli();
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

    public Integer getProductID() {
        return productID;
    }

    public void setProductID(Integer productID) {
        this.productID = productID;
    }

    public Integer getChainID() {
        return chainID;
    }

    public void setChainID(Integer chainID) {
        this.chainID = chainID;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public long getRetrievedDate() {
        return retrievedDate;
    }

    public void setRetrievedDate(long retrievedDate) {
        this.retrievedDate = retrievedDate;
    }

    public Integer getFetchCounts() {
        return fetchCounts;
    }

    public void setFetchCounts(Integer fetchCounts) {
        this.fetchCounts = fetchCounts;
    }
}
