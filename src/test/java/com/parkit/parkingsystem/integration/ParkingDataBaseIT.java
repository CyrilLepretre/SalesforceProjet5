package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

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
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){
        int spotBefore = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR); // get available spot before incoming vehicle
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        int spotAfter = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR); // get available spot after incoming vehicle
        assertNotEquals(spotBefore, spotAfter);
        //Get a ticket from the DB with the vehicle Registration Number previously added with processIncomingVehicle()
        Ticket ticketFromDB = ticketDAO.getTicket(this.getVehicleRegistrationNumber(inputReaderUtil));
        assertEquals(ticketFromDB.getVehicleRegNumber(), "ABCDEF");
    }

    @Test
    public void testParkingLotExit(){
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        // Use of a sleep to force the outTime to be after the inTime in database
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        parkingService.processExitingVehicle();
        Ticket ticketFromDB = ticketDAO.getTicket(this.getVehicleRegistrationNumber(inputReaderUtil));
        // Now, the price must be 0.0 as we offer 30 minutes free
        assertEquals(ticketFromDB.getPrice(), 0.0);
        assertTrue(ticketFromDB.getInTime().before(ticketFromDB.getOutTime()));
    }

    private String getVehicleRegistrationNumber(InputReaderUtil inputReaderUtil){
        String vehicleRegistrationNumber = null;
        try {
            vehicleRegistrationNumber = inputReaderUtil.readVehicleRegistrationNumber();
        } catch (Exception e) {
            System.out.println("Error while getting vehicle registration number");
        }
        return vehicleRegistrationNumber;
    }

}
