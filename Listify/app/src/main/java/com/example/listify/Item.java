package com.example.listify;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Item {
    Integer productID;
    Integer chainID;
    String upc;
    String description;
    BigDecimal price;
    String imageURL;
    String department;
    LocalDateTime retrievedDate;
    Integer fetchCounts;

    public Item(Integer productID, Integer chainID, String upc, String description, BigDecimal price,
         String imageURL, String department, LocalDateTime retrievedDate, Integer fetchCounts) {
        this.productID = productID;
        this.chainID = chainID;
        this.upc = upc;
        this.description = description;
        this.price = price;
        this.imageURL = imageURL;
        this.department = department;
        this.retrievedDate = retrievedDate;
        this.fetchCounts = fetchCounts;
    }

    @Override
    public String toString() {
        return "Item{" +
                "productID=" + productID +
                ", chainID=" + chainID +
                ", upc='" + upc + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", imageURL='" + imageURL + '\'' +
                ", department='" + department + '\'' +
                ", retrievedDate=" + retrievedDate +
                ", fetchCounts=" + fetchCounts +
                '}';
    }
}
