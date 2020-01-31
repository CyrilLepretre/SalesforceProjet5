package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

  /**
   * Calculates and sets the prise of a ticket.
   *      30 minutes free (added 01/10/2020)
   *      Add a discount for recurrent users (added 01/17/2020)
   * @param ticket Ticket of a vehicle
   */
  public void calculateFare(Ticket ticket) {
    if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
      throw new IllegalArgumentException("Out time provided is incorrect:"
              + ticket.getOutTime().toString());
    }
    // Usage of getTime to get inHour and outHour milliseconds, and compare them
    long inHour = ticket.getInTime().getTime();
    long outHour = ticket.getOutTime().getTime();

    // Difference between outHour and inHour in milliseconds is a long, we use a double to
    // store it transformed in hours
    // Must divide by double and not integer to get a double result
    double duration = (outHour - inHour) / 1000.0 / 60.0 / 60.0;

    // 30 minutes free
    duration = (duration - 0.5 < 0) ? 0 : (duration - 0.5);

    switch (ticket.getParkingSpot().getParkingType()) {
      case CAR: {
        ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR * (1 - ticket.getDiscount()));
        break;
      }
      case BIKE: {
        ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR * (1 - ticket.getDiscount()));
        break;
      }
      default: throw new IllegalArgumentException("Unkown Parking Type");
    }
  }
}