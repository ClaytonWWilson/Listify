package com.example.listify;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


public class CreateListAddDialogFragment extends DialogFragment {

    public interface OnNewListAddListener {
        void sendNewListName(String name, int quantity);
    }

    public OnNewListAddListener onNewAddListListener;

    EditText etNewListName;
    EditText etQuantity;
    Button btnMinus;
    Button btnPlus;

    public CreateListAddDialogFragment() {}


    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View root = inflater.inflate(R.layout.dialog_create_list_add, null);
        builder.setView(root)
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        onNewAddListListener.sendNewListName(etNewListName.getText().toString(), Integer.parseInt(etQuantity.getText().toString()));
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CreateListAddDialogFragment.this.getDialog().cancel();
                    }
                });

        etNewListName = (EditText) root.findViewById(R.id.et_new_list_name);

        // Set up quantity selection
        etQuantity = (EditText) root.findViewById(R.id.et_quantity);

        btnMinus = (Button) root.findViewById(R.id.btn_minus);
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etQuantity.getText().toString().equals("")) {
                    etQuantity.setText("1");
                }

                int curQauntity = Integer.parseInt(etQuantity.getText().toString());
                if (curQauntity > 1) {
                    curQauntity--;
                    etQuantity.setText(String.format("%d", curQauntity));
                }
            }
        });

        btnPlus = (Button) root.findViewById(R.id.btn_plus);
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etQuantity.getText().toString().equals("")) {
                    etQuantity.setText("1");
                }

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
            onNewAddListListener = (OnNewListAddListener) getActivity();
        } catch (ClassCastException e) {
            Log.e("CreateListAddDialogFragment", "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}
