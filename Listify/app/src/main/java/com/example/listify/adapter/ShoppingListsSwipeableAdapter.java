package com.example.listify.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.listify.ListPage;
import com.example.listify.R;
import com.example.listify.data.List;

import java.util.ArrayList;

public class ShoppingListsSwipeableAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<List> lists;
    private LayoutInflater inflater;
    private final ViewBinderHelper binderHelper;

    public ShoppingListsSwipeableAdapter(Activity activity, ArrayList<List> lists){
        binderHelper = new ViewBinderHelper();
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
        ViewHolder holder;

        if (inflater == null) {
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.shopping_lists_swipeable_name_item, null);

            holder = new ViewHolder();
            holder.swipeLayout = (SwipeRevealLayout)convertView.findViewById(R.id.swipe_layout);
            holder.frontView = convertView.findViewById(R.id.front_layout);
            holder.deleteList = convertView.findViewById(R.id.delete_list);
            holder.shareList = convertView.findViewById(R.id.share_list);
            holder.textView = (TextView) convertView.findViewById(R.id.shopping_list_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final List curList = lists.get(position);

        // Bind the view to the unique list ID
        binderHelper.bind(holder.swipeLayout, Integer.toString(curList.getItemID()));

        holder.textView.setText(curList.getName());
        holder.deleteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Add database call to delete the list on the server


                Toast.makeText(activity, String.format("%s deleted", curList.getName()), Toast.LENGTH_SHORT).show();
                lists.remove(position);

                // Update listView
                notifyDataSetChanged();
            }
        });

        holder.shareList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Add database call to share list

                Toast.makeText(activity, String.format("Share %s", curList.getName()), Toast.LENGTH_SHORT).show();

                // Close the layout
                binderHelper.closeLayout(Integer.toString(curList.getItemID()));
            }
        });

        holder.frontView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listPage = new Intent(activity, ListPage.class);

                // Send the list ID
                listPage.putExtra("listID", curList.getItemID());
                activity.startActivity(listPage);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        SwipeRevealLayout swipeLayout;
        View frontView;
        View deleteList;
        View shareList;
        TextView textView;
    }
}
