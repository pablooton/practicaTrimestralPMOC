package edu.pmoc.practicatrim.practicatrimestralpmoc.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static  final String URL = "jdbc:mysql://localhost/fantasy";
    private static final String user = "root";
    private static  final String password = "";

    private static Connection connection = null;

    public DatabaseConnection(){}

    public static  Connection getConnection(){
        try {
            if (connection == null || connection.isClosed()){
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    connection = DriverManager.getConnection(URL,user,password);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }
}
