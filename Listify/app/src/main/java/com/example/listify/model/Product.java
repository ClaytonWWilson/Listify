package com.example.listify.model;

public class Product {
    private String itemName;
    private String itemId;
    private String chainName;
    private String chainId;
    private String upc;
    private String description;
    private String department;
    private double price;
    private String retrievedDate;
    private String fetchCounts;
    private String imageUrl;

    public Product() {}

    public Product(String itemName, String itemId, String chainName, String chainId, String upc,
                   String description, String department, double price, String retrievedDate,
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
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
}
