package com.example.listify;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.listify.adapter.ShareeSwipeableAdapter;
import com.example.listify.adapter.ShoppingListsSwipeableAdapter;
import com.example.listify.data.Chain;
import com.example.listify.data.Item;
import com.example.listify.data.List;
import com.example.listify.data.ListEntry;
import com.example.listify.data.ListShare;

import org.json.JSONException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.listify.MainActivity.am;

public class ListSharees extends AppCompatActivity implements Requestor.Receiver {
    ShareeSwipeableAdapter myAdapter;
    Requestor requestor;
    ProgressBar loadingListItems;


    DecimalFormat df = new DecimalFormat("0.00");

    // TODO: Display a message if their list is empty
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listofsharees);

        final int listID = (int) getIntent().getSerializableExtra("listID");

        Properties configs = new Properties();
        try {
            configs = AuthManager.loadProperties(this, "android.resource://" + getPackageName() + "/raw/auths.json");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        requestor = new Requestor(am, configs.getProperty("apiKey"));
        requestor.getObject(Integer.toString(listID), ListShare.class, this);

        loadingListItems = findViewById(R.id.progress_loading_list_items);
        loadingListItems.setVisibility(View.VISIBLE);
    }

    @Override
    public void acceptDelivery(Object delivered) {
        ListShare sharee = (ListShare) delivered;

        if(sharee != null) {
            SynchronousReceiver<ListShare> listShareReceiver = new SynchronousReceiver<>();
            requestor.getObject(Integer.toString(sharee.getListID()), ListShare.class, listShareReceiver, listShareReceiver);

            ArrayList<ListShare> resultList = new ArrayList<>();
            ListShare result;

            try {
                result = listShareReceiver.await();
            }
            catch (Exception e) {
                e.printStackTrace();
                result = null;
            }

            if(result != null) {
                resultList.add(result);

                for(ListShare r : result.getEntries()) {
                    resultList.add(r);
                }

                myAdapter = new ShareeSwipeableAdapter(this, resultList);
            }
        }
    }
}
