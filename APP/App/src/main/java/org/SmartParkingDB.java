package org;

import java.sql.*;

public class SmartParkingDB {
    @SuppressWarnings("finally")
    private static Connection makeJDBCConnection() {
        Connection databaseConnection = null;

        String databaseIP = "localhost";
        // String databasePort = "3306";
        String databaseUsername = "guard";
        String databasePassword = "guard";
        String databaseName = "smart_parking";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");//checks if the Driver class exists (correctly available)
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return databaseConnection;
        }

        try {
            // DriverManager: The basic service for managing a set of JDBC drivers.
            databaseConnection = DriverManager.getConnection(
                    "jdbc:mysql://" + databaseIP + "/" + databaseName + "?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=CET",
                    databaseUsername,
                    databasePassword);
            // The Driver Manager provides the connection specified in the parameter string
            if (databaseConnection == null) {
                System.err.println("Connection to Db failed");
            }
        } catch (SQLException e) {
            System.err.println("MySQL Connection Failed!\n");
            e.printStackTrace();
        }finally {
            return databaseConnection;
        }
    }



    public static void insertCarStatus(final int node, final boolean boolCarStatus) {
        String insertQueryStatement = "INSERT INTO carDetected (node,carPresent) VALUES (?, ?)";

        try (Connection appConnectionToDB = makeJDBCConnection();
             PreparedStatement appPrepareStatement = appConnectionToDB.prepareStatement(insertQueryStatement);
        ) {
            appPrepareStatement.setInt(1, node);
            appPrepareStatement.setBoolean(2,boolCarStatus);
            appPrepareStatement.executeUpdate();

        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
    }

    public static void insertOccupancy(final int node, final int occupancy) {
        String insertQueryStatement = "INSERT INTO occupancy (node,occupancy) VALUES (?, ?)";

        try (Connection appConnectionToDB = makeJDBCConnection();
             PreparedStatement appPrepareStatement = appConnectionToDB.prepareStatement(insertQueryStatement);
        ) {
            appPrepareStatement.setInt(1, node);
            appPrepareStatement.setInt(2, occupancy);
            appPrepareStatement.executeUpdate();

        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
    }
}

