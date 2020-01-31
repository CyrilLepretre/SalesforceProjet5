package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDao;
import com.parkit.parkingsystem.dao.TicketDao;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParkingService {

  private static final Logger logger = LogManager.getLogger("ParkingService");

  private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

  private InputReaderUtil inputReaderUtil;
  private ParkingSpotDao parkingSpotDao;
  private TicketDao ticketDao;

  /**
   * Initializes a parking service.
   * @param inputReaderUtil input reader
   * @param parkingSpotDao DAO for parking spot
   * @param ticketDao DAO for ticket
   */
  public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDao parkingSpotDao,
                        TicketDao ticketDao) {
    this.inputReaderUtil = inputReaderUtil;
    this.parkingSpotDao = parkingSpotDao;
    this.ticketDao = ticketDao;
  }

  /**
   * Process of incoming vehicles.
   * Feature added on 01/10/2020 : controls if it's a reccurent user in order to display a message
   *                               for a discount
   *                               based on a request wich verify if there is already a ticket in
   *                               database with OUT_TIME not null
   */

  public void processIncomingVehicle() {
    try {
      ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
      if (parkingSpot != null && parkingSpot.getId() > 0) {
        String vehicleRegNumber = getVehichleRegNumber();
        Ticket ticket = new Ticket();
        this.discountProcess(parkingSpot.getParkingType(), ticket, true,
                ticketDao.isRecurringUser(vehicleRegNumber));
        parkingSpot.setAvailable(false);
        //Allot this parking space and mark it's availability as false
        parkingSpotDao.updateParking(parkingSpot);

        Date inTime = new Date();
        //ticket.setId(ticketID);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber(vehicleRegNumber);
        ticket.setPrice(0);
        ticket.setInTime(inTime);
        ticket.setOutTime(null);
        ticketDao.saveTicket(ticket);
        System.out.println("Generated Ticket and saved in DB");
        System.out.println("Please park your vehicle in spot number:" + parkingSpot.getId());
        System.out.println("Recorded in-time for vehicle number:"
                + vehicleRegNumber + " is:" + inTime);
      }
    } catch (Exception e) {
      logger.error("Unable to process incoming vehicle",e);
    }
  }

  /**
   * Sets the discount value for recurring users and prints a specif message for incoming vehicles.
   * @param type of parking
   * @param ticket of the current user
   * @param incoming true if the vehicle is incoming, false if exiting
   * @param recurrentUser true if the user has already come, false else
   */
  private void discountProcess(ParkingType type, Ticket ticket,
                               boolean incoming, boolean recurrentUser) {
    if (recurrentUser) {
      switch (type) {
        case CAR:
          if (incoming) {
            System.out.println("Welcome back! As a recurring user of our parking lot, you'll "
                    + "benefit from a " + (int) (Fare.CAR_DISCOUNT * 100) + "% discount.");
          }
          ticket.setDiscount(Fare.CAR_DISCOUNT);
          break;
        case BIKE:
          if (incoming) {
            System.out.println("Welcome back! As a recurring user of our parking lot, you'll "
                    + "benefit from a " + (int) (Fare.BIKE_DISCOUNT * 100) + "% discount.");
          }
          ticket.setDiscount(Fare.BIKE_DISCOUNT);
          break;
        default: {
          System.out.println("Unknown type of vehicle");
        }
      }
    } else {
      ticket.setDiscount(0.0);
    }
  }

  /**
   * Get the registration number of a vehicle.
   * @return the registration number given by the user
   * @throws Exception if an incorrect value is given
   */
  private String getVehichleRegNumber() throws Exception {
    System.out.println("Please type the vehicle registration number and press enter key");
    return inputReaderUtil.readVehicleRegistrationNumber();
  }

  /**
   * Get the next parking available.
   * @return the parking spot with parking number found
   */
  public ParkingSpot getNextParkingNumberIfAvailable() {
    int parkingNumber = 0;
    ParkingSpot parkingSpot = null;
    try {
      ParkingType parkingType = getVehichleType();
      parkingNumber = parkingSpotDao.getNextAvailableSlot(parkingType);
      if (parkingNumber > 0) {
        parkingSpot = new ParkingSpot(parkingNumber,parkingType, true);
      } else {
        throw new Exception("Error fetching parking number from DB. Parking slots might be full");
      }
    } catch (IllegalArgumentException ie) {
      logger.error("Error parsing user input for type of vehicle", ie);
    } catch (Exception e) {
      logger.error("Error fetching next available parking slot", e);
    }
    return parkingSpot;
  }

  private ParkingType getVehichleType() {
    System.out.println("Please select vehicle type from menu");
    System.out.println("1 CAR");
    System.out.println("2 BIKE");
    int input = inputReaderUtil.readSelection();
    switch (input) {
      case 1: {
        return ParkingType.CAR;
      }
      case 2: {
        return ParkingType.BIKE;
      }
      default: {
        System.out.println("Incorrect input provided");
        throw new IllegalArgumentException("Entered input is invalid");
      }
    }
  }

  /**
   * Process of exiting vehicles.
   * Feature added on 01/10/2020 : controls if it's a reccurent user in order to apply
   * a discout, 0 else
   */

  public void processExitingVehicle() {
    try {
      String vehicleRegNumber = getVehichleRegNumber();
      Ticket ticket = ticketDao.getTicket(vehicleRegNumber);
      ParkingSpot parkingSpot = ticket.getParkingSpot();
      this.discountProcess(parkingSpot.getParkingType(), ticket, false,
              ticketDao.isRecurringUser(vehicleRegNumber));
      Date outTime = new Date();
      ticket.setOutTime(outTime);
      fareCalculatorService.calculateFare(ticket);
      if (ticketDao.updateTicket(ticket)) {
        parkingSpot.setAvailable(true);
        parkingSpotDao.updateParking(parkingSpot);
        System.out.println("Please pay the parking fare:" + ticket.getPrice());
        System.out.println("Recorded out-time for vehicle number:"
                + ticket.getVehicleRegNumber() + " is:" + outTime);
      } else {
        System.out.println("Unable to update ticket information. Error occurred");
      }
    } catch (Exception e) {
      logger.error("Unable to process exiting vehicle",e);
    }
  }
}
