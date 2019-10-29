package sample;

import java.sql.*;
import java.sql.Connection;

public class JdbcDao {

        //public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

        public Connection connect() {
            // write your code here

            final String DATABASE_URL = "jdbc:mysql://127.0.0.1:3306/naubsb?characterEncoding=latin1";
            final String DATABASE_USERNAME = "root";
            final String DATABASE_PASSWORD = "1234ABCD";


            Connection connect = null;

            try{

                //DriverManager.registerDriver(new com.mysql.jdbc.Driver());
                connect = DriverManager.getConnection(DATABASE_URL,DATABASE_USERNAME,DATABASE_PASSWORD);
                System.out.println("connected to database");

            }catch (SQLException se){
                se.printStackTrace();
            }
//

            return connect;
        }

        public boolean validate(String emailId, String password) throws SQLException {


            final String DATABASE_URL = "jdbc:mysql://127.0.0.1:3306/naubsb?characterEncoding=latin1";
            final String DATABASE_USERNAME = "root";
            final String DATABASE_PASSWORD = "1234ABCD";
            final String SELECT_QUERY = "SELECT * FROM gate_login WHERE username = ? and password = ?";

            // Step 1: Establishing a Connection and
            // try-with-resource statement will auto close the connection.
            try (
                    Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

                    // Step 2:Create a statement using connection object
                    PreparedStatement preparedStatement = connection.prepareStatement(SELECT_QUERY)) {
                preparedStatement.setString(1, emailId);
                preparedStatement.setString(2, password);

                System.out.println(preparedStatement);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return true;
                }


            } catch (SQLException e) {
                // print SQL exception information
                printSQLException(e);
            }
            return false;
        }

        public boolean validateRole(String emailId, String password){


        final String DATABASE_URL = "jdbc:mysql://127.0.0.1:3306/naubsb?characterEncoding=latin1";
        final String DATABASE_USERNAME = "root";
        final String DATABASE_PASSWORD = "1234ABCD";
        final String SELECT_QUERY = "SELECT role FROM gate_login WHERE username = ? and password = ?";

        // Step 1: Establishing a Connection and
        // try-with-resource statement will auto close the connection.
        try (
                Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

                // Step 2:Create a statement using connection object
                PreparedStatement preparedStatement = connection.prepareStatement(SELECT_QUERY)) {
            preparedStatement.setString(1, emailId);
            preparedStatement.setString(2, password);

            System.out.println(preparedStatement);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }


        } catch (SQLException e) {
            // print SQL exception information
            printSQLException(e);
        }
        return false;
    }

        public static void printSQLException(SQLException ex) {
            for (Throwable e: ex) {
                if (e instanceof SQLException) {
                    e.printStackTrace(System.err);
                    System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                    System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                    System.err.println("Message: " + e.getMessage());
                    Throwable t = ex.getCause();
                    while (t != null) {
                        System.out.println("Cause: " + t);
                        t = t.getCause();
                    }
                }
            }
        }





}
