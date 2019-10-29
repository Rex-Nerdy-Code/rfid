package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static sample.JdbcDao.printSQLException;

public class Controller implements Initializable {

    @FXML
    private Label Fiiname_Obj, Siiname_Obj, Matric_obj, faculty_Obj,  mat_Onj, dept_obj, otherName_obj ;

    @FXML
    private Circle Circle_Obj;

    @FXML
    private HBox center_pane;

    @FXML
    private TextField input;
    @FXML
    private Button scan_button;
    @FXML
    private Circle CircleView, logo1,  army_logo;

    @FXML
    private BorderPane mother_pane;
    private Connection conn;
    private JdbcDao jdbcDao = new JdbcDao();
    private SerialPort arduinoPort;

    @FXML
    public void initialize(URL url, ResourceBundle rb){
        Image imag = new Image("file:army.jpg", 250, 250, true, true);
        army_logo.setFill(new ImagePattern(imag));

        Image imgg = new Image("file:naub.png", 250, 250, true, true);
        logo1.setFill(new ImagePattern(imgg));
    }

    @FXML
    public void Search() {
        System.out.println(input.getText());

        if (input.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please Enter Valid ID");
        }

        try {
            String Matric_No;
            Matric_No = input.getText().toLowerCase();
            conn = jdbcDao.connect();

            boolean flag = validateSearch(Matric_No);

            Statement stmt;
            stmt = conn.createStatement();
            String query = "SELECT First_name, Surname, Other_name, matric_No, Faculty, dept, image FROM subdb WHERE matric_No = '" + Matric_No + "'";
            ResultSet rs = stmt.executeQuery(query);
            System.out.println("making request");


            if (!flag) {
                System.out.println("Display Error Page");
                try {
                    Parent content = FXMLLoader.load(getClass().getResource("error_page.fxml"));
                    mother_pane.setCenter(content);

                } catch (IOException E) {
                    E.printStackTrace();
                }


            } else {

                        Platform.runLater(() -> {
                            try {
                                while (rs.next()) {
                            System.out.println("try to collect");
                            System.out.println(rs.getString("First_name"));

                            try {


                                FXMLLoader loader = new FXMLLoader();
                                loader.setLocation(Controller.class.getResource("profilepage.fxml"));
                                loader.setController(this);
                                System.out.println("collected ui successfully");
                                mother_pane.setCenter(loader.load());
                                Fiiname_Obj.setText(rs.getString("First_name"));
                                Siiname_Obj.setText(rs.getString("Surname"));
                                otherName_obj.setText(rs.getString("Other_name"));
                                faculty_Obj.setText(rs.getString("Faculty"));
                                mat_Onj.setText(rs.getString("matric_No"));
                                dept_obj.setText(rs.getString("dept"));
                                try {
                                InputStream is = rs.getBinaryStream("image");
                                OutputStream os = new FileOutputStream(new File("img.jpg"));
                                 byte[] content = new byte[1024];
                                 int size = 0;
                                while ((size = is.read(content)) != -1) {
                                os.write(content, 0, size);
                                 }
                                os.close();
                                is.close();
                                Image imagex = new Image("file:img.jpg", 250, 250, true, true);
                                Circle_Obj.setFill(new ImagePattern(imagex));

                                } catch (IOException e) {
                                    System.err.println("An IOException was caught!");
                                    e.printStackTrace();
                                }

                            }catch (IOException e){
                                e.printStackTrace();
                            }
                             }
                            } catch (SQLException E) {
                                E.printStackTrace();
                            }
                        });
            }
//            // CLOSE ALL OPEN CONNECTIONS
//            rs.close();
//            stmt.close();
//            conn.close();

        } catch (SQLException ex) {
            Logger.getLogger(JdbcDao.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("the thing get error ohh");
            System.out.println("End of code");


        }

    }

//      alert function
    private void showAlert (Alert.AlertType alertType, String title, String msg){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();

    }

    // Scan Button to activate arduino and prepare for scan
    public void scan () {
        ObservableList<String> portList;
        portList = FXCollections.observableArrayList();
        //String name;

        String[] serialPortNames = SerialPortList.getPortNames();
        for (String name : serialPortNames) {
            System.out.println(name);
            portList.add(name);
        }


        mother_pane.setCenter(center_pane);

        connectArduino("COM16");
        scan_button.setDisable(true);





    }

    //ConnectArduino Function to perform jssc connection
    private boolean connectArduino (String port){
        System.out.println("connect Arduino");

        boolean success = false;
        SerialPort serialPort = new SerialPort(port);
        try {
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
            serialPort.addEventListener((SerialPortEvent serialPortEvent) -> {
                if (serialPortEvent.isRXCHAR() && serialPortEvent.getEventValue() >= 13) {
                    try {
                        String st = serialPort.readString(serialPortEvent.getEventValue());

                        System.out.println(st);

                        Platform.runLater(() -> {

                            try {
                                conn = jdbcDao.connect();
                                Statement stmt;
                                stmt = conn.createStatement();
                                String query = "SELECT First_name, Surname, Other_name, matric_No, Faculty, dept, image FROM subdb WHERE rfid = '" + st + "'";
                                ResultSet rs = stmt.executeQuery(query);
                                System.out.println("making request");

                                if (!rs.isBeforeFirst()) {
                                    System.out.println("Display Error Page");
                                    try {
                                        Parent content = FXMLLoader.load(getClass().getResource("error_page.fxml"));
                                        mother_pane.setCenter(content);
                                    } catch (IOException E) {
                                        System.err.println(E);
                                    }
                                    try {
                                        serialPort.writeString("2");
                                    }catch (SerialPortException e){
                                        e.printStackTrace();
                                    }


                                } else {
                                    while (rs.next()) {
                                        System.out.println("Display Profile Page");
                                        try {
                                            FXMLLoader loader = new FXMLLoader();
                                            loader.setLocation(Controller.class.getResource("profilepage.fxml"));
                                            loader.setController(this);
                                            System.out.println("collected ui successfully");
                                            mother_pane.setCenter(loader.load());

                                        } catch (IOException E) {
                                            System.err.println(E);
                                        }
                                        Fiiname_Obj.setText(rs.getString("First_name"));
                                        Siiname_Obj.setText(rs.getString("Surname"));
                                        otherName_obj.setText(rs.getString("Other_name"));
                                        faculty_Obj.setText(rs.getString("Faculty"));
                                        mat_Onj.setText(rs.getString("matric_No"));
                                        dept_obj.setText(rs.getString("dept"));

                                        try {
                                            InputStream is = rs.getBinaryStream("image");
                                            OutputStream os = new FileOutputStream(new File("img.jpg"));
                                            byte[] content = new byte[1024];
                                            int size = 0;
                                            while ((size = is.read(content)) != -1) {
                                                os.write(content, 0, size);
                                            }
                                            os.close();
                                            is.close();
                                            Image imagex = new Image("file:img.jpg", 250, 250, true, true);
                                            Circle_Obj.setFill(new ImagePattern(imagex));


                                        } catch (IOException e) {
                                            System.err.println("An IOException was caught!");
                                            e.printStackTrace();
                                        }

                                    }
                                    try {
                                        serialPort.writeString("1");
                                    }catch (SerialPortException e){
                                        e.printStackTrace();
                                    }

                                }
                                rs.close();
                                stmt.close();
                                conn.close();


                            } catch (SQLException ex) {
                                Logger.getLogger(JdbcDao.class.getName()).log(Level.SEVERE, null, ex);
                                System.out.println("the Database connection get error ohh");
                            }
                        });


                        System.out.println("End of connection.");
                    } catch (SerialPortException ex) {
                        System.out.println("Hardware not Connected");
                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);

                    }
                }
            });

            arduinoPort = serialPort;
            success = true;
        } catch (SerialPortException ex) {
            //Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SerialPortException: " + ex.toString());
            scan_button.setDisable(false);
            showAlert(Alert.AlertType.ERROR, "Error", "HARDWARE NOT FOUND, PLEASE CHECK CONNECTIONS");

        }

        return success;
    }

    //make connection to database,validate search and return boolean value
    private boolean validateSearch (String Matric){


        final String DATABASE_URL = "jdbc:mysql://127.0.0.1:3306/naubsb?characterEncoding=latin1";
        final String DATABASE_USERNAME = "root";
        final String DATABASE_PASSWORD = "1234ABCD";
        //final String SELECT_QUERY = "SELECT * FROM gate_login WHERE username = ? and password = ?";
        final String SELECT_QUERY = "SELECT First_name, Surname, Other_name, matric_No, Faculty, dept, image FROM subdb WHERE matric_No = ?";

        // Step 1: Establishing a Connection and
        // try-with-resource statement will auto close the connection.
        try (
                Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

                // Step 2:Create a statement using connection object
                PreparedStatement preparedStatement = connection.prepareStatement(SELECT_QUERY)) {
            preparedStatement.setString(1, Matric);

            System.out.println(preparedStatement);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("this is the result: " + resultSet);
                return true;
            }


        } catch (SQLException e) {
            // print SQL exception information
            printSQLException(e);
        }
        return false;
    }

    //menu_item function to log user out.
    public void logout(){
        System.out.println("Logging out...");
        try {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            Stage stage = new Stage();
            stage.setTitle("NIGERIAN ARMY UNIVERSITY BIU");
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();

            Stage pstage = (Stage) mother_pane.getScene().getWindow();
            pstage.close();


        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }

    //StopScan function to disconnect arduino and return to homepage.
    public void StopScan(){
        System.out.println("Stopping Scan..");
        mother_pane.setCenter(center_pane);
        disconnectArduino();
        scan_button.setDisable(false);

    }

    //function to send alert to all security offices
    public void RaiseAlarm(){
        System.out.println("Alert all Security Offices of Threat");
    }

    //function to disconnect all jssc connections.
    private void disconnectArduino(){
        System.out.println("Disconnect Arduino...");
        if (arduinoPort != null){
            try {
                arduinoPort.removeEventListener();

                if (arduinoPort.isOpened()) {
                    arduinoPort.closePort();
                }
            } catch (SerialPortException ex){
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE,null, ex);
            }
        }else{
            System.out.println("Arduino not connected");
        }
    }
}
