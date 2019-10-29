package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddStaffController implements Initializable {

    @FXML
    private TextField password_field, username_field, id_field, fullname_field;

    @FXML
    private ListView <String> listView;

    @FXML
    private TableView<staff2> tableView;

    @FXML
    private TableColumn<staff2, String> fullnameCol;

    @FXML
    private TableColumn<staff2, String> idCol;

    @FXML
    private TableColumn<staff2, String> roleCol;

    @FXML
    private TableColumn<staff2, String> usernameCol;

    @FXML
    private TableColumn<staff2, String> passwordCol;

    @FXML
    private ChoiceBox <String> role;

    private ObservableList<staff2> data = FXCollections.observableArrayList();

    private JdbcDao jdbcDao = new JdbcDao();

    @FXML
    public void initialize(URL url, ResourceBundle rb){
        UpdateTable();

    }


    public void Submit(){

        String username = username_field.getText();
        String password = password_field.getText();
        String fullname = fullname_field.getText();
        String id = id_field.getText();
        role.setValue("admin");
        String ROLE = role.getValue();


        try{
            Connection conn;
            conn = jdbcDao.connect();
            Statement stmt;
            stmt = conn.createStatement();
            String query = "UPDATE gate_login SET username = " + username ;//, password = '" + password + "', Fullname = '" + fullname + "', ID_number = '" + id + "'";
            String querry = "INSERT INTO gate_login VALUES('" + username +"','" + password + "','"+ ROLE + "','" + fullname + "','"+ id + "')";
            stmt.executeUpdate(querry);
            tableView.refresh();
        }catch (SQLException ex) {
            Logger.getLogger(JdbcDao.class.getName()).log(Level.SEVERE, null, ex);
            String st = "java.sql.SQLIntegrityConstraintViolationException: Duplicate entry '" + username + "' for key 'PRIMARY'";
            if (ex.equals(st)){
                showAlert(Alert.AlertType.ERROR, "Error", "Username already Exist");
            }
            System.out.println("the Database connection get error ohh");
        }

    }

    private void UpdateTable(){
        try {

            Connection connection = jdbcDao.connect();
            Statement statement = connection.createStatement();
            String Query = "SELECT * FROM gate_login";
            ResultSet resultSet = statement.executeQuery(Query);
            System.out.println("executing update query");

            while (resultSet.next()){
                data.add(new staff2(
                        resultSet.getString("Fullname"),
                        resultSet.getString("ID_number"),
                        resultSet.getString("role"),
                        resultSet.getString("username"),
                        resultSet.getString("password")));
            }

            fullnameCol.setCellValueFactory(new PropertyValueFactory<>("fullname"));
            idCol.setCellValueFactory(new PropertyValueFactory<>("id_num"));
            roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
            usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
            passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));

            tableView.setItems(data);

        }catch (SQLException e){
            Logger.getLogger(JdbcDao.class.getName()).log(Level.SEVERE, null, e);

            System.out.println("Error in update database connection");
        }

    }

    //      alert function
    private void showAlert (Alert.AlertType alertType, String title, String msg) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }


    }
