package com.example.listify.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Product implements Serializable {
    private String itemName;
    private int itemId;
    private String chainName;
    private int chainId;
    private String upc;
    private String description;
    private String department;
    private BigDecimal price;
    private String retrievedDate;
    private String fetchCounts;
    private String imageUrl;

    public Product() {}

    public Product(String itemName, int itemId, String chainName, int chainId, String upc, String description, BigDecimal price, String imageUrl, String department) {
        this.itemName = itemName;
        this.itemId = itemId;
        this.chainName = chainName;
        this.chainId = chainId;
        this.upc = upc;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.department = department;

    }

    public Product(String itemName, int itemId, String chainName, int chainId, String upc,
                   String description, String department, BigDecimal price, String retrievedDate,
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

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getChainName() {
        return chainName;
    }

    public void setChainName(String chainName) {
        this.chainName = chainName;
    }

    public int getChainId() {
        return chainId;
    }

    public void setChainId(int chainId) {
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
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
