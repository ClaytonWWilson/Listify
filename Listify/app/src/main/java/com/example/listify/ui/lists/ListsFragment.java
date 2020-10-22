package com.example.listify.ui.lists;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import com.amplifyframework.auth.AuthException;
import com.example.listify.AuthManager;
import com.example.listify.CreateListAddDialogFragment;
import com.example.listify.CreateListDialogFragment;
import com.example.listify.ItemDetails;
import com.example.listify.ListPage;
import com.example.listify.R;
import com.example.listify.Requestor;
import com.example.listify.SearchResults;
import com.example.listify.SynchronousReceiver;
import com.example.listify.adapter.DisplayShoppingListsAdapter;
import com.example.listify.data.List;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Properties;

import static com.example.listify.MainActivity.am;

public class ListsFragment extends Fragment implements CreateListDialogFragment.OnNewListListener, Requestor.Receiver {
    ArrayList<List> shoppingLists = new ArrayList<>();
    DisplayShoppingListsAdapter displayShoppingListsAdapter;
    Requestor requestor;
    ListView shoppingListsView;
    ProgressBar loadingLists;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lists, container, false);
        shoppingListsView = root.findViewById(R.id.shopping_lists);
        loadingLists = (ProgressBar) root.findViewById(R.id.progress_loading_lists);
        loadingLists.setVisibility(View.VISIBLE);

        Properties configs = new Properties();
        try {
            configs = AuthManager.loadProperties(getContext(), "android.resource://" + getActivity().getPackageName() + "/raw/auths.json");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        requestor = new Requestor(am, configs.getProperty("apiKey"));
        SynchronousReceiver<Integer[]> listIdsReceiver = new SynchronousReceiver<>();

        final Requestor.Receiver<Integer[]> recv = this;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                requestor.getListOfIds(List.class, recv, null);
            }
        });
        t.start();


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

        return root;
    }

    @Override
    public void sendNewListName(String name) {

        Properties configs = new Properties();
        try {
            configs = AuthManager.loadProperties(getContext(), "android.resource://" + getActivity().getPackageName() + "/raw/auths.json");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        Requestor requestor = new Requestor(am, configs.getProperty("apiKey"));
        SynchronousReceiver<Integer> idReceiver = new SynchronousReceiver<>();

        List newList = new List(-1, name, "user filled by lambda", Instant.now().toEpochMilli());

        try {
            requestor.postObject(newList, idReceiver, idReceiver);
            newList.setItemID(idReceiver.await());
            shoppingLists.add(newList);
            displayShoppingListsAdapter.notifyDataSetChanged();
            Toast.makeText(getContext(), String.format("%s created", name), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void acceptDelivery(Object delivered) {
        SynchronousReceiver<List> listReceiver = new SynchronousReceiver<>();
        Integer[] listIds = (Integer[]) delivered;
        try {
//            Integer[] listIds = listIdsReceiver.await();
            for (int i = 0; i < listIds.length; i++) {
                requestor.getObject(Integer.toString(listIds[i]), List.class, listReceiver, listReceiver);
                shoppingLists.add(listReceiver.await());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set adapter and display this users lists
        displayShoppingListsAdapter = new DisplayShoppingListsAdapter(getActivity(), shoppingLists);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                shoppingListsView.setAdapter(displayShoppingListsAdapter);
                shoppingListsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent listPage = new Intent(getContext(), ListPage.class);

                        // Send the list ID
                        listPage.putExtra("listID", shoppingLists.get(position).getItemID());
                        startActivity(listPage);
                    }
                });
                loadingLists.setVisibility(View.GONE);
            }
        });

    }
}