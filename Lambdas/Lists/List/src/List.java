import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class List {
    Integer itemID;
    String name;
    String owner;
    long lastUpdated;
    ArrayList<ItemEntry> entries;
    boolean shared;

    public List(ResultSet listRow, boolean shared) throws SQLException {
        itemID = listRow.getInt("listID");
        name = listRow.getString("name");
        owner = listRow.getString("owner");
        lastUpdated = listRow.getTimestamp("lastUpdated").toInstant().toEpochMilli();
        entries = new ArrayList<>();
        this.shared = shared;
    }

    @Override
    public String toString() {
        return "List{" +
                "itemID=" + itemID +
                ", name='" + name + '\'' +
                ", owner='" + owner + '\'' +
                ", lastUpdated=" + lastUpdated +
                ", entries=" + entries +
                '}';
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

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean getShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public ItemEntry[] getEntries() {
        return entries.toArray(new ItemEntry[entries.size()]);
    }

    public void addItemEntry(ItemEntry entry) {
        entries.add(entry);
    }
}
