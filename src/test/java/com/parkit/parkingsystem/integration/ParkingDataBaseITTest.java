package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.DBConstants;




import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;



@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseITTest {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    
    @Mock
    private static Ticket ticket;

    @BeforeAll
    private static void setUp() throws Exception{
    	
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
        

    }

    @BeforeEach
    private void setUpPerTest() throws Exception {

        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar() throws Exception{
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        
        parkingService.processIncomingVehicle();
        
        Connection connection = null;
        boolean presence = false;
        boolean parkingAvailabilityUpdated = false;
        try{
            connection = dataBaseTestConfig.getConnection();

            //counts tickets with license plate ABCDEF
			PreparedStatement ps = connection.prepareStatement(DBConstants.IS_PRESENT);
			ps.setString(1, "ABCDEF");
            
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				presence = (rs.getInt(1) > 0);
				
			}
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            dataBaseTestConfig.closeConnection(connection);
        }
        
        try{
            connection = dataBaseTestConfig.getConnection();

            //read the availability value of the first available parking spot
			ResultSet rs = connection.prepareStatement("select AVAILABLE from parking where PARKING_NUMBER = 1").executeQuery();
			if (rs.next()) {
				parkingAvailabilityUpdated = (rs.getBoolean(1) == false);
			}
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            dataBaseTestConfig.closeConnection(connection);
        }
      //check that a ticket is actually saved in DB and Parking table is updated with availability
        assertTrue(presence);
        assertTrue(parkingAvailabilityUpdated);
    }
    
    private void populatePark() throws Exception{
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        
        parkingService.processIncomingVehicle();
    }

    
    @Test
    public void testParkingLotExit() throws Exception{
    	// first the database has to be populated and checked
        populatePark(); //NON -> private methods pour peupler
        
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        
	   	 try {
			 Thread.currentThread();
			Thread.sleep(1000);
		 } catch (InterruptedException e) {
			 e.printStackTrace();
		 }
	        
        parkingService.processExitingVehicle();
        
        Connection connection = null;
        boolean fareGenerated = false;
        boolean parkingAvailabilityUpdated = false;
        try{
            connection = dataBaseTestConfig.getConnection();

            //counts tickets with license plate ABCDEF
			PreparedStatement ps = connection.prepareStatement("select count(ID) from ticket where VEHICLE_REG_NUMBER =? and PRICE is not NULL");
			ps.setString(1, "ABCDEF");
            
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				fareGenerated = (rs.getInt(1) > 0);
				
			}
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            dataBaseTestConfig.closeConnection(connection);
        }
        
        try{
            connection = dataBaseTestConfig.getConnection();

            //read the availability value of the first available parking spot
			ResultSet rs = connection.prepareStatement("select AVAILABLE from parking where PARKING_NUMBER = 1").executeQuery();
			if (rs.next()) {
				parkingAvailabilityUpdated = (rs.getBoolean(1) == true);
			}
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            dataBaseTestConfig.closeConnection(connection);
        }
        //check that a generated fare is actually saved in DB and Parking table is updated with availability
        assertTrue(fareGenerated);
        assertTrue(parkingAvailabilityUpdated);
    }
    
    
    @Test
    public void testParkingGetPlacesAvailablePlaces(){
    	when(inputReaderUtil.readSelection()).thenReturn(1);
    	ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        String querry = "update parking set AVAILABLE = false where PARKING_NUMBER = 1";
        ParkingSpot parkingSpot = null;
        
        Connection connection = null;
        
        try{
            connection = dataBaseTestConfig.getConnection();

            //set availability to false for the first car spot
			connection.prepareStatement(querry).execute();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            dataBaseTestConfig.closeConnection(connection);
        }
        
        
        parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        
        assertEquals(2, parkingSpot.getId());
        assertTrue(parkingSpot.isAvailable());
        
    }
    
    private void exitPark() {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        
	   	 try {
			 Thread.currentThread();
			Thread.sleep(1000);
		 } catch (InterruptedException e) {
			 e.printStackTrace();
		 }
	        
        parkingService.processExitingVehicle();
    }
    
    @Test
    public void testParkingACarReccuringUser() throws Exception{
    	
    	// first the database has to be populated and checked
        populatePark();
        
        
	   	 try {
			 Thread.currentThread();
			Thread.sleep(1000);
		 } catch (InterruptedException e) {
			 e.printStackTrace();
		 }
	        
	   	exitPark();
        
	   	 try {
			 Thread.currentThread();
			Thread.sleep(1000);
		 } catch (InterruptedException e) {
			 e.printStackTrace();
		 }
    	
    	
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        
        parkingService.processIncomingVehicle();
        
        Connection connection = null;
        boolean reccuring = false;
        try{
            connection = dataBaseTestConfig.getConnection();

            //counts tickets with license plate ABCDEF
			PreparedStatement ps = connection.prepareStatement("select count(ID) from ticket where VEHICLE_REG_NUMBER =? and RECUIRING_USER = true");
			ps.setString(1, "ABCDEF");
            
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				reccuring = (rs.getInt(1) > 0);
				
			}
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            dataBaseTestConfig.closeConnection(connection);
        }
        
        
      //check that a ticket is actually saved in DB and Ticket RECUIRING_USER is set to true
        assertTrue(reccuring);

    }
    
    
}
