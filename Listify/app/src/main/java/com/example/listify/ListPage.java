package com.example.listify;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.listify.ui.home.HomeFragment;
import com.bumptech.glide.Glide;
import com.example.listify.data.*;
import org.json.JSONException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.example.listify.MainActivity.am;

public class ListPage extends AppCompatActivity implements Requestor.Receiver, RenameListDialogFragment.OnRenameListListener {
    ListView listView;
    MyAdapter myAdapter;
    Requestor requestor;
    SwipeRefreshLayout refreshList;

    Button incrQuan;
    Button decrQuan;
    Button removeItem;
    Button clearAll;
    Button shareList;

    TextView tvTotalPrice;
    TextView emptyMessage;

    ProgressBar loadingListItems;

    ArrayList<String> pNames = new ArrayList<>();
    ArrayList<String> pStores = new ArrayList<>();
    ArrayList<String> pPrices = new ArrayList<>();
    ArrayList<String> pQuantity = new ArrayList<>();
    ArrayList<String> pImages = new ArrayList<>();

    ArrayList<ListEntry> pListItemPair = new ArrayList<>();

    double totalPrice = 0;

    HashMap<Integer, String> storeID2Name = new HashMap<>();
    Map<String, Double> totalPriceByStore = new HashMap<>();
    Map<String, Integer> storeHeaderIndex = new HashMap<>();

    DecimalFormat df = new DecimalFormat("0.00");
    List selectedList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        selectedList = (List) getIntent().getSerializableExtra("selectedList");
        setTitle(selectedList.getName());

        Properties configs = new Properties();
        try {
            configs = AuthManager.loadProperties(this, "android.resource://" + getPackageName() + "/raw/auths.json");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        requestor = new Requestor(am, configs.getProperty("apiKey"));
        requestor.getObject(Integer.toString(selectedList.getListID()), List.class, this);

        listView = findViewById(R.id.listView);
        myAdapter = new MyAdapter(this, pNames, pStores, pPrices, pQuantity, pImages);
        listView.setAdapter(myAdapter);

        loadingListItems = findViewById(R.id.progress_loading_list_items);
        loadingListItems.setVisibility(View.VISIBLE);

        tvTotalPrice = (TextView) findViewById(R.id.total_price);
        emptyMessage = (TextView) findViewById(R.id.textViewEmpty2);

        /*clearAll = (Button) findViewById(R.id.buttonClear);
        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pNames.clear();
                pStores.clear();
                pPrices.clear();
                pQuantity.clear();
                pImages.clear();

                emptyMessage.setVisibility(View.VISIBLE);

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
                      
                        ListShare listShare = new ListShare(LIST_ID, sharedEmail, "Read, Write, Delete, Share", null);
                        try {
                            requestor.putObject(listShare);
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
        });*/

        refreshList = (SwipeRefreshLayout) findViewById(R.id.refresh_list);
        refreshList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Properties configs = new Properties();
                try {
                    configs = AuthManager.loadProperties(ListPage.this, "android.resource://" + getPackageName() + "/raw/auths.json");
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

                requestor = new Requestor(am, configs.getProperty("apiKey"));
                requestor.getObject(Integer.toString(selectedList.getListID()), List.class, ListPage.this);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list, menu);

        MenuItem renameItem = menu.findItem(R.id.action_rename_list);
        renameItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                RenameListDialogFragment renameListDialog = new RenameListDialogFragment();
                renameListDialog.show(getSupportFragmentManager(), "Rename List");
                return false;
            }
        });

        MenuItem shareItem = menu.findItem(R.id.action_share_list);
        shareItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent listSharees = new Intent(ListPage.this, ListSharees.class);
                listSharees.putExtra("listID", selectedList.getListID());
                ListPage.this.startActivity(listSharees);
                return false;
            }
        });

        MenuItem duplicateItem = menu.findItem(R.id.action_duplicate_list);
        duplicateItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ListDuplicate duplicate = new ListDuplicate(selectedList.getListID(), String.format("%s copy", selectedList.getName()));

                Properties configs = new Properties();
                try {
                    configs = AuthManager.loadProperties(ListPage.this, "android.resource://" + getPackageName() + "/raw/auths.json");
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

                requestor = new Requestor(am, configs.getProperty("apiKey"));
                try {
                    requestor.postObject(duplicate);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(ListPage.this, "List duplicated", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        MenuItem exportItem = menu.findItem(R.id.action_export_list);
        exportItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                StringBuilder listContents = new StringBuilder();

                for (int i = 0; i < pNames.size(); i++) {
                    if (Integer.parseInt(pQuantity.get(i)) == -1) {
                        listContents.append(String.format("\n%s\n", pNames.get(i)));
                    } else {
                        listContents.append(String.format("    %s of %s\n", pQuantity.get(i), pNames.get(i)));
                    }
                }

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, listContents.toString());
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);

                Toast.makeText(ListPage.this, "Export List", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        return true;
    }

    @Override
    public void acceptDelivery(Object delivered) {
        // Clear out old values
        pNames.clear();
        pStores.clear();
        pPrices.clear();
        pQuantity.clear();
        pImages.clear();
        totalPriceByStore.clear();
        storeID2Name.clear();
        storeHeaderIndex.clear();
        pListItemPair.clear();
        totalPrice = 0;

        List list = (List) delivered;

        if(list != null) {
            for (ListEntry entry : list.getEntries()) {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        emptyMessage.setVisibility(View.GONE);
                    }
                });
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
                    int storeID = (Integer)(item.getChainID());
                    if(!storeID2Name.containsKey(storeID)) {
                        Properties configs = new Properties();
                        try {
                            configs = AuthManager.loadProperties(this, "android.resource://" + getPackageName() + "/raw/auths.json");
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }

                        SynchronousReceiver<Chain> chainReciever = new SynchronousReceiver<>();
                        Requestor requestor = new Requestor(am, configs.getProperty("apiKey"));
                        requestor.getObject(Integer.toString(item.getChainID()), Chain.class, chainReciever);

                        try {
                            storeID2Name.put(storeID, chainReciever.await().getName());
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if(!totalPriceByStore.containsKey(storeID2Name.get(storeID))) {
                        totalPriceByStore.put(storeID2Name.get(storeID), item.getPrice().doubleValue() * entry.getQuantity());
                        storeHeaderIndex.put(storeID2Name.get(storeID), pNames.size());

                        pNames.add(storeID2Name.get(storeID));
                        pStores.add("");
                        pPrices.add(df.format(totalPriceByStore.get(storeID2Name.get(storeID))));
                        pQuantity.add("-1");
                        pImages.add("-1");
                        pListItemPair.add(null);

                        pNames.add(item.getDescription());
                        pStores.add(storeID2Name.get(storeID));
                        pPrices.add(df.format(item.getPrice()));
                        pQuantity.add(entry.getQuantity().toString());
                        pImages.add(item.getImageURL());
                        pListItemPair.add(entry);
                    }
                    else {
                        int index = storeHeaderIndex.get(storeID2Name.get(storeID));

                        totalPriceByStore.put(storeID2Name.get(storeID), totalPriceByStore.get(storeID2Name.get(storeID)) + (item.getPrice().doubleValue() * entry.getQuantity()));
                        pPrices.set(index, df.format(totalPriceByStore.get(storeID2Name.get(storeID))));

                        index++;

                        pNames.add(index, item.getDescription());
                        pStores.add(index, storeID2Name.get(storeID));
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

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvTotalPrice.setText(String.format("$%.2f", totalPrice));
                    loadingListItems.setVisibility(View.GONE);
                    myAdapter.notifyDataSetChanged();
                }
            });
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTotalPrice.setText(String.format("$%.2f", totalPrice));
            }
        });

        refreshList.setRefreshing(false);
    }

    @Override
    public void sendRenameListName(String name) {
        selectedList.setName(name);

        Properties configs = new Properties();
        try {
            configs = AuthManager.loadProperties(ListPage.this, "android.resource://" + getPackageName() + "/raw/auths.json");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        requestor = new Requestor(am, configs.getProperty("apiKey"));
        try {
            requestor.putObject(selectedList);
        } catch (Exception e) {
            e.printStackTrace();
        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTitle(name);
                Toast.makeText(ListPage.this, "List Renamed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        ArrayList<String> pNames;
        ArrayList<String> pStores;
        ArrayList<String> pPrices;
        ArrayList<String> pQuantity;
        ArrayList<String> pImages;


        MyAdapter (Context c, ArrayList<String> names, ArrayList<String> stores, ArrayList<String> prices, ArrayList<String> quantity, ArrayList<String> images) {
            super(c, R.layout.shopping_list_product_entry, R.id.productView, names);
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
            View listproduct = layoutInflater.inflate(R.layout.shopping_list_product_entry, parent,false);

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
                    myAdapter.notifyDataSetChanged();
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
                    myAdapter.notifyDataSetChanged();
                }
            });
            if(Integer.parseInt(pQuantity.get(position)) > 1) {
                decrQuan.setEnabled(true);
            }

            removeItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String storeName = pStores.remove(position);

                    totalPriceByStore.put(storeName, totalPriceByStore.get(storeName) - (Double.parseDouble(pPrices.get(position)) * Integer.parseInt(pQuantity.get(position))));
                    pPrices.set(storeHeaderIndex.get(storeName), df.format(totalPriceByStore.get(storeName)));

                    totalPrice -= (Double.parseDouble(pPrices.get(position)) * Double.parseDouble(pQuantity.get(position)));
                    tvTotalPrice.setText(String.format("$%.2f", totalPrice));

                    pNames.remove(position);
                    //pStores.remove(position);
                    pPrices.remove(position);
                    pQuantity.remove(position);
                    pImages.remove(position);
                    requestor.deleteObject(pListItemPair.remove(position));

                    for(String str : storeHeaderIndex.keySet()) {
                        if(storeHeaderIndex.get(str) > position) {
                            storeHeaderIndex.put(str, storeHeaderIndex.get(str) - 1);
                        }
                    }

                    if(String.format("$%.2f", totalPriceByStore.get(storeName)).equals("$0.00") || String.format("$%.2f", totalPriceByStore.get(storeName)).equals("$-0.00")) {
                        int index = storeHeaderIndex.remove(storeName);

                        pNames.remove(index);
                        pStores.remove(index);
                        pPrices.remove(index);
                        pQuantity.remove(index);
                        pImages.remove(index);
                        pListItemPair.remove(index);

                        for(String str : storeHeaderIndex.keySet()) {
                            if(storeHeaderIndex.get(str) > index) {
                                storeHeaderIndex.put(str, storeHeaderIndex.get(str) - 1);
                            }
                        }
                    }

                    if(pNames.isEmpty()) {
                        emptyMessage.setVisibility(View.VISIBLE);
                    }

                    myAdapter.notifyDataSetChanged();
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
                if (Double.parseDouble(pPrices.get(position)) * Double.parseDouble(pQuantity.get(position)) <= 0) {
                    price.setText(String.format("$%s", pPrices.get(position)));
                } else {
                    price.setText(String.format("$%s   ($%.2f)", pPrices.get(position), Double.parseDouble(pPrices.get(position)) * Double.parseDouble(pQuantity.get(position))));
                }

                if(pQuantity.get(position).equals("-1")) {
                    quantity.setVisibility(View.GONE);
                    decrQuan.setVisibility(View.GONE);
                    incrQuan.setVisibility(View.GONE);
                    removeItem.setVisibility(View.GONE);

                    listproduct.setBackgroundColor(Color.parseColor("#BBBBBB"));

                    ConstraintLayout constraintLayout = listproduct.findViewById(R.id.constraintLayout);
                    constraintLayout.setMaxHeight(250);

                    name.setPadding(0, 175, 0, 0);
                    price.setPadding(0, 175, 0, 0);
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
