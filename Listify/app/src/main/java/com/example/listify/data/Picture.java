package com.example.listify.data;

public class Picture {
    String base64EncodedImage;

    public Picture(String base64EncodedImage) {
        this.base64EncodedImage = base64EncodedImage;
    }

    @Override
    public String toString() {
        return "Picture{" +
                "base64EncodedImage='" + base64EncodedImage + '\'' +
                '}';
    }

    public String getBase64EncodedImage() {
        return base64EncodedImage;
    }

    public void setBase64EncodedImage(String base64EncodedImage) {
        this.base64EncodedImage = base64EncodedImage;
    }
}
