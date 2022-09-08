package com.parkit.parkingsystem.integration;


import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;



@ExtendWith(MockitoExtension.class)
public class TicketDAOTest {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    

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
	public void testSaveAndGetsTicket() {
		Ticket ticket = new Ticket();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
		ticket.setParkingSpot(parkingSpot);
		ticket.setInTime(ZonedDateTime.of(LocalDateTime.of(2022, 07, 1, 7, 10, 32), ZoneId.systemDefault()));
		ticket.setOutTime(ZonedDateTime.of(LocalDateTime.of(2022, 07, 1, 13, 10, 32), ZoneId.systemDefault()));
		ticket.setPrice(12);
		ticket.setVehicleRegNumber("AAAABBBB");
		ticket.setRecuringUser(true);
		
		
		ZonedDateTime inTime = ZonedDateTime.of(LocalDateTime.of(2022, 07, 1, 7, 10, 32), ZoneId.systemDefault()); // precision is to the seconds
		ZonedDateTime outTime = ZonedDateTime.of(LocalDateTime.of(2022, 07, 1, 13, 10, 32), ZoneId.systemDefault()); // precision is to the seconds
		Integer price = 0;
		String regPlate = "";
		Boolean recuring = false;
		
		
		ticketDAO.saveTicket(ticket);
		//ticketDAO.saveTicket(ticket);
		
		Ticket ticket2 = ticketDAO.getTicket("AAAABBBB");
		
		Connection connection = null;
        try{
            connection = dataBaseTestConfig.getConnection();

            //counts tickets with license plate ABCDEF
			PreparedStatement ps;
            
			ResultSet rs;
				
				
			ps = connection.prepareStatement("select IN_TIME from ticket where VEHICLE_REG_NUMBER =?");
			ps.setString(1, "AAAABBBB");
			rs = ps.executeQuery();
			if (rs.next()) {
				
				inTime = ZonedDateTime.of(rs.getTimestamp(1).toLocalDateTime(), ZoneId.systemDefault());}
			
			ps = connection.prepareStatement("select OUT_TIME from ticket where VEHICLE_REG_NUMBER =?");
			ps.setString(1, "AAAABBBB");
			rs = ps.executeQuery();
			if (rs.next()) {
				
				outTime = ZonedDateTime.of(rs.getTimestamp(1).toLocalDateTime(), ZoneId.systemDefault());}
			
			ps = connection.prepareStatement("select PRICE from ticket where VEHICLE_REG_NUMBER =?");
			ps.setString(1, "AAAABBBB");
			rs = ps.executeQuery();
			if (rs.next()) {
				
				price = rs.getInt(1);}
			
			ps = connection.prepareStatement("select VEHICLE_REG_NUMBER from ticket where VEHICLE_REG_NUMBER =?");
			ps.setString(1, "AAAABBBB");
			rs = ps.executeQuery();
			if (rs.next()) {
				
				regPlate = rs.getString(1);}
			
			ps = connection.prepareStatement("select RECUIRING_USER from ticket where VEHICLE_REG_NUMBER =?");
			ps.setString(1, "AAAABBBB");
			rs = ps.executeQuery();
			if (rs.next()) {
				
				recuring = rs.getBoolean(1);}
				
			
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            dataBaseTestConfig.closeConnection(connection);
        }      
      //check that a ticket is saved in DB with correct values

        assertTrue(price == ticket.getPrice());
        assertEquals(regPlate, ticket.getVehicleRegNumber());
        assertEquals(recuring, ticket.getRecuiringUser());
        assertEquals(inTime, ticket.getInTime());
        assertEquals(outTime, ticket.getOutTime());
        
      //check that a getTicket has correct values from db
        
        assertTrue(price == ticket2.getPrice());
        assertEquals(regPlate, ticket2.getVehicleRegNumber());
        assertEquals(recuring, ticket2.getRecuiringUser());
        assertEquals(inTime, ticket2.getInTime());
        assertEquals(outTime, ticket2.getOutTime());
        
      //check that isRecuiring has correct value from db  

        assertEquals(recuring, ticketDAO.isRecuiring("AAAABBBB"));
	}
	
	
	
}
