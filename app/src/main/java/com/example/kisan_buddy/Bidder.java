package com.example.kisan_buddy;

public class Bidder {

    private String bidderName;
    private String bidderEmail;
    private double bidAmount;

    // Default constructor
    public Bidder() {
    }

    // Parameterized constructor
    public Bidder(String bidderName, String bidderEmail, double bidAmount) {
        this.bidderName = bidderName;
        this.bidderEmail = bidderEmail;
        this.bidAmount = bidAmount;
    }

    // Getters and Setters
    public String getBidderName() {
        return bidderName;
    }

    public void setBidderName(String bidderName) {
        this.bidderName = bidderName;
    }

    public String getBidderEmail() {
        return bidderEmail;
    }

    public void setBidderEmail(String bidderEmail) {
        this.bidderEmail = bidderEmail;
    }

    public double getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(double bidAmount) {
        this.bidAmount = bidAmount;
    }

    @Override
    public String toString() {
        return "Bidder{" +
                "bidderName='" + bidderName + '\'' +
                ", bidderEmail='" + bidderEmail + '\'' +
                ", bidAmount=" + bidAmount +
                '}';
    }
}
