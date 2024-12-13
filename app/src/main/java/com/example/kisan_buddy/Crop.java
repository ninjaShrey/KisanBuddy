package com.example.kisan_buddy;

public class Crop {

    private String cropName;
    private String cropQuantity;
    private String cropWeight;
    private String producerEmail;
    private String price;


    // Public no-argument constructor
    public Crop() {
        // Required for Firestore deserialization
    }
    public Crop(String cropName, String cropQuantity, String cropWeight, String price, String producerEmail) {
        this.cropName = cropName;
        this.cropQuantity = cropQuantity;
        this.cropWeight = cropWeight;
        this.price=price;
        this.producerEmail = producerEmail;

    }

    public String getCropName() {
        return cropName;
    }

    public String getCropQuantity() {
        return cropQuantity;
    }

    public String getCropWeight() {
        return cropWeight;
    }

    public String getProducerEmail() {
        return producerEmail;
    }

    public String getPrice() {
        return price;
    }
}
