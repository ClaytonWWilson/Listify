package com.example.listify.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.listify.model.Product;
import com.example.listify.R;

import java.util.List;

public class SearchResultsListAdapter extends BaseAdapter {
    private Activity activity;
    private List<Product> productList;
    private LayoutInflater inflater;

    public SearchResultsListAdapter(Activity activity, List<Product> productList){
        this.activity = activity;
        this.productList = productList;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
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
            convertView = inflater.inflate(R.layout.search_list_item, null);
        }

        ImageView productImage = (ImageView) convertView.findViewById(R.id.item_image);
        TextView itemName  = (TextView) convertView.findViewById(R.id.item_name);
        TextView price = (TextView) convertView.findViewById(R.id.item_price);
        TextView itemStore  = (TextView) convertView.findViewById(R.id.item_store);

        Product product = productList.get(position);
        // TODO: If image url is broken, display @drawable/ic_baseline_broken_image_600.xml
        Glide.with(activity).load(product.getImageUrl()).into(productImage);
        if (product.getItemName().length() >= 35) {
            itemName.setText(product.getItemName().substring(0, 35) + "...");
        } else {
            itemName.setText(product.getItemName());
        }
        price.setText(String.format("$%.2f", product.getPrice()));
        itemStore.setText(product.getChainName());

        return convertView;
    }
}
