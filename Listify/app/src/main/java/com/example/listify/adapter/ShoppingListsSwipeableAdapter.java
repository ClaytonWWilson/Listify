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
            holder.shareList = convertView.findViewById(R.id.share_list);
            holder.listName = (TextView) convertView.findViewById(R.id.shopping_list_name);
            holder.itemCount = (TextView) convertView.findViewById(R.id.shopping_list_item_count);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final List curList = lists.get(position);

        // Bind the view to the unique list ID
        binderHelper.bind(holder.swipeLayout, Integer.toString(curList.getListID()));

        if(curList.isShared()) {
            holder.listName.setText(curList.getName() + " (shared by " + curList.getOwner() + ")");
        }
        else {
            holder.listName.setText(curList.getName());
        }

        holder.itemCount.setText(String.format("%d items", curList.getEntries().length));

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
                View codeView = inflater.inflate(R.layout.activity_sharedemail, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setView(codeView);
                builder.setTitle("Share list");
                builder.setMessage("Please enter the email of the user who you want to share the list with.");
                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText sharedEmailText = (EditText) codeView.findViewById(R.id.editTextTextSharedEmail);
                        String sharedEmail = sharedEmailText.getText().toString();
                        ListShare listShare = new ListShare(curList.getListID(), sharedEmail, "Read, Write, Delete, Share", null);
                        try {
                            requestor.putObject(listShare);
                        }
                        catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                Toast.makeText(activity, String.format("Share %s", curList.getName()), Toast.LENGTH_SHORT).show();

                // Close the layout
                binderHelper.closeLayout(Integer.toString(curList.getListID()));
            }
        });

        holder.frontView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listPage = new Intent(activity, ListPage.class);

                // Send the list ID and list name
                listPage.putExtra("listID", curList.getListID());
                listPage.putExtra("listName", curList.getName());

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
