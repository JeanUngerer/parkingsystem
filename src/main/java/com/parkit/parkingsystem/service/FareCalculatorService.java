package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.constants.MillisecondsTimes;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inTime = ticket.getInTime().toInstant().toEpochMilli();
        long outTime = ticket.getOutTime().toInstant().toEpochMilli();
        


        
        long duration = outTime - inTime;
        
        ticket.setPrice(priceGivenDurationAndType(duration, ticket.getParkingSpot().getParkingType(), ticket.getRecuiringUser()));
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
    	
    	if (freeDuration(duration)) {
    		return 0;
    	}
    	if (recuring) {
    		return recuringPrice(rate, duration);
    	}
    	return duration/(double)MillisecondsTimes.HOUR_IN_MILLISECONDS * rate;
    }
    
    private boolean freeDuration(long duration) {
    	if (duration < 30 * MillisecondsTimes.MINUTES_IN_MILLISECONDS) {
    		return true;
    	}
    	return false;
    }
    	
	private double recuringPrice(double rate, long duration) {
		return duration/(double)MillisecondsTimes.HOUR_IN_MILLISECONDS * rate * 95 / 100;
    }
}