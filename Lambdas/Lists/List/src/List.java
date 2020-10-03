import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class List {
    Integer itemID;
    String name;
    String owner;
    LocalDateTime lastUpdated;
    ArrayList<ItemEntry> entries;

    public List(ResultSet listRow) throws SQLException {
        itemID = listRow.getInt(1);
        name = listRow.getString(2);
        owner = listRow.getString(3);
        lastUpdated = listRow.getObject(4, LocalDateTime.class);
        entries = new ArrayList<>();
    }

    public void addItemEntry(ItemEntry entry) {
        entries.add(entry);
    }

    public ItemEntry[] getEntries() {
        return entries.toArray(new ItemEntry[entries.size()]);
    }

    public Integer getItemID() {
        return itemID;
    }

    public void setItemID(Integer itemID) {
        this.itemID = itemID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
