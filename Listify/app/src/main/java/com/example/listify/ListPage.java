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

import com.bumptech.glide.Glide;

import com.example.listify.data.Item;
import com.example.listify.data.List;
import com.example.listify.data.ListEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.json.JSONException;

import static com.example.listify.MainActivity.am;

public class ListPage extends AppCompatActivity {
    ListView listView;
    MyAdapter myAdapter;

    Button incrQuan;
    Button decrQuan;
    Button removeItem;

    ArrayList<String> pNames = new ArrayList<>();
    ArrayList<String> pStores = new ArrayList<>();
    ArrayList<String> pPrices = new ArrayList<>();
    ArrayList<String> pQuantity = new ArrayList<>();
    ArrayList<String> pImages = new ArrayList<>();

    ArrayList<ListEntry> pListItemPair = new ArrayList<>();

    Map<String, Double> totalPriceByStore = new HashMap<>();
    Map<String, Integer> storeHeaderIndex = new HashMap<>();

    Requestor requestor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Read list ID from caller
        final int listID = (int) getIntent().getSerializableExtra("listID");

        pNames.add("Total Price");
        pStores.add("");
        pPrices.add("0.00");
        pQuantity.add("-1");
        pImages.add("-1");
        pListItemPair.add(null);

        Properties configs = new Properties();
        try {
            configs = AuthManager.loadProperties(this, "android.resource://" + getPackageName() + "/raw/auths.json");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        requestor = new Requestor(am, configs.getProperty("apiKey"));
        SynchronousReceiver<List> lr = new SynchronousReceiver<>();
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
                    if(!totalPriceByStore.containsKey("Kroger")) {
                        totalPriceByStore.put("Kroger", item.getPrice().doubleValue() * entry.getQuantity());
                        storeHeaderIndex.put("Kroger", pNames.size());

                        double newTotal = Double.parseDouble(pPrices.get(0)) + (item.getPrice().doubleValue() * entry.getQuantity());
                        pPrices.set(0, String.valueOf(newTotal));

                        pNames.add("Kroger");
                        pStores.add("");
                        pPrices.add(totalPriceByStore.get("Kroger").toString());
                        pQuantity.add("-1");
                        pImages.add("-1");
                        pListItemPair.add(null);

                        pNames.add(item.getDescription());
                        pStores.add("Kroger");
                        pPrices.add(item.getPrice().toString());
                        pQuantity.add(entry.getQuantity().toString());
                        pImages.add(item.getImageURL());
                        pListItemPair.add(entry);
                    }
                    else {
                        int index = storeHeaderIndex.get("Kroger");

                        totalPriceByStore.put("Kroger", totalPriceByStore.get("Kroger") + (item.getPrice().doubleValue() * entry.getQuantity()));
                        pPrices.set(index, totalPriceByStore.get("Kroger").toString());

                        double newTotal = Double.parseDouble(pPrices.get(0)) + (item.getPrice().doubleValue() * entry.getQuantity());
                        pPrices.set(0, String.valueOf(newTotal));

                        index++;

                        pNames.add(index, item.getDescription());
                        pStores.add(index, "Kroger");
                        pPrices.add(index, item.getPrice().toString());
                        pQuantity.add(index, entry.getQuantity().toString());
                        pImages.add(index, item.getImageURL());
                        pListItemPair.add(index, entry);

                        for(String store : storeHeaderIndex.keySet()) {
                            if(storeHeaderIndex.get(store) > index) {
                                storeHeaderIndex.put(store, storeHeaderIndex.get(store) + 1);
                            }
                        }
                    }
                }
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.listView);
        myAdapter = new MyAdapter(this, pNames, pStores, pPrices, pQuantity, pImages);

        listView.setAdapter(myAdapter);
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        ArrayList<String> pNames;
        ArrayList<String> pStores;
        ArrayList<String> pPrices;
        ArrayList<String> pQuantity;
        ArrayList<String> pImages;

        MyAdapter (Context c, ArrayList<String> names, ArrayList<String> stores, ArrayList<String> prices, ArrayList<String> quantity, ArrayList<String> images) {
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
                    totalPriceByStore.put(pStores.get(position), totalPriceByStore.get(pStores.get(position)) - Double.parseDouble(pPrices.get(position)));
                    pPrices.set(storeHeaderIndex.get(pStores.get(position)), totalPriceByStore.get(pStores.get(position)).toString());
                    double newTotal = Double.parseDouble(pPrices.get(0)) - Double.parseDouble(pPrices.get(position));
                    pPrices.set(0, String.valueOf(newTotal));
                    ListEntry le = pListItemPair.remove(position);
                    le.setQuantity(le.getQuantity() - 1);
                    pListItemPair.add(position, le);
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
                    totalPriceByStore.put(pStores.get(position), totalPriceByStore.get(pStores.get(position)) + Double.parseDouble(pPrices.get(position)));
                    pPrices.set(storeHeaderIndex.get(pStores.get(position)), totalPriceByStore.get(pStores.get(position)).toString());
                    double newTotal = Double.parseDouble(pPrices.get(0)) + Double.parseDouble(pPrices.get(position));
                    pPrices.set(0, String.valueOf(newTotal));
                    ListEntry le = pListItemPair.remove(position);
                    le.setQuantity(le.getQuantity() + 1);
                    pListItemPair.add(position, le);
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
                    totalPriceByStore.put("Kroger", totalPriceByStore.get("Kroger") - (Double.parseDouble(pPrices.get(position)) * Integer.parseInt(pQuantity.get(position))));
                    pPrices.set(storeHeaderIndex.get(pStores.get(position)), totalPriceByStore.get(pStores.get(position)).toString());

                    double newTotal = Double.parseDouble(pPrices.get(0)) - (Double.parseDouble(pPrices.get(position)) * Integer.parseInt(pQuantity.get(position)));
                    pPrices.set(0, String.valueOf(newTotal));

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
                price.setText("$" + pPrices.get(position));

                if(pQuantity.get(position).equals("-1")) {
                    quantity.setVisibility(View.INVISIBLE);
                    decrQuan.setVisibility(View.INVISIBLE);
                    incrQuan.setVisibility(View.INVISIBLE);
                    removeItem.setVisibility(View.INVISIBLE);
                }
                else {
                    quantity.setText(pQuantity.get(position));
                }

                if(pImages.get(position).equals("-1")) {
                    image.setVisibility(View.INVISIBLE);
                }
                else {
                    Glide.with(getContext()).load(pImages.get(position)).into(image);
                }
            }

            return listproduct;
        }
    }
}
