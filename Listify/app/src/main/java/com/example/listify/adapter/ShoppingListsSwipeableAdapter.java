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
import com.example.listify.ListSharees;
import com.example.listify.R;
import com.example.listify.Requestor;
import com.example.listify.data.List;
import com.example.listify.data.ListShare;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import static com.example.listify.MainActivity.am;

public class ShoppingListsSwipeableAdapter extends BaseAdapter implements Requestor.Receiver {
    private Activity activity;
    private ArrayList<List> lists;
    private LayoutInflater inflater;
    private List curList;
    private ViewHolder holder;
    private Requestor requestor;
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
    public void acceptDelivery(Object delivered) {
        ListShare sharee = (ListShare) delivered;

        if(sharee != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(sharee.getShareWithEmail().equals(am.getEmail(requestor))) {
                        holder.listName.setText(curList.getName() + " (sh. me)");
                    }
                    else {
                        holder.listName.setText(curList.getName() + " (sh. " + sharee.getShareWithEmail() + ")");
                    }

                    String listText = holder.listName.getText().toString();

                    if(listText.length() > 25) {
                        holder.listName.setText(listText.substring(0, 25) + "...");
                    }
                }
            });
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Properties configs = new Properties();
        try {
            configs = AuthManager.loadProperties(activity, "android.resource://" + activity.getPackageName() + "/raw/auths.json");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        requestor = new Requestor(am, configs.getProperty("apiKey"));

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
            holder.listName = (TextView) convertView.findViewById(R.id.shopping_list_name);
            holder.itemCount = (TextView) convertView.findViewById(R.id.shopping_list_item_count);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        curList = lists.get(position);

        // Bind the view to the unique list ID
        binderHelper.bind(holder.swipeLayout, Integer.toString(curList.getListID()));

        holder.listName.setText(curList.getName());

        if(curList.isShared()) {
            requestor.getObject(Integer.toString(curList.getListID()), ListShare.class, this);
        }

        String listText = holder.listName.getText().toString();

        if(listText.length() > 25) {
            holder.listName.setText(listText.substring(0, 25) + "...");
        }

        if (curList.getEntries() != null) {
            holder.itemCount.setText(String.format("%d items", curList.getEntries().length));
        } else {
            holder.itemCount.setText("0 items");
        }

        holder.deleteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    requestor.deleteObject(Integer.toString(curList.getListID()), List.class);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(activity, String.format("%s deleted", curList.getName()), Toast.LENGTH_SHORT).show();
                lists.remove(position);

                // Update listView
                notifyDataSetChanged();
            }
        });

        holder.shareList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listSharees = new Intent(activity, ListSharees.class);
                listSharees.putExtra("listID", curList.getListID());
                activity.startActivity(listSharees);
            }
        });

        holder.frontView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listPage = new Intent(activity, ListPage.class);
                listPage.putExtra("selectedList", curList);
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
        TextView listName;
        TextView itemCount;
    }
}
