package com.example.listify.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.listify.R;

import java.io.InputStream;

public class Product {
    private String itemName;
    private String itemId;
    private String chainName;
    private String chainId;
    private String upc;
    private String description;
    private String department;
    private String price;
    private String retrievedDate;
    private String fetchCounts;
    private String imageUrl;

    public Product() {}

    public Product(String itemName, String itemId, String chainName, String chainId, String upc,
                   String description, String department, String price, String retrievedDate,
                   String fetchCounts, String imageUrl) {
        this.itemName = itemName;
        this.itemId = itemId;
        this.chainName = chainName;
        this.chainId = chainId;
        this.upc = upc;
        this.description = description;
        this.department = department;
        this.price = price;
        this.retrievedDate = retrievedDate;
        this.fetchCounts = fetchCounts;
        this.imageUrl = imageUrl;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... args) {
            String url = args[0];
            Bitmap image = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                image = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return image;
        }

        protected void onPostExecute(Bitmap result) {
            // Return the broken image icon as a bitmap if the url is invalid
            if (result == null) {
                imageView.setImageResource(R.drawable.ic_baseline_broken_image_600);
            } else {
                imageView.setImageBitmap(result);
            }
        }
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getChainName() {
        return chainName;
    }

    public void setChainName(String chainName) {
        this.chainName = chainName;
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRetrievedDate() {
        return retrievedDate;
    }

    public void setRetrievedDate(String retrievedDate) {
        this.retrievedDate = retrievedDate;
    }

    public String getFetchCounts() {
        return fetchCounts;
    }

    public void setFetchCounts(String fetchCounts) {
        this.fetchCounts = fetchCounts;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // TODO: Need to implement image resizing
    public void loadImageView(int height, int width, ImageView imageView) {
        new DownloadImageTask(imageView).execute(this.imageUrl);
    }
}
