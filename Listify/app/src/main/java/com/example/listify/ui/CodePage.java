package com.example.listify.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.listify.AuthManager;
import com.example.listify.R;
import com.example.listify.MainActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class CodePage extends AppCompatDialogFragment {
    private EditText ediTextCode;

    private CodeDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater =  getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_code, null);

        builder.setView(view)
                .setTitle("Verification code")
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String code = ediTextCode.getText().toString();
                        listener.sendCode("" + code + "", false);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String code = ediTextCode.getText().toString();
                        listener.sendCode("" + code + "", true);
                    }
                });

        ediTextCode = view.findViewById(R.id.editTextCode);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (CodeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("CodeDialogListener not implemented.");
        }
    }

    public interface CodeDialogListener {
        void sendCode(String code, boolean cancel);
    }
}