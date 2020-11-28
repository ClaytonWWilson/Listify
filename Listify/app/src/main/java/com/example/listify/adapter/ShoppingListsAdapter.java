package com.example.listify.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.listify.R;
import com.example.listify.data.List;
import java.util.ArrayList;

public class ShoppingListsAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<List> lists;
    private LayoutInflater inflater;

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

        List curList = lists.get(position);

        TextView tvListName = (TextView) convertView.findViewById(R.id.shopping_list_name);
        if(curList.isShared()) {
            tvListName.setText(curList.getName() + " (shared by " + curList.getOwner() + ")");
        }
        else {
            tvListName.setText(curList.getName());
        }

        return convertView;
    }
}