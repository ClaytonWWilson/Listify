package com.example.listify.data;

// Info on List to copy
public class ListDuplicate {
    Integer listID;
    String name;

    public ListDuplicate(Integer listID, String name) {
        this.listID = listID;
        this.name = name;
    }

    @Override
    public String toString() {
        return "ListDuplicate{" +
                "listID=" + listID +
                ", name='" + name + '\'' +
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

}
