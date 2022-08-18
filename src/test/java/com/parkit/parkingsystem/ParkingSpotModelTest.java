package com.parkit.parkingsystem;


import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ParkingSpotModelTest {
	
	
	@Test
	public void parkingSpotModelUsage() {
		ParkingSpot parkingSpot = new ParkingSpot(2, ParkingType.BIKE, false);
		
		parkingSpot.setAvailable(true);
		parkingSpot.setId(1);
		parkingSpot.setParkingType(ParkingType.CAR);
		
		assertEquals(1, parkingSpot.getId());
		assertEquals(ParkingType.CAR, parkingSpot.getParkingType());
		assertEquals(true, parkingSpot.isAvailable());
	}
}
