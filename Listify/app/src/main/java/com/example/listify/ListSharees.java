package com.example.listify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.listify.data.ListShare;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import static com.example.listify.MainActivity.am;

public class ListSharees extends AppCompatActivity implements Requestor.Receiver {
    ListView listView;
    MyAdapter myAdapter;
    Requestor requestor;

    Button shareList;
    Button removeSharee;

    ArrayList<ListShare> lShareeEntries = new ArrayList<>();
    ArrayList<String> lShareeEmails = new ArrayList<>();

    private final int CONFIRM_REQUEST_CODE = 1;

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

        shareList = (Button) findViewById(R.id.buttonShare);
        shareList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText sharedEmailText = (EditText) findViewById(R.id.editTextShareeEmail);
                String sharedEmail = sharedEmailText.getText().toString();
                Intent confirmIntent = new Intent(ListSharees.this, ConfirmShareView.class);
                confirmIntent.putExtra("listID", listID);
                confirmIntent.putExtra("shareeEmail", sharedEmail);
                startActivityForResult(confirmIntent, CONFIRM_REQUEST_CODE);

            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONFIRM_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Integer listID = data.getIntExtra("listID", -1);
                String shareeEmail = data.getStringExtra("shareeEmail");
                ListShare listShare = new ListShare(listID, shareeEmail, "Read, Write, Delete, Share", null);
                lShareeEntries.add(listShare);
                lShareeEmails.add(shareeEmail);
                myAdapter.notifyDataSetChanged();

            } else { //resultCode == RESULT_CANCELED

            }
        }
    }

    @Override
    public void acceptDelivery(Object delivered) {
        ListShare sharee = (ListShare) delivered;

        if(sharee != null) {
            lShareeEntries.add(sharee);
            lShareeEmails.add(sharee.getShareWithEmail());

            if(sharee.getEntries() != null) {
                for(ListShare ls : sharee.getEntries()) {
                    lShareeEntries.add(ls);
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
                    try {
                        lShareeEntries.get(position).setPermissionLevel(0);
                        ListShare toRemove = lShareeEntries.remove(position);
                        lShareeEmails.remove(position);
                        requestor.putObject(toRemove);
                        myAdapter.notifyDataSetChanged();
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            if(shareeEmail.getText().toString().equals(am.getEmail(requestor))) {
                shareeEmail.setVisibility(View.GONE);
                removeSharee.setVisibility(View.GONE);
            }

            return listproduct;
        }
    }
}
