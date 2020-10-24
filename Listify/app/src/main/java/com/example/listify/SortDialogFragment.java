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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;

import java.util.ArrayList;


public class SortDialogFragment extends DialogFragment {

    public interface OnSortingListener {
        void sendSort(int storeSelection, int sortMode, boolean descending, double minPrice, double maxPrice);
    }

    public OnSortingListener onSortingListener;

    CrystalRangeSeekbar priceSeekbar;

    private int storeSelection;
    private int sortMode;
    private boolean descending;
    private ArrayList<String> stores;
    private double maxProductPrice; // The highest price on the slider
    private double minPrice; // The selected min price
    private double maxPrice; // The selected max price

    public SortDialogFragment(int storeSelection, ArrayList<String> stores, int sortMode, boolean descending, double maxProductPrice, double minPrice, double maxPrice) {
        this.storeSelection = storeSelection;
        this.stores = stores;
        this.sortMode = sortMode;
        this.descending = descending;
        this.maxProductPrice = maxProductPrice;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
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
                        onSortingListener.sendSort(storeSelection, sortMode, descending, priceSeekbar.getSelectedMinValue().doubleValue(), priceSeekbar.getSelectedMaxValue().doubleValue());
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

        // Create the store selection dropdown
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
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Disable the direction button if they have the default sorting mode selected
        // Ascending and Descending are mostly irrelevant in the default sort mode
        if (sortDropdown.getSelectedItemPosition() == 0) {
            sortDirectionButton.setEnabled(false);
        }

        // Set up the seekbar for price
        priceSeekbar = (CrystalRangeSeekbar) root.findViewById(R.id.price_range_seekbar);
        final TextView tvMin = (TextView) root.findViewById(R.id.tv_min_price);
        final TextView tvMax = (TextView) root.findViewById(R.id.tv_max_price);

        priceSeekbar.setMaxValue((float) this.maxProductPrice);
        System.out.println(String.format("%f : %f", this.minPrice, this.maxPrice));
        priceSeekbar.setMinStartValue((float) this.minPrice);
        priceSeekbar.setMaxStartValue((float) this.maxPrice);
        priceSeekbar.apply();

        // Update price display
        priceSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                tvMin.setText(String.format("$%.2f", minValue.doubleValue()));
                tvMax.setText(String.format("$%.2f", maxValue.doubleValue()));
            }
        });

//        // Save price values when user finishes moving the slider
//        priceSeekbar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
//            @Override
//            public void finalValue(Number minValue, Number maxValue) {
//                minPrice = minValue.doubleValue();
//                maxPrice = maxValue.doubleValue();
////                System.out.println(String.format("Min: $%.2f, Max: $%.2f", minValue.doubleValue(), maxValue.doubleValue()));
//            }
//        });


        return builder.create();
    }

    // Required to extend DialogFragment
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
