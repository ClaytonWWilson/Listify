package com.example.listify.ui.lists;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.amplifyframework.auth.AuthException;
import com.example.listify.AuthManager;
import com.example.listify.CreateListAddDialogFragment;
import com.example.listify.CreateListDialogFragment;
import com.example.listify.R;
import com.example.listify.Requestor;
import com.example.listify.SynchronousReceiver;
import com.example.listify.adapter.DisplayShoppingListsAdapter;
import com.example.listify.data.List;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Properties;

public class ListsFragment extends Fragment implements CreateListDialogFragment.OnNewListListener {
    ArrayList<List> shoppingLists = new ArrayList<>();
    ListView shoppingListsView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lists, container, false);
        shoppingListsView = root.findViewById(R.id.shopping_lists);

        // TODO: Switch this to async
        AuthManager authManager = new AuthManager();
        try {
            authManager.signIn("merzn@purdue.edu", "Password123");
        } catch (AuthException e) {
            e.printStackTrace();
        }
        Properties configs = new Properties();
        try {
            configs = AuthManager.loadProperties(getContext(), "android.resource://" + getActivity().getPackageName() + "/raw/auths.json");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        Requestor requestor = new Requestor(authManager, configs.getProperty("apiKey"));
        SynchronousReceiver<Integer[]> listIdsReceiver = new SynchronousReceiver<>();
        SynchronousReceiver<List> listReceiver = new SynchronousReceiver<>();

        requestor.getListOfIds(List.class, listIdsReceiver, listIdsReceiver);
        try {
            Integer[] listIds = listIdsReceiver.await();
            for (int i = 0; i < listIds.length; i++) {
                requestor.getObject(Integer.toString(listIds[i]), List.class, listReceiver, listReceiver);
                shoppingLists.add(listReceiver.await());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Set adapter and display this users lists
        DisplayShoppingListsAdapter displayShoppingListsAdapter = new DisplayShoppingListsAdapter(getActivity(), shoppingLists);
        shoppingListsView.setAdapter(displayShoppingListsAdapter);
        shoppingListsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "open and display " + shoppingLists.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.new_list_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateListDialogFragment createListDialogFragment = new CreateListDialogFragment();
                createListDialogFragment.show(getActivity().getSupportFragmentManager(), "Create New List");
            }
        });

        return root;
    }

    @Override
    public void sendNewListName(String name) {
        AuthManager authManager = new AuthManager();
        try {
            authManager.signIn("merzn@purdue.edu", "Password123");
        } catch (AuthException e) {
            e.printStackTrace();
        }
        Properties configs = new Properties();
        try {
            configs = AuthManager.loadProperties(getContext(), "android.resource://" + getActivity().getPackageName() + "/raw/auths.json");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        Requestor requestor = new Requestor(authManager, configs.getProperty("apiKey"));
        SynchronousReceiver<Integer> idReceiver = new SynchronousReceiver<>();

        List newList = new List(-1, name, "user filled by lambda", Instant.now().toEpochMilli());

        try {
            requestor.postObject(newList, idReceiver, idReceiver);
            System.out.println(idReceiver.await());
            Toast.makeText(getContext(), String.format("%s created", name), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}