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


public class SortDialogFragment extends DialogFragment {

    public interface OnSortListener {
        void sendSort(int sortMode, boolean descending);
    }

    public OnSortListener onSortListener;

    private int sortMode;
    private boolean descending;

    public SortDialogFragment(int sortMode, boolean descending) {
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
                    onSortListener.sendSort(sortMode, descending);
                }
            })
            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    SortDialogFragment.this.getDialog().cancel();
                }
            });

        // Change the sort arrow to be pointing up or down based on ascending or descending
        final ImageButton sortDirectionButton = root.findViewById(R.id.sort_direction_button);
        if (descending) {
            sortDirectionButton.setImageResource(R.drawable.ic_baseline_arrow_downward_50);
        } else {
            sortDirectionButton.setImageResource(R.drawable.ic_baseline_arrow_upward_50);
        }

        // Change arrow pointing direction whenever the user clicks the button
        sortDirectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (descending) {
                    descending = false;
                    sortDirectionButton.setImageResource(R.drawable.ic_baseline_arrow_upward_50);
                } else {
                    descending = true;
                    sortDirectionButton.setImageResource(R.drawable.ic_baseline_arrow_downward_50);
                }
            }
        });

        // Create the sort mode selection dropdown
        Spinner sortDropdown = (Spinner) root.findViewById(R.id.sort_mode_dropdown);
        String[] items = new String[] {"<Default>", "Name", "Price", "Store"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(root.getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        sortDropdown.setAdapter(adapter);
        sortDropdown.setSelection(this.sortMode);
        sortDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortMode = position;

                // Update the sort direction button
                if (position == 0) {
                    sortDirectionButton.setEnabled(false);
                } else {
                    sortDirectionButton.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Disable the direction button if they have the default sorting mode selected
        // Ascending and Descending are mostly irrelevant in the default sort mode
        if (sortDropdown.getSelectedItemPosition() == 0) {
            sortDirectionButton.setEnabled(false);
        }

        return builder.create();
    }

    // Required to extend DialogFragment
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onSortListener = (OnSortListener) getActivity();
        } catch (ClassCastException e) {
            Log.e("FilterDialogFragment", "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}
