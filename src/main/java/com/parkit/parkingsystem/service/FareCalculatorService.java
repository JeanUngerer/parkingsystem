package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inTime = ticket.getInTime().getTime();
        long outTime = ticket.getOutTime().getTime();
        


        //TODO: Some tests are failing here. Need to check if this logic is correct
        long duration = outTime - inTime;
        
        ticket.setPrice(priceGivenDurationAndType(duration, ticket.getParkingSpot().getParkingType(), ticket.getRecuiringUser()));
    }

    
    
    private long duratinCalc(long start, long end) {
    	return 0;
    }
    
   private double priceGivenDurationAndType(long duration, ParkingType pType, boolean recuring) {
       switch (pType){
	       case CAR: {
	           return priceGivenParkingDurationAndRate(duration, Fare.CAR_RATE_PER_HOUR, recuring);
	           
	       }
	       case BIKE: {
	           return priceGivenParkingDurationAndRate(duration, Fare.BIKE_RATE_PER_HOUR, recuring);
	           
	       }
	       default: throw new IllegalArgumentException("Unkown Parking Type");
       }
   }
    private double priceGivenParkingDurationAndRate (long duration, double rate, boolean recuring) {
    	
    	if (duration < 30 * 60 * 1000) {
    		return 0;
    	}
    	if (recuring) {
    		return duration/3600 / 1000 * rate * 95 / 100;
    	}
    	return duration/3600 / 1000 * rate;
    }
}