package com.example.listify;
import android.media.Image;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.listify.adapter.SearchResultsListAdapter;
import com.example.listify.model.Product;

import java.util.ArrayList;
import java.util.List;

public class SearchResults extends AppCompatActivity {
    private ListView listView;
    private SearchResultsListAdapter searchResultsListAdapter;
    private List<Product> productList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Back button closes this activity and returns to previous activity (MainActivity)
        ImageButton backButton = (ImageButton) findViewById(R.id.backToHomeButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_from_right);
            }
        });

        // Set the search bar to be active and focused
        final SearchView searchView = (SearchView) findViewById(R.id.searchBar);
        searchView.setIconified(false);

        // There's no easy way to find the close button on the search bar, so this is the way I'm
        // doing it
        int searchCloseButtonId = searchView.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = (ImageView) searchView.findViewById(searchCloseButtonId);
        closeButton.setOnClickListener(new View.OnClickListener() {
            // Override default close behavior to only clear the search text and the query
            @Override
            public void onClick(View v) {
                // Finding the edit text of the search bar. Same as the method above
                int searchTextId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
                EditText searchText = (EditText) searchView.findViewById(searchTextId);
                searchText.setText("");
                searchView.setQuery("", false);
            }
        });

        listView = (ListView) findViewById(R.id.search_results_list);
        searchResultsListAdapter = new SearchResultsListAdapter(this, productList);
        listView.setAdapter(searchResultsListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SearchResults.this, productList.get(position).getItemName(), Toast.LENGTH_SHORT).show();
            }
        });

        // Handle searches
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                doSearch(query);
                // TODO: Display the search results listview
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    // Override default phone back button to add animation
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_from_right);
    }

    // TODO: This function will handle the search operation with the database and return
    // a listview to caller to be displayed
    private ListView doSearch(String query) {
        // Hardcode some search results...
        Product a = new Product("Bottled Water", "0000", "Walmart", "0001", "0123456780", "Bro, it's water...", "Grocery", "$13.37", "9/24/2020", "1", "http://3.bp.blogspot.com/-MfroPPQVDKo/UyhUZWqGvkI/AAAAAAAAB-I/DGk622onsvc/s1600/lettuce-b-kool-cat-meme.jpg");
        Product b = new Product("Tin Foil", "0001", "Walmart", "0001", "0123456781", "Not aluminum foil", "Grocery", "$1.00", "9/24/2020", "1", "https://i.ytimg.com/vi/q9N1doYMxR0/maxresdefault.jpg");
        Product c = new Product("Lettuce", "0002", "Walmart", "0001", "0123456782", "It's still wet", "Grocery", "$0.60", "9/24/2020", "1", "https://www.cattitudedaily.com/wp-content/uploads/2019/12/white-cat-meme-feature.jpg");
        Product d = new Product("Video Game", "0003", "Walmart", "0001", "0123456783", "Fun Vidya Gaemz", "Electronics", "$60.00", "9/24/2020", "1", "https://i1.wp.com/bestlifeonline.com/wp-content/uploads/2018/06/cat-meme-67.jpg?resize=1024%2C1024&ssl=1");
        Product e = new Product("Mountain Dew", "0004", "Walmart", "0001", "0123456784", "Gamer fuel", "Grocery", "$5.87", "9/24/2020", "1", "https://memeguy.com/photos/images/gaming-cat-7680.png");
        Product f = new Product("Tire", "0005", "Walmart", "0001", "0123456785", "30 inch rims", "Automotive", "$146.97", "9/24/2020", "1", "http://cdn.sheknows.com/articles/2013/05/pet5.jpg");
        productList.add(a);
        productList.add(b);
        productList.add(c);
        productList.add(d);
        productList.add(e);
        productList.add(f);

        return null;
    }
}