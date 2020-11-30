import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class List {
    Integer listID;
    String name;
    String owner;
    long lastUpdated;
    ArrayList<ItemEntry> entries;
    boolean shared;
    Integer uiPosition;

    public List(ResultSet listRow, boolean shared, Integer uiPosition) throws SQLException {
        listID = listRow.getInt("listID");
        name = listRow.getString("name");
        owner = listRow.getString("owner");
        lastUpdated = listRow.getTimestamp("lastUpdated").toInstant().toEpochMilli();
        entries = new ArrayList<>();
        this.shared = shared;
        this.uiPosition = uiPosition;
    }

    @Override
    public String toString() {
        return "List{" +
                "listID=" + listID +
                ", name='" + name + '\'' +
                ", owner='" + owner + '\'' +
                ", lastUpdated=" + lastUpdated +
                ", entries=" + entries +
                ", shared=" + shared +
                ", uiPosition=" + uiPosition +
                '}';
    }


    public ItemEntry[] getEntries() {
        return entries.toArray(new ItemEntry[entries.size()]);
    }

    public Integer getListID() {
        return listID;
    }

    public void setListID(Integer listID) {
        this.listID = listID;
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

    public Integer getUiPosition() {
        return uiPosition;
    }

//    public void setUiPosition(Integer uiPosition) {
//        this.uiPosition = uiPosition;
//
//   public ItemEntry[] getEntries() {
//        //return entries.toArray(new ItemEntry[entries.size()]);
//        return;
//    }
//
//    public void addItemEntry(ItemEntry entry) {
//        //entries.add(entry);
//            return;
//    }
}
