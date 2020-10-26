package com.example.listify;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.listify.data.Item;
import com.example.listify.data.List;
import com.example.listify.data.ListEntry;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import static com.example.listify.MainActivity.am;

public class ListPage extends AppCompatActivity {
    ListView listView;
    MyAdapter myAdapter;

    Button incrQuan;
    Button decrQuan;
    Button removeItem;
    TextView tvTotalPrice;

    ArrayList<String> pNames = new ArrayList<>();
    ArrayList<String> pStores = new ArrayList<>();
    ArrayList<String> pPrices = new ArrayList<>();
    ArrayList<String> pQuantity = new ArrayList<>();
    ArrayList<Integer> pImages = new ArrayList<>();

    ArrayList<ListEntry> pListItemPair = new ArrayList<>();

    Requestor requestor;
    double totalPrice = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Read list ID from caller
        final int listID = (int) getIntent().getSerializableExtra("listID");

        Properties configs = new Properties();
        try {
            configs = AuthManager.loadProperties(this, "android.resource://" + getPackageName() + "/raw/auths.json");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        requestor = new Requestor(am, configs.getProperty("apiKey"));
        SynchronousReceiver<List> lr = new SynchronousReceiver<>();
        //ListReceiver<List> lr = new ListReceiver<>();
        requestor.getObject(Integer.toString(listID), List.class, lr);

        List list;

        try {
            list = lr.await();
        }
        catch (Exception e) {
            list = null;
        }

        if(list != null) {
            for (ListEntry entry : list.getEntries()) {
                int product = entry.getProductID();
                SynchronousReceiver<Item> pr = new SynchronousReceiver<>();
                requestor.getObject(Integer.toString(product), Item.class, pr, pr);
                Item item;
                try {
                    item = pr.await();
                }
                catch (Exception e) {
                    item = null;
                }
                if(item != null) {
                    pNames.add(item.getDescription());
                    pStores.add("Kroger");
                    pPrices.add(item.getPrice().toString());
                    pQuantity.add(entry.getQuantity().toString());
                    pImages.add(R.drawable.placeholder);
                    pListItemPair.add(entry);

                    // Increment total price
                    totalPrice += (item.getPrice().doubleValue() * entry.getQuantity());
                }
            }
        }

        /*pNames.add("Half-gallon organic whole milk");
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
        pImages.add(R.drawable.peanutbutter);*/

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.listView);
        myAdapter = new MyAdapter(this, pNames, pStores, pPrices, pQuantity, pImages);

        listView.setAdapter(myAdapter);

        tvTotalPrice = (TextView) findViewById(R.id.total_price);
        tvTotalPrice.setText(String.format("$%.2f", totalPrice));
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        ArrayList<String> pNames;
        ArrayList<String> pStores;
        ArrayList<String> pPrices;
        ArrayList<String> pQuantity;
        ArrayList<Integer> pImages;

        MyAdapter (Context c, ArrayList<String> names, ArrayList<String> stores, ArrayList<String> prices, ArrayList<String> quantity, ArrayList<Integer> images) {
            super(c, R.layout.activity_listproductentry, R.id.productView, names);
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
            View listproduct = layoutInflater.inflate(R.layout.activity_listproductentry, parent,false);

            decrQuan = (Button) listproduct.findViewById(R.id.buttonDecr);
            incrQuan = (Button) listproduct.findViewById(R.id.buttonIncr);
            removeItem = (Button) listproduct.findViewById(R.id.buttonDel);

            decrQuan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int q = Integer.parseInt(pQuantity.get(position)) - 1;
                    pQuantity.set(position, Integer.toString(q));
                    ListEntry le = pListItemPair.remove(position);
                    le.setQuantity(le.getQuantity() - 1);
                    pListItemPair.add(position, le);

                    totalPrice -= Double.parseDouble(pPrices.get(position));
                    tvTotalPrice.setText(String.format("$%.2f", totalPrice));

                    SynchronousReceiver<Integer> synchronousenforcer = new SynchronousReceiver<>();
                    requestor.deleteObject(le, synchronousenforcer, synchronousenforcer);
                    try {
                        synchronousenforcer.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        requestor.postObject(le, synchronousenforcer, synchronousenforcer);
                        synchronousenforcer.await();
                    }
                    catch (Exception e) {
                        Log.i("Authentication", e.toString());
                    }
                    listView.setAdapter(myAdapter);
                }
            });
            if(Integer.parseInt(pQuantity.get(position)) <= 1) {
                decrQuan.setEnabled(false);
            }

            incrQuan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int q = Integer.parseInt(pQuantity.get(position)) + 1;
                    pQuantity.set(position, Integer.toString(q));
                    ListEntry le = pListItemPair.remove(position);
                    le.setQuantity(le.getQuantity() + 1);
                    pListItemPair.add(position, le);

                    totalPrice += Double.parseDouble(pPrices.get(position));
                    tvTotalPrice.setText(String.format("$%.2f", totalPrice));

                    SynchronousReceiver<Integer> synchronousenforcer = new SynchronousReceiver<>();
                    requestor.deleteObject(le, synchronousenforcer, synchronousenforcer);
                    try {
                        synchronousenforcer.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        requestor.postObject(le, synchronousenforcer, synchronousenforcer);
                        synchronousenforcer.await();
                    }
                    catch (Exception e) {
                        Log.i("Authentication", e.toString());
                    }
                    listView.setAdapter(myAdapter);
                }
            });
            if(Integer.parseInt(pQuantity.get(position)) > 1) {
                decrQuan.setEnabled(true);
            }

            removeItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    totalPrice -= (Double.parseDouble(pPrices.get(position)) *
                            Double.parseDouble(pQuantity.get(position)));
                    tvTotalPrice.setText(String.format("$%.2f", totalPrice));

                    pNames.remove(position);
                    pStores.remove(position);
                    pPrices.remove(position);
                    pQuantity.remove(position);
                    pImages.remove(position);

                    requestor.deleteObject(pListItemPair.remove(position));

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

    class ListReceiver<T> implements Requestor.Receiver<T> {
        @Override
        public void acceptDelivery(T delivered) {
            for(ListEntry entry : ((List) delivered).getEntries()) {
                int product = entry.getProductID();
                ProductReceiver<Item> pr = new ProductReceiver<>();
                requestor.getObject(Integer.toString(product), Item.class, pr);
                pQuantity.add(entry.getQuantity().toString());
                pListItemPair.add(entry);
            }
        }
    }

    class ProductReceiver<T> implements Requestor.Receiver<T> {
        @Override
        public void acceptDelivery(T delivered) {
            Item i = (Item) delivered;
            pNames.add(i.getDescription());
            pStores.add("Kroger");
            pPrices.add(i.getPrice().toString());
            pImages.add(R.drawable.placeholder);
        }
    }
}
