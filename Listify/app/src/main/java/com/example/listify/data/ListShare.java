package com.example.listify.data;

public class ListShare {
    Integer listID;
    String shareWithEmail;

    public ListShare(Integer listID, String shareWithEmail) {
        this.listID = listID;
        this.shareWithEmail = shareWithEmail;
    }

    public Integer getListID() {
        return listID;
    }

    public void setListID(Integer listID) {
        this.listID = listID;
    }

    public String getShareWithEmail() {
        return shareWithEmail;
    }

    public void setShareWithEmail(String shareWithEmail) {
        this.shareWithEmail = shareWithEmail;
    }
}
