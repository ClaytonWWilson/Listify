package com.example.listify.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.view.ViewGroup;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import static com.example.listify.MainActivity.am;

import com.example.listify.AuthManager;
import com.example.listify.MainActivity;
import com.example.listify.R;
import com.example.listify.Requestor;
import com.example.listify.ui.LoginPage;

import org.json.JSONException;

import java.io.IOException;
import java.util.Properties;

public class HomeFragment extends Fragment {
    private Button toLoginPage;
    private Button toListPage;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        toLoginPage = (Button) root.findViewById(R.id.button1);
        toLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), com.example.listify.ui.LoginPage.class);
                startActivity(intent);
            }
        });

        toListPage = (Button) root.findViewById(R.id.button2);
        toListPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(HomeFragment.this.getActivity(), com.example.listify.ListPage.class);
                //startActivity(intent);

                try {
                    Properties configs = new Properties();
                    try {
                        configs = AuthManager.loadProperties(getContext(), "android.resource://" + getActivity().getPackageName() + "/raw/auths.json");
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    Requestor requestor = new Requestor(am, configs.getProperty("apiKey"));
                    am.deleteUser(requestor);
                }
                catch(Exception ex) {}
            }
        });

        return root;
    }
}