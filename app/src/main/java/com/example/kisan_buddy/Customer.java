package com.example.kisan_buddy;

import com.google.firebase.firestore.GeoPoint;

public class Customer {

    private String bidderName;
    private String bidderEmail;
    private int bidAmount;
    private String cropName;  // Assuming cropName is a field
    private String cropQuantity;
    private String cropWeight;
    private String price;
    private String documentId;
    private GeoPoint location;  // Adding location field

    // No-argument constructor for Firestore deserialization
    public Customer() {
        // Required empty constructor
    }

    // Getter and Setter methods

    public String getBidderName() {
        return bidderName;
    }

    public void setBidderName(String bidderName) {
        this.bidderName = bidderName;
    }
    public void setBidderDocId(String documentId) {
        this.documentId = documentId;
    }
    public String getBidderEmail() {
        return bidderEmail;
    }

    public void setBidderEmail(String bidderEmail) {
        this.bidderEmail = bidderEmail;
    }

    public int getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(int bidAmount) {
        this.bidAmount = bidAmount;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getCropQuantity() {
        return cropQuantity;
    }

    public void setCropQuantity(String cropQuantity) {
        this.cropQuantity = cropQuantity;
    }

    public String getCropWeight() {
        return cropWeight;
    }

    public void setCropWeight(String cropWeight) {
        this.cropWeight = cropWeight;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public GeoPoint getLocation() {
        return location;  // Adding the getLocation method
    }

    // New methods for getName and getEmail
    public String getName() {
        return bidderName;  // Assuming 'Name' refers to 'bidderName'
    }

    public String getEmail() {
        return bidderEmail;  // Assuming 'Email' refers to 'bidderEmail'
    }

    public String getBidderDocId() { return documentId;
    }
}
