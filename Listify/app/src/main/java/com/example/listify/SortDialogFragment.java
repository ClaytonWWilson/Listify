package com.example.listify;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
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
        builder.setView(root);

        TextView tvSortNone = root.findViewById(R.id.sort_none);
        TextView tvSortName = root.findViewById(R.id.sort_name);
        TextView tvSortPrice = root.findViewById(R.id.sort_price);
        TextView tvSortStore = root.findViewById(R.id.sort_store);

        tvSortNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tvSortName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tvSortPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tvSortStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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
