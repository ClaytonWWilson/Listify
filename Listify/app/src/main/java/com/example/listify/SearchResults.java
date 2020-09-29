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

public class SearchResults extends AppCompatActivity implements SortDialogFragment.OnSortingListener {
    private ListView listView;
    private SearchResultsListAdapter searchResultsListAdapter;
    private List<Product> resultsProductList = new ArrayList<>();
    private List<Product> resultsProductListSorted = new ArrayList<>();
    private ArrayList<String> stores = new ArrayList<>();
    private int storeSelection;
    private int sortMode;
    private boolean descending;

    @Override
    public void sendSort(int storeSelection, int sortMode, boolean descending) {
        this.storeSelection = storeSelection;
        this.sortMode = sortMode;
        this.descending = descending;
        sortResults();
    }

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
        searchResultsListAdapter = new SearchResultsListAdapter(this, resultsProductListSorted);
        listView.setAdapter(searchResultsListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SearchResults.this, resultsProductListSorted.get(position).getItemName(), Toast.LENGTH_SHORT).show();
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

        // Create a dialog for filtering and sorting search results
        ImageButton sortButton = (ImageButton) findViewById(R.id.results_sort_button);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sort the store list
                stores.sort(new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                });
                SortDialogFragment sortDialog = new SortDialogFragment(storeSelection, stores, sortMode, descending);
                sortDialog.show(getSupportFragmentManager(), "Sort");
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

        // Create a list of all stores in the results so the user can filter by store name
        for (int i = 0; i < resultsProductList.size(); i++) {
            if (!stores.contains(resultsProductList.get(i).getChainName())) {
                stores.add(resultsProductList.get(i).getChainName());
            }
        }

        // Add all results to the sorted list
        resultsProductListSorted.addAll(resultsProductList);

        // Apply selected sorting to the list
        sortResults();
    }

    // Sorts the search results
    private void sortResults() {
        // Sort Modes
        // 0 default (no sorting)
        // 1 itemName
        // 2 price
        // 3 chainName
        // 4 upc

        // Sort based on mode
        switch (this.sortMode) {
            case 0:
                resultsProductListSorted.clear();
                resultsProductListSorted.addAll(resultsProductList);
                searchResultsListAdapter.notifyDataSetChanged();
                return;
            case 1:
                resultsProductListSorted.sort(new Comparator<Product>() {
                    @Override
                    public int compare(Product a, Product b) {
                        return a.getItemName().compareToIgnoreCase(b.getItemName());
                    }
                });
                break;

            // TODO: May need to change this depending on if price is stored as a string or a double
            case 2:
                resultsProductListSorted.sort(new Comparator<Product>() {
                    @Override
                    public int compare(Product a, Product b) {
                        if (a.getPrice() - b.getPrice() > 0) {
                            return 1;
                        } else if (a.getPrice() - b.getPrice() < 0) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                });
                break;

            case 3:
                resultsProductListSorted.sort(new Comparator<Product>() {
                    @Override
                    public int compare(Product a, Product b) {
                        return a.getChainName().compareToIgnoreCase(b.getChainName());
                    }
                });
                break;

            case 4:
                resultsProductListSorted.sort(new Comparator<Product>() {
                    @Override
                    public int compare(Product a, Product b) {
                        return a.getUpc().compareToIgnoreCase(b.getUpc());
                    }
                });
                break;
        }

        if (this.descending) {
            for (int i = 0; i < resultsProductListSorted.size() / 2; i++) {
                Product temp = resultsProductListSorted.get(i);
                resultsProductListSorted.set(i, resultsProductListSorted.get(resultsProductListSorted.size() - i - 1));
                resultsProductListSorted.set(resultsProductListSorted.size() - i - 1, temp);
            }
        }

        searchResultsListAdapter.notifyDataSetChanged();
    }
}