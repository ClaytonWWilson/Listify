package com.example.listify.data;

import java.io.Serializable;

public class ListEntry implements Serializable {
    Integer listID;
    Integer productID;
    Integer quantity;
    long addedDate;
    Boolean purchased;

    public ListEntry(Integer listID, Integer productID, Integer quantity, long addedDate, Boolean purchased) {
        this.listID = listID;
        this.productID = productID;
        this.quantity = quantity;
        this.addedDate = addedDate;
        this.purchased = purchased;
    }

    @Override
    public String toString() {
        return "ListEntry{" +
                "listID=" + listID +
                ", productID=" + productID +
                ", quantity=" + quantity +
                ", addedDate=" + addedDate +
                ", purchased=" + purchased +
                '}';
    }

    public Integer getListID() {
        return listID;
    }

    public void setListID(Integer listID) {
        this.listID = listID;
    }

    public Integer getProductID() {
        return productID;
    }

    public void setProductID(Integer productID) {
        this.productID = productID;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public long getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(long addedDate) {
        this.addedDate = addedDate;
    }

    public Boolean getPurchased() {
        return purchased;
    }

    public void setPurchased(Boolean purchased) {
        this.purchased = purchased;
    }
}
