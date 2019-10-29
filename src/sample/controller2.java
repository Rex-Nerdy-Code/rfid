package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static sample.JdbcDao.printSQLException;

public class controller2 implements Initializable {


    @FXML
    private Circle logo1;

    @FXML
    private Circle army_logo;

    @FXML
    private VBox MOTHER_PANE;

    @FXML
    private VBox form_pane;

    @FXML
    private HBox logo_pane;

    @FXML
    private TextField username_fd;

    @FXML
    private PasswordField password_fd;

    @FXML
    private Button LOGIN_BUTTON;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // jdbcDao = new JdbcDao();

        Image imag = new Image("file:army.jpg", 250, 250, true, true);
        army_logo.setFill(new ImagePattern(imag));

        Image imgg = new Image("file:naub.png", 250, 250, true, true);
        logo1.setFill(new ImagePattern(imgg));

    }

    public void login2() throws SQLException {
        if (username_fd.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "form error", "Please Enter valid Username");
        }

        String emailId = username_fd.getText();
        String password = password_fd.getText();

        JdbcDao jdbcDao = new JdbcDao();
        boolean flag = jdbcDao.validate(emailId, password);

        final String DATABASE_URL = "jdbc:mysql://127.0.0.1:3306/naubsb?characterEncoding=latin1";
        final String DATABASE_USERNAME = "root";
        final String DATABASE_PASSWORD = "1234ABCD";
        final String SELECT_QUERY = "SELECT role FROM gate_login WHERE username = ? and password = ?";

        // Step 1: Establishing a Connection and
        // try-with-resource statement will auto close the connection.


        if (!flag) {
            infoBox("Please enter correct Username and Password", null, "Login Failed");
        } else {


            try (
                    Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

                    // Step 2:Create a statement using connection object
                    PreparedStatement preparedStatement = connection.prepareStatement(SELECT_QUERY)) {
                preparedStatement.setString(1, emailId);
                preparedStatement.setString(2, password);

                System.out.println(preparedStatement);

                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String rss = resultSet.getString("role");
                    if(rss.equals("gate")){
                        System.out.println("this is the Gateman, open gate app");
                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader();
                            fxmlLoader.setLocation(getClass().getResource("sample.fxml"));
                            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
                            Stage stage = new Stage();
                            stage.setTitle("NIGERIAN ARMY UNIVERSITY BIU");
                            stage.setScene(scene);
                            stage.setFullScreen(true);
                            stage.show();
                            Stage pstage = (Stage) username_fd.getScene().getWindow();
                            pstage.close();
                        } catch (IOException e) {
                            Logger logger = Logger.getLogger(getClass().getName());
                            logger.log(Level.SEVERE, "Failed to create new Window.", e);
                        }
                    }else if (rss.equals("exams")){
                        System.out.println("this is the examiner, Open Exams app");
                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader();
                            fxmlLoader.setLocation(getClass().getResource("Exams_page.fxml"));
                            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
                            Stage stage = new Stage();
                            stage.setTitle("NIGERIAN ARMY UNIVERSITY BIU");
                            stage.setScene(scene);
                            stage.setFullScreen(true);
                            stage.show();
                            Stage pstage = (Stage) username_fd.getScene().getWindow();
                            pstage.close();
                        } catch (IOException e) {
                            Logger logger = Logger.getLogger(getClass().getName());
                            logger.log(Level.SEVERE, "Failed to create new Window.", e);
                        }
                    }else if (rss.equals("admin")){
                        System.out.println("this is an admin, open admin log");
                        try{
                            FXMLLoader fxmlLoader = new FXMLLoader();
                            fxmlLoader.setLocation(getClass().getResource("admin_page.fxml"));
                            Scene scene = new Scene(fxmlLoader.load(), 800,600);
                            Stage stage = new Stage();
                            stage.setTitle("NIGERIAN ARMY ARMY UNIVERSITY");
                            stage.setScene(scene);
                            stage.setFullScreen(true);
                            stage.show();
                            Stage pstage = (Stage) username_fd.getScene().getWindow();
                            pstage.close();
                        }catch (IOException e){
                            Logger logger = Logger.getLogger(getClass().getName());
                            logger.log(Level.SEVERE, "failed to create admin window.", e);
                        }
                    }
                }


            } catch (SQLException e) {
                // print SQL exception information
                printSQLException(e);
            }


            //infoBox("Login Successful!", null, "Success");



        }

    }

//    public void handleCloseButtonAction(ActionEvent event) {
//        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
//    }

    private void showAlert(Alert.AlertType alertType, String title, String msg) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();

    }

    private static void infoBox(String infoMessage, String headerText, String title) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(infoMessage);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.showAndWait();
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

}
