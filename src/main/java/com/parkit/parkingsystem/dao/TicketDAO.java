package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TicketDAO {

	private static final Logger logger = LogManager.getLogger("TicketDAO");

	public DataBaseConfig dataBaseConfig = new DataBaseConfig();

	public boolean saveTicket(Ticket ticket) {
		boolean result = false;
		Connection con = null;
		PreparedStatement ps = null;

		try {
			
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.SAVE_TICKET);
			ps.setInt(1, ticket.getParkingSpot().getId());
			ps.setString(2, ticket.getVehicleRegNumber());
			ps.setDouble(3, ticket.getPrice());
			ps.setTimestamp(4, new Timestamp(ticket.getInTime().toInstant().toEpochMilli()));
			ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : (new Timestamp(ticket.getOutTime().toInstant().toEpochMilli())));
			ps.setBoolean(6, ticket.getRecuiringUser());
			result =  ps.execute();
		} catch (Exception ex) {
			logger.error("Error initializing ticket", ex);
		} finally {
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);
		}
		return result;

	}

	public Ticket getTicket(String vehicleRegNumber) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Ticket ticket = null;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.GET_TICKET);
			ps.setString(1, vehicleRegNumber);
			rs = ps.executeQuery();
			if (rs.next()) {
				ticket = new Ticket();
				ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
				ticket.setParkingSpot(parkingSpot);
				ticket.setId(rs.getInt(2));   // correcting column id
				ticket.setVehicleRegNumber(vehicleRegNumber);
				ticket.setPrice(rs.getDouble(3));
				ticket.setInTime(ZonedDateTime.of(rs.getTimestamp(4).toLocalDateTime(), ZoneId.systemDefault()));
				ZonedDateTime outTime = ZonedDateTime.of(rs.getTimestamp(5).toLocalDateTime(), ZoneId.systemDefault());
				if (outTime != null) {
					ticket.setOutTime(outTime);
				}
				
				ticket.setRecuringUser(rs.getBoolean(7));
			}

		} catch (Exception ex) {
			logger.error("Error getting ticket", ex);
		} finally {
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);	
		}
		return ticket;
	}

	public boolean updateTicket(Ticket ticket) {
		boolean result = false;
		Connection con = null;
		PreparedStatement ps  = null;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
			ps.setDouble(1, ticket.getPrice());
			ps.setTimestamp(2, new Timestamp(ticket.getOutTime().toInstant().toEpochMilli()));
			ps.setInt(3, ticket.getId());
			ps.execute();
			result = true;
		} catch (Exception ex) {
			logger.error("Error updating ticket info", ex);
		} finally {
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);
		}
		return result;
	}
	
	public boolean isRecuiring (String vehicleRegNumber) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean result = false;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.IS_RECUIRING);
			ps.setString(1, vehicleRegNumber);
			rs = ps.executeQuery();
			
			boolean nxt = rs.next();
			if (nxt) {
				result = (rs.getInt(1) > 0);
				
			}
			
			
		} catch (Exception ex) {
			logger.error("Error handling recuiring", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
			
		}
		return result;
	}
	
	public boolean isPresent (String vehicleRegNumber) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean result = false;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.IS_PRESENT);
			ps.setString(1, vehicleRegNumber);
			rs = ps.executeQuery();
			if (rs.next()) {
				result = (rs.getInt(1) > 0);
			}
			
			
			
		} catch (Exception ex) {
			logger.error("Error handling present", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
		}
		return result;
	}
	
	
}
