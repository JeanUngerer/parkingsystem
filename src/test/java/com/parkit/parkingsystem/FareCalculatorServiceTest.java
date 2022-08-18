package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCar(){
    	LocalDateTime inTime = LocalDateTime.now();
        
        LocalDateTime outTime = inTime.plusHours(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(ZonedDateTime.of(inTime, ZoneId.systemDefault()));
        ticket.setOutTime(ZonedDateTime.of(outTime, ZoneId.systemDefault()));
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        ticket.setRecuringUser(false);
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareBike(){
    	LocalDateTime inTime = LocalDateTime.now();
        
        LocalDateTime outTime = inTime.plusHours(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(ZonedDateTime.of(inTime, ZoneId.systemDefault()));
        ticket.setOutTime(ZonedDateTime.of(outTime, ZoneId.systemDefault()));
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        ticket.setRecuringUser(false);
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareUnkownType(){
    	LocalDateTime inTime = LocalDateTime.now();
        
        LocalDateTime outTime = inTime.plusHours(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);
        
        ticket.setInTime(ZonedDateTime.of(inTime, ZoneId.systemDefault()));
        ticket.setOutTime(ZonedDateTime.of(outTime, ZoneId.systemDefault()));
        ticket.setParkingSpot(parkingSpot);
        ticket.setRecuringUser(false);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime(){
    	LocalDateTime outTime = LocalDateTime.now();
        
        LocalDateTime inTime = outTime.plusHours(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(ZonedDateTime.of(inTime, ZoneId.systemDefault()));
        ticket.setOutTime(ZonedDateTime.of(outTime, ZoneId.systemDefault()));
        ticket.setParkingSpot(parkingSpot);
        ticket.setRecuringUser(false);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
    	LocalDateTime inTime = LocalDateTime.now();
        
        LocalDateTime outTime = inTime.plusMinutes(45);//45 minutes parking time should give 3/4th parking fare
    	

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(ZonedDateTime.of(inTime, ZoneId.systemDefault()));
        ticket.setOutTime(ZonedDateTime.of(outTime, ZoneId.systemDefault()));
        ticket.setParkingSpot(parkingSpot);
        ticket.setRecuringUser(false);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (45 * 60 * 1000 / 3600 / 1000 * Fare.BIKE_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
    	LocalDateTime inTime = LocalDateTime.now();
        
        LocalDateTime outTime = inTime.plusMinutes(45);//45 minutes parking time should give 3/4th parking fare
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(ZonedDateTime.of(inTime, ZoneId.systemDefault()));
        ticket.setOutTime(ZonedDateTime.of(outTime, ZoneId.systemDefault()));
        ticket.setParkingSpot(parkingSpot);
        ticket.setRecuringUser(false);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (45 * 60 * 1000 / 3600 / 1000 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
    	LocalDateTime inTime = LocalDateTime.now();
        
        LocalDateTime outTime = inTime.plusHours(24);//24 hours parking time should give 24 * parking fare per hour
        
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(ZonedDateTime.of(inTime, ZoneId.systemDefault()));
        ticket.setOutTime(ZonedDateTime.of(outTime, ZoneId.systemDefault()));
        ticket.setParkingSpot(parkingSpot);
        ticket.setRecuringUser(false);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }
    
    @Test
    public void calculateFareCarWithLessThanThirtyMinutesParkingTime(){
    	LocalDateTime inTime = LocalDateTime.now();
        
        LocalDateTime outTime = inTime.plusMinutes(29);//less than 30 minutes parking time should give 0 parking fare
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(ZonedDateTime.of(inTime, ZoneId.systemDefault()));
        ticket.setOutTime(ZonedDateTime.of(outTime, ZoneId.systemDefault()));
        ticket.setParkingSpot(parkingSpot);
        ticket.setRecuringUser(false);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (0) , ticket.getPrice());
    }
    
    @Test
    public void calculateFareBikeWithLessThanThirtyMinutesParkingTime(){
    	LocalDateTime inTime = LocalDateTime.now();
        
        LocalDateTime outTime = inTime.plusMinutes(25);//less than 30 minutes parking time should give 0 parking fare

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(ZonedDateTime.of(inTime, ZoneId.systemDefault()));
        ticket.setOutTime(ZonedDateTime.of(outTime, ZoneId.systemDefault()));
        ticket.setParkingSpot(parkingSpot);
        ticket.setRecuringUser(false);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (0) , ticket.getPrice());
    }
    
    @Test
    public void calculateFareCarWithExactlyThirtyMinutesParkingTime(){
    	LocalDateTime inTime = LocalDateTime.now();
        
        LocalDateTime outTime = inTime.plusMinutes(30);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(ZonedDateTime.of(inTime, ZoneId.systemDefault()));
        ticket.setOutTime(ZonedDateTime.of(outTime, ZoneId.systemDefault()));
        ticket.setParkingSpot(parkingSpot);
        ticket.setRecuringUser(false);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (30 * 60 * 1000 / 3600 / 1000 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }
    
    @Test
    public void calculateFareCarForRecuringUser(){
    	LocalDateTime inTime = LocalDateTime.now();
        
        LocalDateTime outTime = inTime.plusMinutes(45);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(ZonedDateTime.of(inTime, ZoneId.systemDefault()));
        ticket.setOutTime(ZonedDateTime.of(outTime, ZoneId.systemDefault()));
        ticket.setParkingSpot(parkingSpot);
        ticket.setRecuringUser(true);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (45 * 60 / 3600 * 95 / 100 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    /*
    @Test
    public void calculateFareBikeForRecuringUser(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  12 * 3600 * 1000) );//12 hours parking time should give 12 times parking fare - 5% for recuring
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setRecuringUser(true);
       fareCalculatorService.calculateFare(ticket);       assertEquals( (12 * 95 / 100 * Fare.BIKE_RATE_PER_HOUR) , ticket.getPrice());
   }*/

}
