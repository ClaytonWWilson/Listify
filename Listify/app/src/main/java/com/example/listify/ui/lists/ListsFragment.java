package com.example.listify.ui.lists;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.listify.MainActivity;
import com.example.listify.R;
import com.example.listify.adapter.DisplayShoppingListsAdapter;
import com.example.listify.model.ShoppingList;

import java.util.ArrayList;

public class ListsFragment extends Fragment {
    ListView shoppingListsView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lists, container, false);

//        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar_lists);
//        ((AppCompatActivity)getActivity()).setActionBar(toolbar);

        // Hardcode shopping lists to demonstrate displaying lists
        shoppingListsView = root.findViewById(R.id.shopping_lists);
        ShoppingList a = new ShoppingList("first list");
        ShoppingList b = new ShoppingList("Groceries");
        ShoppingList c = new ShoppingList("Expensive Stuff");
        ArrayList<ShoppingList> shoppingLists = new ArrayList<>();
        shoppingLists.add(a);
        shoppingLists.add(b);
        shoppingLists.add(c);

        // Set adapter and display this users lists
        DisplayShoppingListsAdapter displayShoppingListsAdapter = new DisplayShoppingListsAdapter(getActivity(), shoppingLists);
        shoppingListsView.setAdapter(displayShoppingListsAdapter);
        shoppingListsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "open and display " + shoppingLists.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }
}