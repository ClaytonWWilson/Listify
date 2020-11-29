import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ListShare {
    Integer listID;
    String shareWithEmail;
    ArrayList<ListShare> other;

    public ListShare(ResultSet listRow) throws SQLException {
        this.listID = listRow.getInt("listID");
        this.shareWithEmail = listRow.getString("userID");
        other = new ArrayList<>();
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
