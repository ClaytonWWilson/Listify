package com.example.listify.data;

import java.util.Arrays;

public class List {
    Integer listID;
    String name;
    String owner;
    long lastUpdated;
    final ListEntry[] entries;
    boolean shared;

    public List(Integer listID, String name, String owner, long lastUpdated, ListEntry[] entries, boolean shared) {
        this.listID = listID;
        this.name = name;
        this.owner = owner;
        this.lastUpdated = lastUpdated;
        this.entries = entries;
        this.shared = false;
    }

    public List(Integer listID, String name, String owner, long lastUpdated) {
        this(listID,  name,  owner,  lastUpdated, null, false);
    }

    @Override
    public String toString() {
        return "List{" +
                "listID=" + listID +
                ", name='" + name + '\'' +
                ", owner='" + owner + '\'' +
                ", lastUpdated=" + lastUpdated +
                ", entries=" + Arrays.toString(entries) +
                ", shared=" + shared +
                '}';
    }

    public Integer getListID() {
        return listID;
    }

    public void setListID(Integer listID) {
        this.listID = listID;
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

    public ListEntry[] getEntries() {
        return entries;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }
}
