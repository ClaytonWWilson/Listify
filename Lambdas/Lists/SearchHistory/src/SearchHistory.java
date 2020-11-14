import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SearchHistory implements Serializable {
    ArrayList<String> searches;

    public SearchHistory(ArrayList<String> searches) {
        this.searches = searches;
    }

    public SearchHistory() {
        this.searches = new ArrayList<>();
    }

    public SearchHistory(ResultSet row) throws SQLException {
        this.searches = new ArrayList<>();
        row.beforeFirst();
        while (row.next()) {
            this.searches.add(row.getString("search"));
        }
    }

    public ArrayList<String> getSearches() {
        return searches;
    }

    public void setSearches(ArrayList<String> searches) {
        this.searches = searches;
    }

    public void addSearch(String newSearch) {
        searches.add(newSearch);
    }

    @Override
    public String toString() {
        return "SearchHistory{" +
                "searches=" + searches +
                '}';
    }
}
