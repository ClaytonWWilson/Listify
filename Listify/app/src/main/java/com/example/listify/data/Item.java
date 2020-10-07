package com.example.listify.data;

import java.math.BigDecimal;

public class Item {
    Integer productID;
    Integer chainID;
    String upc;
    String description;
    BigDecimal price;
    String imageURL;
    String department;
    long retrievedDate;
    Integer fetchCounts;

    public Item(Integer productID, Integer chainID, String upc, String description, BigDecimal price,
                String imageURL, String department, long retrievedDate, Integer fetchCounts) {
        this.productID = productID;
        this.chainID = chainID;
        this.upc = upc;
        this.description = description;
        this.price = price;
        this.imageURL = imageURL;
        this.department = department;
        this.retrievedDate = retrievedDate;
        this.fetchCounts = fetchCounts;
    }

    @Override
    public String toString() {
        return "Item{" +
                "productID=" + productID +
                ", chainID=" + chainID +
                ", upc='" + upc + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", imageURL='" + imageURL + '\'' +
                ", department='" + department + '\'' +
                ", retrievedDate=" + retrievedDate +
                ", fetchCounts=" + fetchCounts +
                '}';
    }

    public Integer getProductID() {
        return productID;
    }

    public void setProductID(Integer productID) {
        this.productID = productID;
    }

    public Integer getChainID() {
        return chainID;
    }

    public void setChainID(Integer chainID) {
        this.chainID = chainID;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public long getRetrievedDate() {
        return retrievedDate;
    }

    public void setRetrievedDate(long retrievedDate) {
        this.retrievedDate = retrievedDate;
    }

    public Integer getFetchCounts() {
        return fetchCounts;
    }

    public void setFetchCounts(Integer fetchCounts) {
        this.fetchCounts = fetchCounts;
    }
}
