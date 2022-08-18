package com.parkit.parkingsystem.model;

import java.time.ZonedDateTime;


public class Ticket {
    private int id;
    private ParkingSpot parkingSpot;
    private String vehicleRegNumber;
    private double price;
    private ZonedDateTime inTime;
    private ZonedDateTime outTime;
    private boolean recuringUser;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public void setParkingSpot(ParkingSpot parkingSpot) {
        this.parkingSpot = parkingSpot;
    }

    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    public void setVehicleRegNumber(String vehicleRegNumber) {
        this.vehicleRegNumber = vehicleRegNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ZonedDateTime getInTime() {
        return inTime;
    }

    public void setInTime(ZonedDateTime inTime) {
        this.inTime = inTime;
    }

    public ZonedDateTime getOutTime() {
        return outTime;
    }

    public void setOutTime(ZonedDateTime outTime) {
        this.outTime = outTime;
    }
    
    public boolean getRecuiringUser() {
        return recuringUser;
    }

    public void setRecuringUser(boolean recuring) {
        this.recuringUser = recuring;
    }
}
