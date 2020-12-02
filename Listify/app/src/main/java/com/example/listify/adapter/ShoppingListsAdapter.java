package com.example.listify.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.listify.AuthManager;
import com.example.listify.R;
import com.example.listify.Requestor;
import com.example.listify.data.List;
import com.example.listify.data.ListShare;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import static com.example.listify.MainActivity.am;

public class ShoppingListsAdapter extends BaseAdapter implements Requestor.Receiver {
    private Activity activity;
    private ArrayList<List> lists;
    private LayoutInflater inflater;
    private TextView tvListName;
    private List curList;
    private Requestor requestor;

    public ShoppingListsAdapter(Activity activity, ArrayList<List> lists){
        this.activity = activity;
        this.lists = lists;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.shopping_lists_name_item, null);
        }

        curList = lists.get(position);

        tvListName = (TextView) convertView.findViewById(R.id.shopping_list_name);

        if(curList.isShared()) {
            Properties configs = new Properties();
            try {
                configs = AuthManager.loadProperties(activity, "android.resource://" + activity.getPackageName() + "/raw/auths.json");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            requestor = new Requestor(am, configs.getProperty("apiKey"));
            requestor.getObject(Integer.toString(curList.getListID()), ListShare.class, this);
        }
        else {
            tvListName.setText(curList.getName());
        }

        return convertView;
    }

    @Override
    public void acceptDelivery(Object delivered) {
        ListShare sharee = (ListShare) delivered;

        if(sharee != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(sharee.getShareWithEmail().equals(am.getEmail(requestor))) {
                        tvListName.setText(curList.getName() + " (shared by me)");
                    }
                    else {
                        tvListName.setText(curList.getName() + " (shared by " + sharee.getShareWithEmail() + ")");
                    }
                }
            });
        }
    }
}