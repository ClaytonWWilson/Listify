package com.example.listify;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import java.util.ArrayList;


public class SortDialogFragment extends DialogFragment {

    public interface OnSortingListener {
        void sendSort(int storeSelection, int sortMode, boolean descending);
    }

    public OnSortingListener onSortingListener;

    private int storeSelection;
    private int sortMode;
    private boolean descending;
    private ArrayList<String> stores;

    public SortDialogFragment(int storeSelection, ArrayList<String> stores, int sortMode, boolean descending) {
        this.storeSelection = storeSelection;
        this.stores = stores;
        this.sortMode = sortMode;
        this.descending = descending;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View root = inflater.inflate(R.layout.dialog_sort, null);
        builder.setView(root)
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        onSortingListener.sendSort(storeSelection, sortMode, descending);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SortDialogFragment.this.getDialog().cancel();
                    }
                });

        Spinner storeDropdown = (Spinner) root.findViewById(R.id.sort_store_dropdown);
        String[] storeChoices = new String[stores.size() + 1];
        storeChoices[0] = "All";
        for (int i = 1; i < stores.size() + 1; i++) {
            storeChoices[i] = stores.get(i - 1);
        }
        ArrayAdapter<String> storeAdapter = new ArrayAdapter<>(root.getContext(), android.R.layout.simple_spinner_dropdown_item, storeChoices);
        storeDropdown.setAdapter(storeAdapter);
        storeDropdown.setSelection(this.storeSelection);
        storeDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                storeSelection = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner sortDropdown = (Spinner) root.findViewById(R.id.sort_mode_dropdown);
        String[] items = new String[] {"<Default>", "Name", "Price", "Store"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(root.getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        sortDropdown.setAdapter(adapter);
        sortDropdown.setSelection(this.sortMode);
        sortDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortMode = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ImageButton sortDirectionButton = root.findViewById(R.id.sort_direction_button);
        sortDirectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (descending) {
                    descending = false;
                } else {
                    descending = true;
                }
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onSortingListener = (OnSortingListener) getActivity();
        } catch (ClassCastException e) {
            Log.e("SortDialogFragment", "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}
