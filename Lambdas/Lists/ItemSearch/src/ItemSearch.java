import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ItemSearch {
    ArrayList<Item> results;

    ItemSearch(ResultSet searchResults) throws SQLException {
        results = new ArrayList<>();
        while (searchResults.next()) {
            results.add(new Item(searchResults));
        }
    }

    @Override
    public String toString() {
        return "ItemSearch{" +
                "results=" + results +
                '}';
    }

    public ArrayList<Item> getResults() {
        return results;
    }

    public void setResults(ArrayList<Item> results) {
        this.results = results;
    }
}
