package com.example.listify;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


public class RenameListDialogFragment extends DialogFragment {

    public interface OnRenameListListener {
        void sendRenameListName(String name);
    }

    public OnRenameListListener onRenameListListener;

    EditText etRenameListName;

    public RenameListDialogFragment() {}


    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View root = inflater.inflate(R.layout.dialog_rename_list, null);
        builder.setView(root)
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        onRenameListListener.sendRenameListName(etRenameListName.getText().toString());
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RenameListDialogFragment.this.getDialog().cancel();
                    }
                });

        etRenameListName = (EditText) root.findViewById(R.id.et_renamed_list_name);

        return builder.create();
    }

    // Required to extend DialogFragment
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onRenameListListener = (OnRenameListListener) getTargetFragment();
            if (onRenameListListener == null) {
                onRenameListListener = (OnRenameListListener) getActivity();
            }
        } catch (ClassCastException e) {
            Log.e("CreateListDialogFragment", "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}
