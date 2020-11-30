package com.example.listify.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.listify.AuthManager;
import com.example.listify.CreateListDialogFragment;
import com.example.listify.LoadingCircleDialog;
import com.example.listify.R;
import com.example.listify.Requestor;
import com.example.listify.SynchronousReceiver;
import com.example.listify.adapter.ShoppingListsSwipeableAdapter;
import com.example.listify.data.List;
import com.example.listify.data.SearchHistory;
import com.example.listify.model.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Properties;

import static com.example.listify.MainActivity.am;
import static com.example.listify.MainActivity.searchHistory;

public class HomeFragment extends Fragment implements CreateListDialogFragment.OnNewListListener, Requestor.Receiver {
    ArrayList<List> shoppingLists = new ArrayList<>();
    ShoppingListsSwipeableAdapter shoppingListsSwipeableAdapter;
    Requestor requestor;
    ListView shoppingListsView;
    ProgressBar loadingLists;
    TextView emptyMessage;
    SwipeRefreshLayout refreshLists;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        shoppingListsView = root.findViewById(R.id.shopping_lists);
        loadingLists = (ProgressBar) root.findViewById(R.id.progress_loading_lists);
        loadingLists.setVisibility(View.VISIBLE);
        emptyMessage = (TextView) root.findViewById(R.id.textViewEmpty);
        refreshLists = (SwipeRefreshLayout) root.findViewById(R.id.refresh_lists);

        Properties configs = new Properties();
        try {
            configs = AuthManager.loadProperties(getContext(), "android.resource://" + getActivity().getPackageName() + "/raw/auths.json");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        requestor = new Requestor(am, configs.getProperty("apiKey"));
        SynchronousReceiver<Integer[]> listIdsReceiver = new SynchronousReceiver<>();

//        final Requestor.Receiver<Integer[]> recv = this;
        requestor.getListOfIds(List.class, this, null);


        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.new_list_fab);
        Fragment thisFragment = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateListDialogFragment createListDialogFragment = new CreateListDialogFragment();
                createListDialogFragment.show(getFragmentManager(), "Create New List");
                createListDialogFragment.setTargetFragment(thisFragment, 0);
            }
        });

        refreshLists.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Properties configs = new Properties();
                try {
                    configs = AuthManager.loadProperties(getContext(), "android.resource://" + getActivity().getPackageName() + "/raw/auths.json");
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

                requestor = new Requestor(am, configs.getProperty("apiKey"));
                SynchronousReceiver<Integer[]> listIdsReceiver = new SynchronousReceiver<>();

                requestor.getListOfIds(List.class, HomeFragment.this, null);
            }
        });

        return root;
    }


    @Override
    public void sendNewListName(String name) {
        LoadingCircleDialog loadingDialog = new LoadingCircleDialog(getActivity());
        loadingDialog.show();

        Properties configs = new Properties();
        try {
            configs = AuthManager.loadProperties(getContext(), "android.resource://" + getActivity().getPackageName() + "/raw/auths.json");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        Requestor requestor = new Requestor(am, configs.getProperty("apiKey"));
        SynchronousReceiver<Integer> idReceiver = new SynchronousReceiver<>();

        List newList = new List(-1, name, "user filled by lambda", Instant.now().toEpochMilli() , -1);

        try {
            requestor.postObject(newList, idReceiver, idReceiver);
            emptyMessage.setVisibility(View.GONE);
        } catch (Exception e) {
            Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    newList.setListID(idReceiver.await());
                } catch (Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_LONG).show();
                            loadingDialog.cancel();
                        }
                    });
                    e.printStackTrace();

                }
                shoppingLists.add(newList);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shoppingListsSwipeableAdapter.notifyDataSetChanged();
                        loadingDialog.cancel();
                        Toast.makeText(getContext(), String.format("%s created", name), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        t.start();
    }

    @Override
    public void acceptDelivery(Object delivered) {
        // Remove old lists on refresh
        shoppingLists.clear();

        Integer[] listIds = (Integer[]) delivered;

        // Create threads and add them to a list
        Thread[] threads = new Thread[listIds.length];
        List[] results = new List[listIds.length];
        for (int i = 0; i < listIds.length; i++) {
            SynchronousReceiver<List> listReceiver = new SynchronousReceiver<>();
            requestor.getObject(Integer.toString(listIds[i]), List.class, listReceiver, listReceiver);
            final int finalI = i;
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        results[finalI] = listReceiver.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            threads[i] = t;
            t.start();
        }

        // Request search history
        final SearchHistory[] history = new SearchHistory[1]; // Needs to be an array because of anonymous class weirdness
        SynchronousReceiver<SearchHistory> historyReceiver = new SynchronousReceiver<>();
        Properties configs = new Properties();
        try {
            configs = AuthManager.loadProperties(getContext(), "android.resource://" + getActivity().getPackageName() + "/raw/auths.json");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        Requestor historyRequestor = new Requestor(am, configs.getProperty("apiKey"));
        historyRequestor.getObject("0", SearchHistory.class, historyReceiver);
        Thread historyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    history[0] = historyReceiver.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        historyThread.start();

        // Wait for each thread to finish and add results to shoppingLists
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            shoppingLists.add(results[i]);
        }

        // Wait for search History response
        try {
            historyThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        searchHistory = history[0].getSearches();

        // Set adapter and display this users lists
        shoppingListsSwipeableAdapter = new ShoppingListsSwipeableAdapter(getActivity(), shoppingLists);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                shoppingListsView.setAdapter(shoppingListsSwipeableAdapter);
                loadingLists.setVisibility(View.GONE);

                if(listIds.length == 0) {
                    emptyMessage.setVisibility(View.VISIBLE);
                }
            }
        });

        refreshLists.setRefreshing(false);
    }
}