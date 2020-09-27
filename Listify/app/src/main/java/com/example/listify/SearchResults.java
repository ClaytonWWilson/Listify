package com.example.listify;
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
import java.util.Comparator;
import java.util.List;

public class SearchResults extends AppCompatActivity {
    private ListView listView;
    private SearchResultsListAdapter searchResultsListAdapter;
    private List<Product> resultsProductList = new ArrayList<>();
    private List<Product> resultsProductListFiltered = new ArrayList<>();


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
        searchResultsListAdapter = new SearchResultsListAdapter(this, resultsProductListFiltered);
        listView.setAdapter(searchResultsListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SearchResults.this, resultsProductListFiltered.get(position).getItemName(), Toast.LENGTH_SHORT).show();
            }
        });

        // Handle searches
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doSearch(query);
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

    private void doSearch(String query) {
        // TODO: Query Database
        // TODO: Create a new Product Object for each result
        // TODO: Add each result to productList

        // Hardcode some search results...
        for (int i = 0; i < 2; i++) {
            Product a = new Product("Bottled Water", "0000", "Walmart", "0001", "0123456780", "Bro, it's water...", "Grocery", 13.37, "9/24/2020", "1", "http://3.bp.blogspot.com/-MfroPPQVDKo/UyhUZWqGvkI/AAAAAAAAB-I/DGk622onsvc/s1600/lettuce-b-kool-cat-meme.jpg");
            Product b = new Product("Tin Foil", "0001", "Walmart", "0001", "0123456781", "Not aluminum foil", "Grocery", 1.00, "9/24/2020", "1", "https://i.ytimg.com/vi/q9N1doYMxR0/maxresdefault.jpg");
            Product c = new Product("Lettuce", "0002", "Walmart", "0001", "0123456782", "Burger King foot lettuce", "Grocery", 0.60, "9/24/2020", "1", "https://www.cattitudedaily.com/wp-content/uploads/2019/12/white-cat-meme-feature.jpg");
            Product d = new Product("Video Game", "0003", "Walmart", "0001", "0123456783", "Fun Vidya Gaemz", "Electronics", 60.00, "9/24/2020", "1", "https://i1.wp.com/bestlifeonline.com/wp-content/uploads/2018/06/cat-meme-67.jpg?resize=1024%2C1024&ssl=1");
            Product e = new Product("Mountain Dew", "0004", "Walmart", "0001", "0123456784", "Gamer fuel", "Grocery", 5.87, "9/24/2020", "1", "https://memeguy.com/photos/images/gaming-cat-7680.png");
            Product f = new Product("Tire", "0005", "Walmart", "0001", "0123456785", "30 inch rims", "Automotive", 146.97, "9/24/2020", "1", "http://cdn.sheknows.com/articles/2013/05/pet5.jpg");
            resultsProductList.add(a);
            resultsProductList.add(b);
            resultsProductList.add(c);
            resultsProductList.add(d);
            resultsProductList.add(e);
            resultsProductList.add(f);
        }

        // Add all results to the filtered list
        resultsProductListFiltered.addAll(resultsProductList);
    }

    // Sorts the search results
    private void sortResults(int sortMode, boolean descending) {
        // Sort Modes
        // 0 itemName
        // 1 price
        // 2 chainName
        // 3 upc

        // Sort based on mode
        switch (sortMode) {
            case 0:
                resultsProductListFiltered.sort(new Comparator<Product>() {
                    @Override
                    public int compare(Product a, Product b) {
                        return a.getItemName().compareToIgnoreCase(b.getItemName());
                    }
                });
                break;

            // TODO: May need to change this depending on if price is stored as a string or a double
            case 1:
                resultsProductListFiltered.sort(new Comparator<Product>() {
                    @Override
                    public int compare(Product a, Product b) {
                        return (int)(a.getPrice() - b.getPrice());
                    }
                });
                break;

            case 2:
                resultsProductListFiltered.sort(new Comparator<Product>() {
                    @Override
                    public int compare(Product a, Product b) {
                        return a.getChainName().compareToIgnoreCase(b.getChainName());
                    }
                });
                break;

            case 3:
                resultsProductListFiltered.sort(new Comparator<Product>() {
                    @Override
                    public int compare(Product a, Product b) {
                        return a.getUpc().compareToIgnoreCase(b.getUpc());
                    }
                });
                break;
        }

        if (descending) {
            for (int i = 0; i < resultsProductListFiltered.size() / 2; i++) {
                Product temp = resultsProductListFiltered.get(i);
                resultsProductListFiltered.set(i, resultsProductListFiltered.get(resultsProductListFiltered.size() - i - 1));
                resultsProductListFiltered.set(resultsProductListFiltered.size() - i - 1, temp);
            }
        }
    }
}