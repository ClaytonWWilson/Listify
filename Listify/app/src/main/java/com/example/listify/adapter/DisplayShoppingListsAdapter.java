package com.example.listify.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.listify.R;
import com.example.listify.model.ShoppingList;
import java.util.ArrayList;

public class DisplayShoppingListsAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<ShoppingList> lists;
    private LayoutInflater inflater;

    public DisplayShoppingListsAdapter(Activity activity, ArrayList<ShoppingList> lists){
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
            convertView = inflater.inflate(R.layout.display_shopping_lists_item, null);
        }

        ShoppingList curList = lists.get(position);

        TextView tvListName = (TextView) convertView.findViewById(R.id.shopping_list_name);
        tvListName.setText(curList.getName());

        return convertView;
    }
}
