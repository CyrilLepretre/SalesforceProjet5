package com.parkit.parkingsystem.model;

import java.util.Date;

/**
 * <b>Ticket describes the ticket object.</b>
 * A ticket contains :
 * <ul>
 *     <li>An id</li>
 *     <li>A parking spot object</li>
 *     <li>The vehicle license plate number</li>
 *     <li>The price of the ticket (wich will be 0 when the vehicle enters in the parking)</li>
 *     <li>The Date when the vehicle enters</li>
 *     <li>The Date when the vehicle exits</li>
 *     <li>The discount applied (for reccuring users) - Feature added 01-10-2020</li>
 * </ul>
 *
 *
 * @author Cyril Lepretre
 * @version 2.0
 */

public class Ticket {
  private int id;
  private ParkingSpot parkingSpot;
  private String vehicleRegNumber;
  private double price;
  private Date inTime;
  private Date outTime;
  private double discount;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public ParkingSpot getParkingSpot() {
    return parkingSpot;
  }

  public void setParkingSpot(ParkingSpot parkingSpot) {
    this.parkingSpot = parkingSpot;
  }

  public String getVehicleRegNumber() {
    return vehicleRegNumber;
  }

  public void setVehicleRegNumber(String vehicleRegNumber) {
    this.vehicleRegNumber = vehicleRegNumber;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public Date getInTime() {
    return inTime;
  }

  public void setInTime(Date inTime) {
    this.inTime = inTime;
  }

  public Date getOutTime() {
    return outTime;
  }

  public void setOutTime(Date outTime) {
    this.outTime = outTime;
  }

  public double getDiscount() {
    return discount;
  }

  public void setDiscount(double discount) {
    this.discount = discount;
  }

}
