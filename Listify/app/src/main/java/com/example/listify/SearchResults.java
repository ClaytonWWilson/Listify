package com.example.listify;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
//import android.widget.SearchView;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;

import com.example.listify.adapter.SearchResultsListAdapter;
import com.example.listify.data.Chain;
import com.example.listify.data.ItemSearch;
import com.example.listify.model.Product;
import org.json.JSONException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import static com.example.listify.MainActivity.am;
import static com.example.listify.MainActivity.searchHistory;

public class SearchResults extends AppCompatActivity implements FilterDialogFragment.OnFilterListener, SortDialogFragment.OnSortListener, Requestor.Receiver {
    private ListView resultsListView;
    private MenuItem filterItem;
    private ProgressBar loadingSearch;
    private TextView tvNoResults;
    private SearchView.SearchAutoComplete searchAutoComplete;
    private ArrayList<String> orderedHistory = new ArrayList<>();
    private ArrayAdapter<String> searchHistoryAdapter;
    private SearchResultsListAdapter searchResultsListAdapter;
    private List<Product> resultsProductList = new ArrayList<>();
    private List<Product> resultsProductListSorted = new ArrayList<>();
    private ArrayList<String> stores = new ArrayList<>();
    private ArrayList<String> selectedStores = new ArrayList<>();
    private SortModes sortMode = SortModes.NONE;
    private boolean descending;
    private double minPrice = 0;
    private double maxPrice = -1;

    @Override
    public void sendFilter(ArrayList<String> selectedStores, double minPrice, double maxPrice) {
        this.selectedStores = selectedStores;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        sortResults();
    }

    @Override
    public void sendSort(SortModes sortMode, boolean descending) {
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
        tvNoResults = (TextView) findViewById(R.id.tv_search_no_results);

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
//        int searchCloseButtonId = searchView.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
//        ImageView closeButton = (ImageView) searchView.findViewById(searchCloseButtonId);
//        closeButton.setOnClickListener(new View.OnClickListener() {
//            // Override default close behavior to only clear the search text and the query
//            @Override
//            public void onClick(View v) {
//                // Finding the edit text of the search bar. Same as the method above
//                int searchTextId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
//                EditText searchText = (EditText) searchView.findViewById(searchTextId);
//                searchText.setText("");
//                searchView.setQuery("", false);
//            }
//        });

        resultsListView = (ListView) findViewById(R.id.search_results_list);
        searchResultsListAdapter = new SearchResultsListAdapter(this, resultsProductListSorted);
        resultsListView.setAdapter(searchResultsListAdapter);
        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent itemDetailsPage = new Intent(SearchResults.this, ItemDetails.class);

                // Send the selected product
                itemDetailsPage.putExtra("SelectedProduct", resultsProductListSorted.get(position));
                startActivity(itemDetailsPage);
            }
        });

//         Handle searches
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
                doSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        SearchManager searchManager = (SearchManager) getSystemService(this.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        orderedHistory.clear();
        orderedHistory.addAll(searchHistory);

        // Reverse the history so it's displayed in the correct ordered
        for (int i = 0; i < orderedHistory.size() / 2; i++) {
            String temp = orderedHistory.get(i);
            orderedHistory.set(i, orderedHistory.get(orderedHistory.size() - i - 1));
            orderedHistory.set(orderedHistory.size() - i - 1, temp);
        }

        searchHistoryAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.search_suggestion, orderedHistory);
        System.out.println(orderedHistory);

        searchAutoComplete.setAdapter(searchHistoryAdapter);
        searchAutoComplete.setThreshold(0);

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String query = ((TextView) view).getText().toString();
                searchView.setQuery(query, false);

                // Show progress bar
                loadingSearch.setVisibility(View.VISIBLE);

                // Clear the old search results
                resultsProductList.clear();

                // Clear old search results from the view
                resultsProductListSorted.clear();
                searchResultsListAdapter.notifyDataSetChanged();

                doSearch(query);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem sortItem = menu.findItem(R.id.action_sort);
        sortItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                SortDialogFragment sortDialog = new SortDialogFragment(sortMode, descending);
                sortDialog.show(getSupportFragmentManager(), "Sort Dialog");
                return false;
            }
        });

        filterItem = menu.findItem(R.id.action_filter);
        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Determine the max price for the price slider
                double maxProductPrice;
                if (resultsProductList.isEmpty()) {
                    // default to $100
                    maxProductPrice = 100.00;

                    minPrice = 0;
                    maxPrice = 100;
                } else {
                    maxProductPrice = resultsProductList.get(0).getPrice().doubleValue();
                    for (int i = 1; i < resultsProductList.size(); i++) {
                        if (resultsProductList.get(i).getPrice().doubleValue() > maxProductPrice) {
                            maxProductPrice = resultsProductList.get(i).getPrice().doubleValue();
                        }
                    }
                    if (maxPrice == -1) {
                        maxPrice = maxProductPrice;
                    }
                }

                // Round up to nearest whole number for display on price seekbar
                maxProductPrice = Math.ceil(maxProductPrice);

                FilterDialogFragment sortDialog = new FilterDialogFragment(selectedStores, stores, maxProductPrice, minPrice, maxPrice);
                sortDialog.show(getSupportFragmentManager(), "Filter Dialog");
                return false;
            }
        });

        // Disable filtering by default until a search is made
        if (resultsProductList.isEmpty()) {
            filterItem.setEnabled(false);
        }
        return true;
    }

    // Override default phone back button to add animation
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_from_right);
    }

    private void doSearch(String query) {
        // Add this search to the history
        searchHistory.add(query);
        orderedHistory.clear();
        orderedHistory.addAll(searchHistory);
        ArrayList<String> t = new ArrayList<>();
        t.addAll(searchHistory);

        // Reverse the history so it's displayed in the correct ordered
        for (int i = 0; i < t.size() / 2; i++) {
            String temp = t.get(i);
            t.set(i, t.get(t.size() - i - 1));
            t.set(t.size() - i - 1, temp);
        }

        // Have to make a new adapter here because calling notifyDatasetChanged() would not update the search suggestions for some reason
        searchHistoryAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.search_suggestion, t);
        searchAutoComplete.setAdapter(searchHistoryAdapter);


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

        // Scroll the user back to the top of the results
        if (resultsListView != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    resultsListView.smoothScrollToPosition(0);
                }
            });
        }

        // Sort Modes
        // 0 default (no sorting)
        // 1 itemName
        // 2 price
        // 3 chainName
        // 4 upc

        // Sort based on mode
        switch (this.sortMode) {
            case NONE:
                // Do nothing
                break;
            
            case NAME:
                resultsProductListSorted.sort(new Comparator<Product>() {
                    @Override
                    public int compare(Product a, Product b) {
                        return a.getItemName().compareToIgnoreCase(b.getItemName());
                    }
                });
                break;
            
            case PRICE:
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

            case STORE:
                resultsProductListSorted.sort(new Comparator<Product>() {
                    @Override
                    public int compare(Product a, Product b) {
                        return a.getChainName().compareToIgnoreCase(b.getChainName());
                    }
                });
                break;

            case UPC:
                resultsProductListSorted.sort(new Comparator<Product>() {
                    @Override
                    public int compare(Product a, Product b) {
                        return a.getUpc().compareToIgnoreCase(b.getUpc());
                    }
                });
                break;
        }

        // Flip the list if descending is selected
        if (this.descending) {
            for (int i = 0; i < resultsProductListSorted.size() / 2; i++) {
                Product temp = resultsProductListSorted.get(i);
                resultsProductListSorted.set(i, resultsProductListSorted.get(resultsProductListSorted.size() - i - 1));
                resultsProductListSorted.set(resultsProductListSorted.size() - i - 1, temp);
            }
        }

        // Only keep results that match the current store selection
        if (!this.selectedStores.equals(this.stores)) {
            ArrayList<Product> temp = new ArrayList<>();
            resultsProductListSorted.forEach(product -> {
                if (this.selectedStores.contains(product.getChainName())) {
                    temp.add(product);
                }
            });
            resultsProductListSorted.clear();
            resultsProductListSorted.addAll(temp);
        }
      
      // Filter out products that don't fit price restraints
        ArrayList<Product> temp = new ArrayList<>();
        resultsProductListSorted.forEach(product -> {
            if (product.getPrice().doubleValue() >= this.minPrice &&
                    (this.maxPrice == -1 || product.getPrice().doubleValue() <= this.maxPrice)) {
                temp.add(product);
            }
        });
        resultsProductListSorted.clear();
        resultsProductListSorted.addAll(temp);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchResultsListAdapter.notifyDataSetChanged();
            }
        });
    }

    // This is called after the search results come back from the server
    @Override
    public void acceptDelivery(Object delivered) {
        ItemSearch results = (ItemSearch) delivered;

        // Display "no results" message if the search returns none
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (results.getResults().size() == 0) {
                    tvNoResults.setVisibility(View.VISIBLE);
                } else {
                    tvNoResults.setVisibility(View.GONE);
                }
            }
        });

        try {
            HashMap<Integer,String> chainNameMap = new HashMap<>();
            for (int i = 0; i < results.getResults().size(); i++) {
                // Use hash map to save chain name to avoid lots of requests
                if (!chainNameMap.containsKey((Integer)results.getResults().get(i).getChainID())) {
                    Properties configs = new Properties();
                    try {
                        configs = AuthManager.loadProperties(this, "android.resource://" + getPackageName() + "/raw/auths.json");
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }

                    SynchronousReceiver<Chain> chainReciever = new SynchronousReceiver<>();
                    Requestor requestor = new Requestor(am, configs.getProperty("apiKey"));
                    requestor.getObject(Integer.toString(results.getResults().get(i).getChainID()), Chain.class, chainReciever);

                    chainNameMap.put((Integer)results.getResults().get(i).getChainID(), chainReciever.await().getName());
                }

                resultsProductList.add(new Product(
                        results.getResults().get(i).getDescription(),
                        results.getResults().get(i).getProductID(),
                        chainNameMap.get(results.getResults().get(i).getChainID()),
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
        stores.clear();
        for (int i = 0; i < resultsProductList.size(); i++) {
            if (!stores.contains(resultsProductList.get(i).getChainName())) {
                stores.add(resultsProductList.get(i).getChainName());
            }
        }

        // Reset selected stores on search so that every store is selected
        this.selectedStores.clear();
        this.selectedStores.addAll(stores);

        // Add all results to the sorted list
        resultsProductListSorted.addAll(resultsProductList);
      
        // Filtering should only be allowed if there are items in the results
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (resultsProductList.isEmpty()) {
                    filterItem.setEnabled(false);
                } else {
                    filterItem.setEnabled(true);
                }
            }
        });

        // Reset price filter
        double max = 0;
        for (int i = 0; i < resultsProductListSorted.size(); i++){
            if (resultsProductListSorted.get(i).getPrice().doubleValue() > max) {
                max = resultsProductListSorted.get(i).getPrice().doubleValue();
            }
        }

        this.minPrice = 0;
        this.maxPrice = max;

        // Apply selected sorting to the list
        sortResults();

        // Updates the list of search results. Runs on the main UI thread since other threads are
        // not allowed to change UI elements
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Hide progress bar
                loadingSearch.setVisibility(View.GONE);

            }
        });
    }
}