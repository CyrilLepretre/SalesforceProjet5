package com.parkit.parkingsystem.model;

import com.parkit.parkingsystem.constants.ParkingType;

public class ParkingSpot {
  private int number;
  private ParkingType parkingType;
  private boolean isAvailable;

  /**
   * Constructor.
   * @param number corresponding to the place
   * @param parkingType represents the kinf of parking (for a car, a bike ...)
   * @param isAvailable true if the spot is available, false else
   */
  public ParkingSpot(int number, ParkingType parkingType, boolean isAvailable) {
    this.number = number;
    this.parkingType = parkingType;
    this.isAvailable = isAvailable;
  }

  public int getId() {
    return number;
  }

  /*public void setId(int number) {
    this.number = number;
  }*/

  public ParkingType getParkingType() {
    return parkingType;
  }

  /*public void setParkingType(ParkingType parkingType) {
    this.parkingType = parkingType;
  }*/

  public boolean isAvailable() {
    return isAvailable;
  }

  public void setAvailable(boolean available) {
    isAvailable = available;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ParkingSpot that = (ParkingSpot) o;
    return number == that.number;
  }

  @Override
  public int hashCode() {
    return number;
  }
}
