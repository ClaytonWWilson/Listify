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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
    ArrayList<Integer> pImages = new ArrayList<>();

    ArrayList<ListEntry> pListItemPair = new ArrayList<>();

    Map<String, Integer> numItemsFromStore = new HashMap<>();

    Requestor requestor;

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
                    if(!numItemsFromStore.containsKey("Kroger") || numItemsFromStore.get("Kroger") == 0) {
                        pNames.add("Kroger");
                        pStores.add("");
                        pPrices.add("$?.??");
                        pQuantity.add("0");
                        pImages.add(-1);
                        pListItemPair.add(null);

                        numItemsFromStore.put("Kroger", 1);

                        pNames.add(item.getDescription());
                        pStores.add("Kroger");
                        pPrices.add(item.getPrice().toString());
                        pQuantity.add(entry.getQuantity().toString());
                        pImages.add(R.drawable.placeholder);
                        pListItemPair.add(entry);
                    }
                    else {
                        int index = 0;

                        while(index < pNames.size() && pNames.get(index).equals("Kroger")) {
                            index++;
                        }

                        index++;

                        pNames.add(index, item.getDescription());
                        pStores.add(index, "Kroger");
                        pPrices.add(index, item.getPrice().toString());
                        pQuantity.add(index, entry.getQuantity().toString());
                        pImages.add(index, R.drawable.placeholder);
                        pListItemPair.add(index, entry);

                        numItemsFromStore.put("Kroger", numItemsFromStore.get("Kroger") + 1);
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
}
