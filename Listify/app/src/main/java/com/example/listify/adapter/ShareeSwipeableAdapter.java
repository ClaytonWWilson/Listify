package com.example.listify.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.listify.AuthManager;
import com.example.listify.ListPage;
import com.example.listify.R;
import com.example.listify.Requestor;
import com.example.listify.data.List;
import com.example.listify.data.ListShare;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import static com.example.listify.MainActivity.am;

public class ShareeSwipeableAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<ListShare> sharees;
    private LayoutInflater inflater;
    private final ViewBinderHelper binderHelper;

    public ShareeSwipeableAdapter(Activity activity, ArrayList<ListShare> sharees){
        binderHelper = new ViewBinderHelper();
        this.activity = activity;
        this.sharees = sharees;
    }

    @Override
    public int getCount() {
        return sharees.size();
    }

    @Override
    public Object getItem(int position) {
        return sharees.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        Properties configs = new Properties();
        try {
            configs = AuthManager.loadProperties(activity, "android.resource://" + activity.getPackageName() + "/raw/auths.json");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        Requestor requestor = new Requestor(am, configs.getProperty("apiKey"));

        if (inflater == null) {
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.shopping_lists_swipeable_name_item, null);

            holder = new ViewHolder();
            holder.swipeLayout = (SwipeRevealLayout)convertView.findViewById(R.id.swipe_layout);
            holder.frontView = convertView.findViewById(R.id.front_layout);
            holder.deleteList = convertView.findViewById(R.id.delete_list);
//            holder.shareList = convertView.findViewById(R.id.share_list);
            holder.textView = (TextView) convertView.findViewById(R.id.shopping_list_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ListShare currSharee = sharees.get(position);

        // Bind the view to the unique list ID
        binderHelper.bind(holder.swipeLayout, currSharee.getShareWithEmail());

        holder.textView.setText(currSharee.getShareWithEmail());

        holder.deleteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

//        holder.shareList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        holder.frontView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
