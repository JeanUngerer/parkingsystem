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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

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
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){
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
				int res = rs.getInt(1);
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
        assert(presence);
        assert(parkingAvailabilityUpdated);
        
    }

    
    @Test
    public void testParkingLotExit(){
    	// first the database has to be populated and checked
        testParkingACar();
        
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        
	   	 try {
			 Thread.currentThread().sleep(1000);
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
				int res = rs.getInt(1);
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
        assert(fareGenerated);
        assert(parkingAvailabilityUpdated);
        //TODO: check that the fare generated and out time are populated correctly in the database
    }
    
    @Test
    public void testParkingGetPlacesAvailablePlaces(){
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
        
        //assertEquals(2, parkingSpot.getId());
        //assert(parkingSpot.isAvailable());
        
    }
    
    @Disabled
    @Test
    public void testParkingGetPlacesNoAvailablePlaces() {
    	ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        String querry = "update parking set AVAILABLE = false where TYPE = 'CAR'";
        ParkingSpot parkingSpot = null;
        
        Connection connection = null;
        
        try{
            connection = dataBaseTestConfig.getConnection();

            //set availability to false for the first car spot
			connection.prepareStatement(querry).executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            dataBaseTestConfig.closeConnection(connection);
        }

        Exception exception = assertThrows(Exception.class, () -> {parkingService.getNextParkingNumberIfAvailable();});
        
        String expectedMessage = "Error fetching parking number from DB. Parking slots might be full";
        String actualMessage = exception.getMessage();
        
        
        actualMessage = "tt";
        System.out.println("EXPECTED : " + expectedMessage);
        System.out.println( "ACTUAL : " + actualMessage);
        
        assert(actualMessage.contains(expectedMessage));
        
    }
}
