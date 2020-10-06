package com.example.listify.data;

import java.util.ArrayList;

public class ItemSearch {
    ArrayList<Item> results;

    public ItemSearch(ArrayList<Item> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "ItemSearch{" +
                "results=" + results +
                '}';
    }

    public ArrayList<Item> getResults() {
        return results;
    }

    public void setResults(ArrayList<Item> results) {
        this.results = results;
    }
}

