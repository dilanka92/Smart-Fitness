package com.sourcey.user;

public class MySqlHandler {
//    public final static String URL = "jdbc:mysql://db4free.net:3306/fitness";
//    public final static String USER = "dilanka";
//    public final static String PASSWORD = "dilanka92";

    public final static String URL = "jdbc:mysql://192.168.1.92:3306/smartfitness";
    public final static String USER = "itesgfinance";
    public final static String PASSWORD = "itesg@finance";

    // all the DB transactions to be handled via the API
//    public Connection mySqlConnection() {
//        Connection connection = null;
//        try {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//
//            Class.forName("com.mysql.jdbc.Driver").newInstance();
//            connection = DriverManager.getConnection(URL, USER, PASSWORD);
//
//            System.out.println("Connnection Success");
//
//        } catch (Exception e) {
//            System.err.println("Connnection Failed");
//        }
//        return connection;
//    }
}
