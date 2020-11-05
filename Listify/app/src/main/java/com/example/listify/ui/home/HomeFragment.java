package com.example.listify.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class HomeFragment extends Fragment {
    private Button toDeleteAccountPage;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

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

//                                try {
//                                    am.changePassword(am.getEmail());
//                                }
//                                catch (Exception e) {}
                                /*try {
                                    am.confirmPasswordReset("", "");
                                }
                                
                                String[] TO = {am.getEmail()};
                                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                emailIntent.setData(Uri.parse("mailto:"));
                                emailIntent.setType("text/plain");
                                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Listify account deleted");
                                emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello, this email is to confirm that you have deleted your Listify account.");
                                try {
                                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                                    Log.i("Finished sending email...", "");
                                    System.out.println("A");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    System.out.println("B");
                                }*/

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