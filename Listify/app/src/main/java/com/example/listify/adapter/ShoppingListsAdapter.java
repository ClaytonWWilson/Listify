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

public class ShoppingListsAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<List> lists;
    private LayoutInflater inflater;
    private TextView tvListName;

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

        final List curList = lists.get(position);

        tvListName = (TextView) convertView.findViewById(R.id.shopping_list_name);

        tvListName.setText(curList.getName());

        if(curList.isShared()) {
            tvListName.setText(curList.getName() + " (shared by User " + curList.getOwner() + ")");

            String listText = tvListName.getText().toString();

            if(listText.length() > 25) {
                tvListName.setText(listText.substring(0, 25) + "...");
            }
        }

        return convertView;
    }
}