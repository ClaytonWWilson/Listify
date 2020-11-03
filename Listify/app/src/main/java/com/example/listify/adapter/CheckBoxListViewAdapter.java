package com.example.listify.adapter;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.example.listify.R;

import java.util.ArrayList;

public class CheckBoxListViewAdapter extends BaseAdapter {
    private Activity activity;
    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> checkedList = new ArrayList<>();

    public CheckBoxListViewAdapter(Activity activity, ArrayList<String> list, ArrayList<String> checkedList) {
        super();
        this.activity = activity;
        this.list = list;
        this.checkedList = checkedList;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        public TextView label;
        public CheckBox checkBox;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflator = ((Activity) activity).getLayoutInflater();

        if (convertView == null) {
            convertView = inflator.inflate(R.layout.filter_store_item, null);

            convertView.setSoundEffectsEnabled(false);

            holder = new ViewHolder();
            holder.label = (TextView) convertView.findViewById(R.id.store_name);
            holder.checkBox = (CheckBox)convertView.findViewById(R.id.store_check_box);

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();

                    if (isChecked) {
                        checkedList.add(list.get(getPosition));
                    } else {
                        checkedList.remove(list.get(getPosition));
                    }
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("clicked");
                    holder.checkBox.performClick();
                }
            });

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.checkBox.setTag(position);
        holder.label.setText("" + list.get(position));
        holder.checkBox.setChecked(checkedList.contains(list.get(position)));
        return convertView;
    }

    public ArrayList<String> getChecked() {
        return this.checkedList;
    }
}
