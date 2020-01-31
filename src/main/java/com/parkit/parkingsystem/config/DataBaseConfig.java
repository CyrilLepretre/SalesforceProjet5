package com.parkit.parkingsystem.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataBaseConfig {

  private static final Logger logger = LogManager.getLogger("DataBaseConfig");

  /**
   * Get an SQL connection.
   * @return Connection
   * @throws ClassNotFoundException if a class used is not found
   * @throws SQLException if an erreor is encoutered with SQL driver
   */
  public Connection getConnection() throws ClassNotFoundException, SQLException {
    logger.info("Create DB connection");
    Class.forName("com.mysql.cj.jdbc.Driver");
    return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/prod?zeroDateTimeBehavior="
                 + "CONVERT_TO_NULL&serverTimezone=UTC",
            "root","rootroot");
  }

  /**
   * Close the SQL connection.
   * @param con Connection to the SQL Database
   */
  public void closeConnection(Connection con) {
    if (con != null) {
      try {
        con.close();
        logger.info("Closing DB connection");
      } catch (SQLException e) {
        logger.error("Error while closing connection",e);
      }
    }
  }

  /**
   * Close the statement.
   * @param ps statement
   */
  public void closePreparedStatement(PreparedStatement ps) {
    if (ps != null) {
      try {
        ps.close();
        logger.info("Closing Prepared Statement");
      } catch (SQLException e) {
        logger.error("Error while closing prepared statement",e);
      }
    }
  }

  /**
   * Close the result set.
   * @param rs ResultSet
   */
  public void closeResultSet(ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
        logger.info("Closing Result Set");
      } catch (SQLException e) {
        logger.error("Error while closing result set",e);
      }
    }
  }
}
