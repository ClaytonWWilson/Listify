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
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import java.util.ArrayList;


public class FilterDialogFragment extends DialogFragment {

    public interface OnFilterListener {
        void sendFilter(int storeSelection, double minPrice, double maxPrice);
    }

    public OnFilterListener onFilterListener;

    CrystalRangeSeekbar priceSeekbar;

    private int storeSelection;
    private ArrayList<String> stores;
    private double maxProductPrice; // The highest price on the slider
    private double minPrice; // The selected min price
    private double maxPrice; // The selected max price

    public FilterDialogFragment(int storeSelection, ArrayList<String> stores, double maxProductPrice, double minPrice, double maxPrice) {
        this.storeSelection = storeSelection;
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
                    onFilterListener.sendFilter(storeSelection, priceSeekbar.getSelectedMinValue().doubleValue(), priceSeekbar.getSelectedMaxValue().doubleValue());
                }
            })
            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    FilterDialogFragment.this.getDialog().cancel();
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
