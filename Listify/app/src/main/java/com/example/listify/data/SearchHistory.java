package com.example.listify.data;

import java.util.ArrayList;

public class SearchHistory {
    ArrayList<String> searches;

    public SearchHistory(ArrayList<String> searches) {
        this.searches = searches;
    }

    public ArrayList<String> getSearches() {
        return searches;
    }

    public void setSearches(ArrayList<String> searches) {
        this.searches = searches;
    }

    public void addSearch(String newSearch) {
        searches.add(newSearch);
    }

    @Override
    public String toString() {
        return "SearchHistory{" +
                "searches=" + searches +
                '}';
    }
}
