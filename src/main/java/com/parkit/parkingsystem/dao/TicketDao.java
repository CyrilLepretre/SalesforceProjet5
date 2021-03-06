package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DbConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TicketDao {

  private static final Logger logger = LogManager.getLogger("TicketDAO");

  public DataBaseConfig dataBaseConfig = new DataBaseConfig();

  /**
   * Save a ticket in DataBase.
   *
   * @param ticket to save in DataBase
   * @return true if the ticket was correctly saved, false else
   */
  public boolean saveTicket(Ticket ticket) {
    Connection con = null;
    try {
      con = dataBaseConfig.getConnection();
      PreparedStatement ps = con.prepareStatement(DbConstants.SAVE_TICKET);
      //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
      //ps.setInt(1,ticket.getId());
      ps.setInt(1,ticket.getParkingSpot().getId());
      ps.setString(2, ticket.getVehicleRegNumber());
      ps.setDouble(3, ticket.getPrice());
      ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
      ps.setTimestamp(5,(ticket.getOutTime() == null) ? null :
              (new Timestamp(ticket.getOutTime().getTime())));
      return ps.execute();
    } catch (Exception ex) {
      logger.error("Error fetching next available slot",ex);
    } finally {
      dataBaseConfig.closeConnection(con);
      return false;
    }
  }

  /**
   * Get the ticket from DataBase of a vehicle.
   *
   * @param vehicleRegNumber corresponding to the vehicle number
   * @return the ticket of the vehicle passed as paramater
   */
  public Ticket getTicket(String vehicleRegNumber) {
    Connection con = null;
    Ticket ticket = null;
    try {
      con = dataBaseConfig.getConnection();
      PreparedStatement ps = con.prepareStatement(DbConstants.GET_TICKET);
      //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
      ps.setString(1,vehicleRegNumber);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        ticket = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1),
                ParkingType.valueOf(rs.getString(6)),false);
        ticket.setParkingSpot(parkingSpot);
        ticket.setId(rs.getInt(2));
        ticket.setVehicleRegNumber(vehicleRegNumber);
        ticket.setPrice(rs.getDouble(3));
        ticket.setInTime(rs.getTimestamp(4));
        ticket.setOutTime(rs.getTimestamp(5));
      }
      dataBaseConfig.closeResultSet(rs);
      dataBaseConfig.closePreparedStatement(ps);
    } catch (Exception ex) {
      logger.error("Error fetching next available slot",ex);
    } finally {
      dataBaseConfig.closeConnection(con);
      return ticket;
    }
  }

  /**
   * Update a ticket on DataBase.
   *
   * @param ticket to update
   * @return true if the ticket was correctly updated, false else
   */
  public boolean updateTicket(Ticket ticket) {
    Connection con = null;
    try {
      con = dataBaseConfig.getConnection();
      PreparedStatement ps = con.prepareStatement(DbConstants.UPDATE_TICKET);
      ps.setDouble(1, ticket.getPrice());
      ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
      ps.setInt(3,ticket.getId());
      ps.execute();
      return true;
    } catch (Exception ex) {
      logger.error("Error saving ticket info",ex);
    } finally {
      dataBaseConfig.closeConnection(con);
    }
    return false;
  }

  /**
   * Indicates if a vehicle has already come in the parking, and so if the user is a recurring one.
   * Added for the discount feature.
   *
   * @param vehicleRegNumber the vehicle Reg Number
   * @return true if the vehicle has already come, false else
   */
  public boolean isRecurringUser(String vehicleRegNumber) {
    Connection con = null;
    int numberTicketsVehicle = 0;
    try {
      con = dataBaseConfig.getConnection();
      PreparedStatement ps = con.prepareStatement(DbConstants.CHECK_RECURRING_USER);
      ps.setString(1,vehicleRegNumber);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        numberTicketsVehicle = rs.getInt(1);
      }
      dataBaseConfig.closeResultSet(rs);
      dataBaseConfig.closePreparedStatement(ps);
    } catch (Exception ex) {
      logger.error("Error checking recurrent user",ex);
    } finally {
      dataBaseConfig.closeConnection(con);
    }
    return (numberTicketsVehicle > 0);
  }
}
