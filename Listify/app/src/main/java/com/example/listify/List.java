package com.example.listify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class List extends AppCompatActivity {
    ListView listView;
    MyAdapter myAdapter;

    Button incrQuan;
    Button decrQuan;
    Button removeItem;

    ArrayList<String> pNames = new ArrayList<>(); //String[] pNames = {"Half-gallon organic whole milk"};
    ArrayList<String> pStores = new ArrayList<>(); //String[] pStores = {"Kroger"};
    ArrayList<String> pPrices = new ArrayList<>(); //String[] pPrices = {"$5.00"};
    ArrayList<String> pQuantity = new ArrayList<>(); //String[] pQuantity = {"1"};
    ArrayList<Integer> pImages = new ArrayList<>(); //int[] pImages = {R.drawable.milk};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        pNames.add("Half-gallon organic whole milk");
        pStores.add("Kroger");
        pPrices.add("$5.00");
        pQuantity.add("1");
        pImages.add(R.drawable.milk);

        pNames.add("5-bunch medium bananas");
        pStores.add("Kroger");
        pPrices.add("$3.00");
        pQuantity.add("1");
        pImages.add(R.drawable.bananas);

        pNames.add("JIF 40-oz creamy peanut butter");
        pStores.add("Kroger");
        pPrices.add("$7.00");
        pQuantity.add("1");
        pImages.add(R.drawable.peanutbutter);

        int currSize = pNames.size();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.listView);
        myAdapter = new MyAdapter(this, pNames, pStores, pPrices, pQuantity, pImages);

        listView.setAdapter(myAdapter);
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        ArrayList<String> pNames; //String[] pNames;
        ArrayList<String> pStores; //String[] pStores;
        ArrayList<String> pPrices; //String[] pPrices;
        ArrayList<String> pQuantity; //String[] pQuantity;
        ArrayList<Integer> pImages; //int[] pImages;

        MyAdapter (Context c, ArrayList<String> names, ArrayList<String> stores, ArrayList<String> prices, ArrayList<String> quantity, ArrayList<Integer> images) {
            super(c, R.layout.listproduct, R.id.productView, names);
            context = c;
            pNames = names;
            pStores = stores;
            pPrices = prices;
            pQuantity = quantity;
            pImages = images;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View listproduct = layoutInflater.inflate(R.layout.listproduct, parent,false);

            decrQuan = (Button) listproduct.findViewById(R.id.buttonDecr);
            incrQuan = (Button) listproduct.findViewById(R.id.buttonIncr);
            removeItem = (Button) listproduct.findViewById(R.id.buttonDel);

            decrQuan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int q = Integer.parseInt(pQuantity.get(position)) - 1;
                    pQuantity.set(position, Integer.toString(q));
                    listView.setAdapter(myAdapter);
                }
            });
            if(Integer.parseInt(pQuantity.get(position)) <= 1) {
                decrQuan.setEnabled(false);
            }
            if(Integer.parseInt(pQuantity.get(position)) < 10) {
                incrQuan.setEnabled(true);
            }

            incrQuan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int q = Integer.parseInt(pQuantity.get(position)) + 1;
                    pQuantity.set(position, Integer.toString(q));
                    listView.setAdapter(myAdapter);
                }
            });
            if(Integer.parseInt(pQuantity.get(position)) > 1) {
                decrQuan.setEnabled(true);
            }
            if(Integer.parseInt(pQuantity.get(position)) >= 10) {
                incrQuan.setEnabled(false);
            }

            removeItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pNames.remove(position);
                    pStores.remove(position);
                    pPrices.remove(position);
                    pQuantity.remove(position);
                    pImages.remove(position);
                    listView.setAdapter(myAdapter);
                }
            });

            TextView name = listproduct.findViewById(R.id.productView);
            TextView store = listproduct.findViewById(R.id.storeView);
            TextView price = listproduct.findViewById(R.id.priceView);
            TextView quantity = listproduct.findViewById(R.id.quantityView);
            ImageView image = listproduct.findViewById(R.id.imageView);

            if(!pNames.isEmpty()) {
                name.setText(pNames.get(position));
                store.setText(pStores.get(position));
                price.setText(pPrices.get(position));
                quantity.setText(pQuantity.get(position));
                image.setImageResource(pImages.get(position));
            }

            return listproduct;
        }
    }
}
