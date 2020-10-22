package com.example.listify;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import com.example.listify.adapter.SearchResultsListAdapter;
import com.example.listify.data.ItemSearch;
import com.example.listify.model.Product;
import org.json.JSONException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import static com.example.listify.MainActivity.am;

public class SearchResults extends AppCompatActivity implements SortDialogFragment.OnSortingListener, Requestor.Receiver {
    private ProgressBar loadingSearch;
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

        loadingSearch = (ProgressBar) findViewById(R.id.progress_loading_search);

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

        ListView listView = (ListView) findViewById(R.id.search_results_list);
        searchResultsListAdapter = new SearchResultsListAdapter(this, resultsProductListSorted);
        listView.setAdapter(searchResultsListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(SearchResults.this, resultsProductListSorted.get(position).getItemName(), Toast.LENGTH_SHORT).show();
                Intent itemDetailsPage = new Intent(SearchResults.this, ItemDetails.class);

                // Send the selected product
                itemDetailsPage.putExtra("SelectedProduct", resultsProductListSorted.get(position));
                startActivity(itemDetailsPage);
            }
        });

        // Handle searches
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Show progress bar
                loadingSearch.setVisibility(View.VISIBLE);

                // Clear the old search results
                resultsProductList.clear();

                // Clear old search results from the view
                resultsProductListSorted.clear();
                searchResultsListAdapter.notifyDataSetChanged();

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        doSearch(query);
                    }
                });
                t.start();
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
        Properties configs = new Properties();
        try {
            configs = AuthManager.loadProperties(this, "android.resource://" + getPackageName() + "/raw/auths.json");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        Requestor requestor = new Requestor(am, configs.getProperty("apiKey"));
        requestor.getObject(query, ItemSearch.class, this);
    }

    // Sorts the search results
    private void sortResults() {
        // Reset the filtered list
        resultsProductListSorted.clear();
        resultsProductListSorted.addAll(resultsProductList);

        // Sort Modes
        // 0 default (no sorting)
        // 1 itemName
        // 2 price
        // 3 chainName
        // 4 upc

        // Sort based on mode
        switch (this.sortMode) {
            case 0:
                // Do nothing
                break;
            case 1:
                resultsProductListSorted.sort(new Comparator<Product>() {
                    @Override
                    public int compare(Product a, Product b) {
                        return a.getItemName().compareToIgnoreCase(b.getItemName());
                    }
                });
                break;

            case 2:
                resultsProductListSorted.sort(new Comparator<Product>() {
                    @Override
                    public int compare(Product a, Product b) {
                        if (b.getPrice().compareTo(a.getPrice()) > 0) {
                            return 1;
                        } else if (b.getPrice().compareTo(a.getPrice()) < 0) {
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

        if (this.sortMode != 0 & this.descending) {
            for (int i = 0; i < resultsProductListSorted.size() / 2; i++) {
                Product temp = resultsProductListSorted.get(i);
                resultsProductListSorted.set(i, resultsProductListSorted.get(resultsProductListSorted.size() - i - 1));
                resultsProductListSorted.set(resultsProductListSorted.size() - i - 1, temp);
            }
        }

        // Only keep results that match the current store selection
        if (this.storeSelection != 0) {
            ArrayList<Product> temp = new ArrayList<>();
            resultsProductListSorted.forEach(product -> {
                if (product.getChainName().equals(this.stores.get(this.storeSelection - 1))) {
                    temp.add(product);
                }
            });
            resultsProductListSorted.clear();
            resultsProductListSorted.addAll(temp);
        }
    }

    // This is called after the search results come back from the server
    // TODO: Display a "no results" message if nothing is found when searching
    @Override
    public void acceptDelivery(Object delivered) {
        ItemSearch results = (ItemSearch) delivered;

        try {
            for (int i = 0; i < results.getResults().size(); i++) {
                // TODO: Change to dynamically grab chain name by id
                resultsProductList.add(new Product(
                        results.getResults().get(i).getDescription(),
                        results.getResults().get(i).getProductID(),
                        "Kroger",
                        results.getResults().get(i).getChainID(),
                        results.getResults().get(i).getUpc(),
                        results.getResults().get(i).getDescription(),
                        results.getResults().get(i).getPrice(),
                        results.getResults().get(i).getImageURL(),
                        results.getResults().get(i).getDepartment()
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
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

        // Updates the list of search results. Runs on the main UI thread since other threads are
        // not allowed to change UI elements
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchResultsListAdapter.notifyDataSetChanged();

                // Hide progress bar
                loadingSearch.setVisibility(View.GONE);

            }
        });
    }
}