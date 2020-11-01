package com.example.listify;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.example.listify.adapter.CheckBoxListViewAdapter;

import java.util.ArrayList;


public class FilterDialogFragment extends DialogFragment {

    public interface OnFilterListener {
        void sendFilter(ArrayList<String> selectedStores, double minPrice, double maxPrice);
    }

    public OnFilterListener onFilterListener;

    CrystalRangeSeekbar priceSeekbar;
    CheckBoxListViewAdapter checkBoxAdapter;

    private ArrayList<String> selectedStores;
    private ArrayList<String> stores;
    private double maxProductPrice; // The highest price on the slider
    private double minPrice; // The selected min price
    private double maxPrice; // The selected max price

    public FilterDialogFragment(ArrayList<String> selectedStores, ArrayList<String> stores, double maxProductPrice, double minPrice, double maxPrice) {
        this.selectedStores = selectedStores;
        this.stores = stores;
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
        View root = inflater.inflate(R.layout.dialog_filter, null);
        builder.setView(root)
            // Add action buttons
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    selectedStores = checkBoxAdapter.getChecked();
                    onFilterListener.sendFilter(selectedStores, priceSeekbar.getSelectedMinValue().doubleValue(), priceSeekbar.getSelectedMaxValue().doubleValue());
                }
            })
            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    FilterDialogFragment.this.getDialog().cancel();
                }
            });

        ListView storesList = root.findViewById(R.id.store_name_list);

        // Create arraylist of stores from search results
        ArrayList<String> storeChoices = new ArrayList<>(stores);

        // Create adapter and send stores and selected stores
        checkBoxAdapter = new CheckBoxListViewAdapter(getActivity(), storeChoices, this.selectedStores);

        storesList.setAdapter(checkBoxAdapter);

        // Set up the seekbar for price
        priceSeekbar = (CrystalRangeSeekbar) root.findViewById(R.id.price_range_seekbar);
        final TextView tvMin = (TextView) root.findViewById(R.id.tv_min_price);
        final TextView tvMax = (TextView) root.findViewById(R.id.tv_max_price);

        priceSeekbar.setMaxValue((float) this.maxProductPrice);
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

        return builder.create();
    }

    // Required to extend DialogFragment
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onFilterListener = (OnFilterListener) getActivity();
        } catch (ClassCastException e) {
            Log.e("FilterDialogFragment", "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}
