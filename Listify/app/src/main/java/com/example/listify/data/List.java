package com.example.listify.data;

import java.util.Arrays;

public class List {
    Integer itemID;
    String name;
    String owner;
    long lastUpdated;
    final ListEntry[] entries;
    boolean shared;

    public List(Integer itemID, String name, String owner, long lastUpdated, ListEntry[] entries, boolean shared) {
        this.itemID = itemID;
        this.name = name;
        this.owner = owner;
        this.lastUpdated = lastUpdated;
        this.entries = entries;
        this.shared = false;
    }

    public List(Integer itemID, String name, String owner, long lastUpdated) {
        this(itemID,  name,  owner,  lastUpdated, null, false);
    }

    @Override
    public String toString() {
        return "List{" +
                "itemID=" + itemID +
                ", name='" + name + '\'' +
                ", owner='" + owner + '\'' +
                ", lastUpdated=" + lastUpdated +
                ", entries=" + Arrays.toString(entries) +
                ", shared=" + shared +
                '}';
    }

    public Integer getItemID() {
        return itemID;
    }

    public void setItemID(Integer itemID) {
        this.itemID = itemID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public ListEntry[] getEntries() {
        return entries;
    }
}
