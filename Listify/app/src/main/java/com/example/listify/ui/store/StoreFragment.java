package com.example.listify.ui.store;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.listify.ListSharees;
import com.example.listify.R;
import com.example.listify.data.ListShare;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.example.listify.MainActivity.am;

public class StoreFragment extends Fragment {
    ListView listView;
    StoreFragment.MyAdapter myAdapter;

    ArrayList<Integer> storeLogos = new ArrayList<>();
    ArrayList<String> storeNames = new ArrayList<>();
    ArrayList<String> storeURLs = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_stores, container, false);

        storeLogos.add(-1);
        storeLogos.add(R.drawable.kroger);
        storeLogos.add(R.drawable.kohls);
        storeLogos.add(R.drawable.ebay);

        storeNames.add("");
        storeNames.add("Kroger");
        storeNames.add("Kohl's");
        storeNames.add("Ebay");

        storeURLs.add("");
        storeURLs.add("https://www.kroger.com/");
        storeURLs.add("https://www.kohls.com/");
        storeURLs.add("https://www.ebay.com/");

        listView = root.findViewById(R.id.listOfStores);
        myAdapter = new StoreFragment.MyAdapter(this.getContext(), storeLogos, storeNames, storeNames);
        listView.setAdapter(myAdapter);

        return root;
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        ArrayList<Integer> storeLogos = new ArrayList<>();
        ArrayList<String> storeNames = new ArrayList<>();
        ArrayList<String> storeURLs = new ArrayList<>();

        MyAdapter (Context c, ArrayList<Integer> logos, ArrayList<String> names, ArrayList<String> urls) {
            super(c, R.layout.shopping_list_product_entry, R.id.productView, names);
            context = c;
            storeLogos = logos;
            storeNames = names;
            storeURLs = urls;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View listproduct = layoutInflater.inflate(R.layout.shopping_list_product_entry, parent,false);

            TextView store = listproduct.findViewById(R.id.storeView);
            store.setVisibility(View.GONE);
            TextView price = listproduct.findViewById(R.id.priceView);
            price.setVisibility(View.GONE);
            TextView quantity = listproduct.findViewById(R.id.quantityView);
            quantity.setVisibility(View.GONE);

            Button increase = listproduct.findViewById(R.id.buttonIncr);
            increase.setVisibility(View.GONE);
            Button decrease = listproduct.findViewById(R.id.buttonDecr);
            decrease.setVisibility(View.GONE);
            Button remove = listproduct.findViewById(R.id.buttonDel);
            remove.setVisibility(View.GONE);

            ImageView image = listproduct.findViewById(R.id.imageView);
            if(position == 0) {
                image.setVisibility(View.GONE);
            }
            else {
                image.setImageResource(storeLogos.get(position));
            }

            TextView name = listproduct.findViewById(R.id.productView);
            if(position == 0) {
                name.setVisibility(View.GONE);
            }
            else {
                name.setText(storeNames.get(position));
                name.setTextColor(Color.parseColor("#0000FF"));
                name.setTextSize(20);
                name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoUrl(storeURLs.get(position));
                    }
                });
            }

            if(position == 0) {
                ConstraintLayout constraintLayout = listproduct.findViewById(R.id.constraintLayout);
                constraintLayout.setMaxHeight(0);
            }

            return listproduct;
        }
    }

    private void gotoUrl(String url) {
        Uri u = Uri.parse(url);
        startActivity(new Intent(Intent.ACTION_VIEW, u));
    }
}
