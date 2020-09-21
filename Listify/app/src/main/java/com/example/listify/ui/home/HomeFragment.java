package com.example.listify.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.listify.R;

public class HomeFragment extends Fragment {
    private Button toLoginPage;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        toLoginPage = (Button) root.findViewById(R.id.button1);
        toLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeFragment.this.getActivity(), com.example.listify.ui.SignupPage.class);
                startActivity(intent);
            }
        });
        return root;
    }
}