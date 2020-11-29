import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ListShare {
    Integer listID;
    String shareWithEmail;
    Integer permissionLevel;
    Integer uiPosition;
    ArrayList<ListShare> other;

    public ListShare(ResultSet listRow) throws SQLException {
        this.listID = listRow.getInt("listID");
        this.shareWithEmail = listRow.getString("userID");
        this.permissionLevel = listRow.getInt("permissionLevel");
        this.uiPosition = listRow.getInt("uiPosition");
        other = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "ListShare{" +
                "listID=" + listID +
                ", shareWithEmail='" + shareWithEmail + '\'' +
                ", permissionLevel=" + permissionLevel +
                ", uiPosition=" + uiPosition +
                ", other=" + other +
                '}';
    }

    public Integer getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(Integer permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public Integer getUiPosition() {
        return uiPosition;
    }

    public void setUiPosition(Integer uiPosition) {
        this.uiPosition = uiPosition;
    }

    public ArrayList<ListShare> getOther() {
        return other;
    }

    public void setOther(ArrayList<ListShare> other) {
        this.other = other;
    }

    public Integer getListID() {
        return listID;
    }

    public void setListID(Integer listID) {
        this.listID = listID;
    }

    public String getShareWithEmail() {
        return shareWithEmail;
    }

    public void setShareWithEmail(String shareWithEmail) {
        this.shareWithEmail = shareWithEmail;
    }

    public ListShare[] getEntries() {
        return other.toArray(new ListShare[other.size()]);
    }

    public void addtoList(ListShare entry) {
        other.add(entry);
    }
}
