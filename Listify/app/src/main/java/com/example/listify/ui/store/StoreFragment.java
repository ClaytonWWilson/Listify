package com.example.listify.ui.store;

import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.listify.R;

public class StoreFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_stores, container, false);

        TextView krogerText = (TextView) root.findViewById(R.id.textView11);
        krogerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("https://www.kroger.com/");
            }
        });

        TextView kohlsText = (TextView) root.findViewById(R.id.textView12);
        krogerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("https://www.kohls.com/");
            }
        });

        TextView ebayText = (TextView) root.findViewById(R.id.textView13);
        krogerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("https://www.ebay.com/");
            }
        });

        return root;
    }

    private void gotoUrl(String url) {
        Uri u = Uri.parse(url);
        startActivity(new Intent(Intent.ACTION_VIEW, u));
    }
}
