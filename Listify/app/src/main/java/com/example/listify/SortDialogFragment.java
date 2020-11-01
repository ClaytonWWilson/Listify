package com.example.listify;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;

public class SortDialogFragment extends DialogFragment {

    public interface OnSortListener {
        void sendSort(SortModes sortMode, boolean descending);
    }

    public OnSortListener onSortListener;

    private SortModes sortMode;
    private boolean descending;

    TextView tvSortNone;
    TextView tvSortName;
    TextView tvSortPrice;
    TextView tvSortStore;
    SwitchCompat swDescending;

    public SortDialogFragment(SortModes sortMode, boolean descending) {
        this.sortMode = sortMode;
        this.descending = descending;
    }


    // TODO: Sorting should scroll the user back to the top of the page
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View root = inflater.inflate(R.layout.dialog_sort, null);
        builder.setView(root);

        tvSortNone = (TextView) root.findViewById(R.id.sort_none);
        tvSortName = (TextView) root.findViewById(R.id.sort_name);
        tvSortPrice = (TextView) root.findViewById(R.id.sort_price);
        tvSortStore = (TextView) root.findViewById(R.id.sort_store);
        LinearLayout llDescendingContainer = (LinearLayout) root.findViewById(R.id.descending_container);
        swDescending = (SwitchCompat) root.findViewById(R.id.switch_descending);

        switch (this.sortMode) {
            case NONE:
                tvSortNone.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                break;
            case NAME:
                tvSortName.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                break;
            case PRICE:
                tvSortPrice.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                break;
            case STORE:
                tvSortStore.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                break;
        }

        if (this.descending) {
            swDescending.setChecked(true);
        }

        tvSortNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClicked(tvSortNone, SortModes.NONE, swDescending.isChecked());
            }
        });

        tvSortName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClicked(tvSortName, SortModes.NAME, swDescending.isChecked());
            }
        });

        tvSortPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClicked(tvSortPrice, SortModes.PRICE, swDescending.isChecked());
            }
        });

        tvSortStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClicked(tvSortStore, SortModes.STORE, swDescending.isChecked());
            }
        });

        // TODO: set onclick listener for descending switch
        llDescendingContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("linear");
                swDescending.performClick();
//                handleClicked(sortMode, swDescending.isChecked());
            }
        });

        swDescending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("switch");
                descending = swDescending.isChecked();
                onSortListener.sendSort(sortMode, swDescending.isChecked());
            }
        });

        return builder.create();
    }

    void handleClicked(TextView tvSelected, SortModes sortMode, boolean descending) {
        this.sortMode = sortMode;
        this.descending = descending;

        tvSortNone.setBackgroundColor(getResources().getColor(R.color.white));
        tvSortName.setBackgroundColor(getResources().getColor(R.color.white));
        tvSortPrice.setBackgroundColor(getResources().getColor(R.color.white));
        tvSortStore.setBackgroundColor(getResources().getColor(R.color.white));

        tvSelected.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        onSortListener.sendSort(sortMode, descending);

//        SortDialogFragment.this.getDialog().cancel();
    }

    // Required to extend DialogFragment
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onSortListener = (OnSortListener) getActivity();
        } catch (ClassCastException e) {
            Log.e("SortDialogFragment", "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}
