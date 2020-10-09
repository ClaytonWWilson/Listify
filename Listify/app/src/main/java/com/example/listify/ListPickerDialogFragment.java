package com.example.listify;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.example.listify.adapter.DisplayShoppingListsAdapter;
import com.example.listify.data.List;
import com.example.listify.model.ShoppingList;
import java.util.ArrayList;


public class ListPickerDialogFragment extends DialogFragment {

    public interface OnListPickListener {
        void sendListSelection(int selectedListIndex, int quantity);
    }

    public OnListPickListener onListPickListener;

    ListView userListsView;
    DisplayShoppingListsAdapter displayShoppingListsAdapter;
    Button btnMinus;
    Button btnPlus;
    EditText etQuantity;
    private ArrayList<List> userLists;
    private int selectedListIndex;

    public ListPickerDialogFragment(ArrayList<List> userLists) {
        this.userLists = userLists;
    }


    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View root = inflater.inflate(R.layout.dialog_add_to_list, null);
        builder.setView(root)
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        onListPickListener.sendListSelection(selectedListIndex, Integer.parseInt(etQuantity.getText().toString()));
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ListPickerDialogFragment.this.getDialog().cancel();
                    }
                });


        // Display user's shopping lists
        userListsView = (ListView) root.findViewById(R.id.user_lists);
        displayShoppingListsAdapter = new DisplayShoppingListsAdapter(getActivity(), userLists);
        userListsView.setAdapter(displayShoppingListsAdapter);

//        TODO: fix highlighting error
        userListsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                for (int i = 0; i < parent.getChildCount(); i++) {
//                    parent.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
//                }

//                parent.getChildAt(position).setBackgroundColor(Color.GREEN);
//                view.setBackgroundColor(Color.GREEN);
                selectedListIndex = position;
            }
        });

        // Set up quantity selection
        etQuantity = (EditText) root.findViewById(R.id.et_quantity);

        btnMinus = (Button) root.findViewById(R.id.btn_minus);
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int curQauntity = Integer.parseInt(etQuantity.getText().toString());
                if (curQauntity > 0) {
                    curQauntity--;
                    etQuantity.setText(String.format("%d", curQauntity));
                }
            }
        });

        btnPlus = (Button) root.findViewById(R.id.btn_plus);
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int curQauntity = Integer.parseInt(etQuantity.getText().toString());
                curQauntity++;
                etQuantity.setText(String.format("%d", curQauntity));
            }
        });

        return builder.create();
    }

    // Required to extend DialogFragment
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onListPickListener = (OnListPickListener) getActivity();
        } catch (ClassCastException e) {
            Log.e("ListPickerDialogFragment", "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}
