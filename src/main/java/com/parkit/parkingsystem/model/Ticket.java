package com.parkit.parkingsystem.model;

import java.time.ZonedDateTime;

import com.parkit.parkingsystem.constants.ParkingType;


public class Ticket {
    private int id;
    private ParkingSpot parkingSpot = new ParkingSpot(0, ParkingType.CAR,false );
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
    	ParkingSpot parkingSpotCopy = new ParkingSpot(parkingSpot.getId(), parkingSpot.getParkingType(), parkingSpot.isAvailable());
        return parkingSpotCopy;
    }

    public void setParkingSpot(ParkingSpot parkingSpotIn) {
    	parkingSpot.setAvailable(parkingSpotIn.isAvailable());
    	parkingSpot.setId(parkingSpotIn.getId());
    	parkingSpot.setParkingType(parkingSpotIn.getParkingType());
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
