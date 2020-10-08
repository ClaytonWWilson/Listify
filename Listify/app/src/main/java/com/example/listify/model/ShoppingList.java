package com.example.listify.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class ShoppingList extends ArrayList<Product> {
    private ArrayList<Product> list;
    private String name;

    public ShoppingList(String name) {
        list = new ArrayList<Product>();
        this.name = name;
    }

    public Product get(int position) {
        return this.list.get(position);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return list.contains(o);
    }

    @Override
    public int indexOf(@Nullable Object o) {
        return list.indexOf(o);
    }

    @Override
    public boolean add(Product product) {
        return list.add(product);
    }

    @Override
    public Product remove(int index) {
        return list.remove(index);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean remove(@Nullable Object o) {
        return list.remove(o);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends Product> c) {
        return list.addAll(c);
    }
}