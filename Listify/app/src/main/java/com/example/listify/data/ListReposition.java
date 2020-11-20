package com.example.listify.data;

public class ListReposition {
    Integer listID;
    Integer newPosition;

    public ListReposition(Integer listID, Integer newPosition) {
        this.listID = listID;
        this.newPosition = newPosition;
    }

    @Override
    public String toString() {
        return "ListReposition{" +
                "listID=" + listID +
                ", newPosition=" + newPosition +
                '}';
    }

    public Integer getListID() {
        return listID;
    }

    public void setListID(Integer listID) {
        this.listID = listID;
    }

    public Integer getNewPosition() {
        return newPosition;
    }

    public void setNewPosition(Integer newPosition) {
        this.newPosition = newPosition;
    }
}
