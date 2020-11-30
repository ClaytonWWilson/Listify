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
    ListView listView;
    MyAdapter myAdapter;
    Requestor requestor;

    Button removeSharee;

    ArrayList<String> lShareeEmails = new ArrayList<>();

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

        listView = findViewById(R.id.listOfSharees);
        myAdapter = new MyAdapter(this, lShareeEmails);
        listView.setAdapter(myAdapter);
    }

    @Override
    public void acceptDelivery(Object delivered) {
        ListShare sharee = (ListShare) delivered;

        if(sharee != null) {
            lShareeEmails.add(sharee.getShareWithEmail());

            if(sharee.getEntries() != null) {
                for(ListShare ls : sharee.getEntries()) {
                    lShareeEmails.add(ls.getShareWithEmail());
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    myAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        ArrayList<String> lShareeEmails;

        MyAdapter (Context c, ArrayList<String> shareeEmails) {
            super(c, R.layout.shopping_list_sharee_entry, R.id.textView14, shareeEmails);
            context = c;
            lShareeEmails = shareeEmails;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View listproduct = layoutInflater.inflate(R.layout.shopping_list_sharee_entry, parent,false);

            TextView shareeEmail = listproduct.findViewById(R.id.textView14);
            if(!lShareeEmails.isEmpty()) {
                shareeEmail.setText(lShareeEmails.get(position));
            }

            removeSharee = (Button) listproduct.findViewById(R.id.button4);
            removeSharee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lShareeEmails.remove(position);
                    myAdapter.notifyDataSetChanged();
                }
            });

            return listproduct;
        }
    }
}
