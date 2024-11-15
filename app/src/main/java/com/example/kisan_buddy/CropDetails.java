package com.example.kisan_buddy;

public class CropDetails {
    private String cropName;
    private String quantity;
    private String price;
    private String cropImageUrl;

    // Default constructor (required for Firestore)
    public CropDetails() {
    }

    // Constructor
    public CropDetails(String cropName, String quantity, String price, String cropImageUrl) {
        this.cropName = cropName;
        this.quantity = quantity;
        this.price = price;
        this.cropImageUrl = cropImageUrl;
    }

    // Getters and setters
    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCropImageUrl() {
        return cropImageUrl;
    }

    public void setCropImageUrl(String cropImageUrl) {
        this.cropImageUrl = cropImageUrl;
    }
}
