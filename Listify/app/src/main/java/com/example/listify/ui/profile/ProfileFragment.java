package com.example.listify.ui.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.listify.AuthManager;
import com.example.listify.R;
import com.example.listify.Requestor;

import org.json.JSONException;

import java.io.IOException;
import java.util.Properties;

import static com.example.listify.MainActivity.am;

public class ProfileFragment extends Fragment {
    private Button toDeleteAccountPage;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        toDeleteAccountPage = (Button) root.findViewById(R.id.button);
        toDeleteAccountPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View passwordView = getLayoutInflater().inflate(R.layout.activity_code, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(passwordView);
                builder.setTitle("Account deletion verification");
                builder.setMessage("Are you sure you want to delete your account? If so, enter your password below and hit \"Yes\".");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText passwordText = (EditText) passwordView.findViewById(R.id.editTextCode);
                        String password = passwordText.getText().toString();
                        if(password.equals(am.getPassword())) {
                            try {
                                Properties configs = new Properties();
                                try {
                                    configs = AuthManager.loadProperties(getContext(), "android.resource://" + getActivity().getPackageName() + "/raw/auths.json");
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                                Requestor requestor = new Requestor(am, configs.getProperty("apiKey"));
                                am.deleteUser(requestor);
                                am.nullify();
                                Intent intent = new Intent(getActivity(), com.example.listify.ui.LoginPage.class);
                                startActivity(intent);
                            }
                            catch (Exception e) {
                                Log.i("Authentication", e.toString());
                            }
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return root;
    }
}
