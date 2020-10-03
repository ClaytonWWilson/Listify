package com.example.listify;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class List extends AppCompatActivity {
    ListView listView;
    String listName = "Sample List";
    String[] pNames = {"Half-gallon organic whole milk"};
    String[] pStores = {"Kroger"};
    String[] pPrices = {"$5.00"};
    int[] pImages = {R.drawable.milk};

    //List(String name) {
    //    listName = name;
    //}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.listView);

        MyAdapter myAdapter = new MyAdapter(this, pNames, pStores, pPrices, pImages);
        listView.setAdapter(myAdapter);
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        String[] pNames;
        String[] pStores;
        String[] pPrices;
        int[] pImages;

        MyAdapter (Context c, String[] names, String[] stores, String[] prices, int[] images) {
            super(c, R.layout.listproduct, R.id.productView, names);
            context = c;
            pNames = names;
            pStores = stores;
            pPrices = prices;
            pImages = images;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View listproduct = layoutInflater.inflate(R.layout.listproduct, parent,false);

            ImageView image = listproduct.findViewById(R.id.imageView);
            TextView name = listproduct.findViewById(R.id.productView);
            TextView store = listproduct.findViewById(R.id.storeView);
            TextView price = listproduct.findViewById(R.id.priceView);

            image.setImageResource(pImages[position]);
            name.setText(pNames[position]);
            store.setText(pStores[position]);
            price.setText(pPrices[position]);

            return listproduct;
        }
    }
}
