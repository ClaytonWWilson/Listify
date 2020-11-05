package com.example.listify;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.listify.data.ListShare;
import com.example.listify.ui.SignupPage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.json.JSONException;

import static com.example.listify.MainActivity.am;

public class ListPage extends AppCompatActivity implements Requestor.Receiver {
    ListView listView;
    MyAdapter myAdapter;
    Requestor requestor;

    Button incrQuan;
    Button decrQuan;
    Button removeItem;
    Button clearAll;
    Button shareList;

    TextView tvTotalPrice;
    ProgressBar loadingListItems;

    ArrayList<String> pNames = new ArrayList<>();
    ArrayList<String> pStores = new ArrayList<>();
    ArrayList<String> pPrices = new ArrayList<>();
    ArrayList<String> pQuantity = new ArrayList<>();
    ArrayList<String> pImages = new ArrayList<>();

    ArrayList<ListEntry> pListItemPair = new ArrayList<>();

    double totalPrice = 0;
  
    Map<String, Double> totalPriceByStore = new HashMap<>();
    Map<String, Integer> storeHeaderIndex = new HashMap<>();

    DecimalFormat df = new DecimalFormat("0.00");

    // TODO: Display a message if their list is empty
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        final int listID = (int) getIntent().getSerializableExtra("listID");

        Properties configs = new Properties();
        try {
            configs = AuthManager.loadProperties(this, "android.resource://" + getPackageName() + "/raw/auths.json");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        requestor = new Requestor(am, configs.getProperty("apiKey"));
        requestor.getObject(Integer.toString(listID), List.class, this);

        listView = findViewById(R.id.listView);
        myAdapter = new MyAdapter(this, pNames, pStores, pPrices, pQuantity, pImages);
        listView.setAdapter(myAdapter);

        loadingListItems = findViewById(R.id.progress_loading_list_items);
        loadingListItems.setVisibility(View.VISIBLE);

        clearAll = (Button) findViewById(R.id.buttonClear);
        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pNames.clear();
                pStores.clear();
                pPrices.clear();
                pQuantity.clear();
                pImages.clear();

                while(!pListItemPair.isEmpty()) {
                    try {
                        requestor.deleteObject(pListItemPair.remove(0));
                    }
                    catch(Exception e) {}
                }

                totalPrice = 0;
                tvTotalPrice.setText(String.format("$%.2f", totalPrice));
                myAdapter.notifyDataSetChanged();
            }
        });

        shareList = (Button) findViewById(R.id.buttonShare);
        shareList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View codeView = getLayoutInflater().inflate(R.layout.activity_sharedemail, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(ListPage.this);
                builder.setView(codeView);
                builder.setTitle("Share list");
                builder.setMessage("Please enter the email of the user who you want to share the list with.");
                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText sharedEmailText = (EditText) codeView.findViewById(R.id.editTextTextSharedEmail);
                        String sharedEmail = sharedEmailText.getText().toString();
                        ListShare listShare = new ListShare(listID, sharedEmail);
                        try {
                            requestor.postObject(listShare);
                        }
                        catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public void acceptDelivery(Object delivered) {
        List list = (List) delivered;

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

                        pNames.add("Kroger");
                        pStores.add("");
                        pPrices.add(df.format(totalPriceByStore.get("Kroger")));
                        pQuantity.add("-1");
                        pImages.add("-1");
                        pListItemPair.add(null);

                        pNames.add(item.getDescription());
                        pStores.add("Kroger");
                        pPrices.add(df.format(item.getPrice()));
                        pQuantity.add(entry.getQuantity().toString());
                        pImages.add(item.getImageURL());
                        pListItemPair.add(entry);
                    }
                    else {
                        int index = storeHeaderIndex.get("Kroger");

                        totalPriceByStore.put("Kroger", totalPriceByStore.get("Kroger") + (item.getPrice().doubleValue() * entry.getQuantity()));
                        pPrices.set(index, df.format(totalPriceByStore.get("Kroger")));

                        index++;

                        pNames.add(index, item.getDescription());
                        pStores.add(index, "Kroger");
                        pPrices.add(index, df.format(item.getPrice()));
                        pQuantity.add(index, entry.getQuantity().toString());
                        pImages.add(index, item.getImageURL());
                        pListItemPair.add(index, entry);

                        for(String store : storeHeaderIndex.keySet()) {
                            if(storeHeaderIndex.get(store) > index) {
                                storeHeaderIndex.put(store, storeHeaderIndex.get(store) + 1);
                            }
                        }
                    }

                    // Increment total price
                    totalPrice += (item.getPrice().doubleValue() * entry.getQuantity());
                }
            }


            tvTotalPrice = (TextView) findViewById(R.id.total_price);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvTotalPrice.setText(String.format("$%.2f", totalPrice));
                    loadingListItems.setVisibility(View.GONE);
                    myAdapter.notifyDataSetChanged();
                }
            });
        }
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
                    pPrices.set(storeHeaderIndex.get(pStores.get(position)), df.format(totalPriceByStore.get(pStores.get(position))));
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
                    totalPriceByStore.put(pStores.get(position), totalPriceByStore.get(pStores.get(position)) + Double.parseDouble(pPrices.get(position)));
                    pPrices.set(storeHeaderIndex.get(pStores.get(position)), df.format(totalPriceByStore.get(pStores.get(position))));
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
                    totalPriceByStore.put("Kroger", totalPriceByStore.get("Kroger") - (Double.parseDouble(pPrices.get(position)) * Integer.parseInt(pQuantity.get(position))));
                    pPrices.set(storeHeaderIndex.get(pStores.get(position)), df.format(totalPriceByStore.get(pStores.get(position))));

                    totalPrice -= (Double.parseDouble(pPrices.get(position)) *
                            Double.parseDouble(pQuantity.get(position)));
                    tvTotalPrice.setText(String.format("$%.2fllll", totalPrice));

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
                if (Double.parseDouble(pPrices.get(position)) * Double.parseDouble(pQuantity.get(position)) < 0) {
                    price.setText(String.format("$%s", pPrices.get(position)));
                } else {
                    price.setText(String.format("$%s   ($%.2f)", pPrices.get(position), Double.parseDouble(pPrices.get(position)) * Double.parseDouble(pQuantity.get(position))));
                }

                if(pQuantity.get(position).equals("-1")) {
                    quantity.setVisibility(View.GONE);
                    decrQuan.setVisibility(View.GONE);
                    incrQuan.setVisibility(View.GONE);
                    removeItem.setVisibility(View.GONE);
                }
                else {
                    quantity.setText(pQuantity.get(position));
                }

                if(pImages.get(position).equals("-1")) {
                    image.setVisibility(View.GONE);
                }
                else {
                    Glide.with(getContext()).load(pImages.get(position)).into(image);
                }
            }

            return listproduct;
        }
    }
}
