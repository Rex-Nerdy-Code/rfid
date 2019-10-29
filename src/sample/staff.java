//package sample;

import javafx.beans.property.SimpleStringProperty;

public class staff {

    String fullname, username, password, role, id;

    public SimpleStringProperty fullName = new SimpleStringProperty();
    public  SimpleStringProperty UserName = new SimpleStringProperty();
    public  SimpleStringProperty Password = new SimpleStringProperty();
    public  SimpleStringProperty Role = new SimpleStringProperty();
    public  SimpleStringProperty IDNumber = new SimpleStringProperty();

    public staff(String fullname, String id, String role, String username, String password) {
        this.fullname = fullname;
        this.username = username;
        this.password = password;
        this.role = role;
        this.id = id;
    }


    public String getFullName() {
        return fullName.get();
    }
    public void setFullName(String firstNameStr) {
        fullName.set(firstNameStr);
    }
    public String getUserName() {
        return UserName.get();
    }
    public void setUserName(String lastNameStr) {
        UserName.set(lastNameStr);
    }
    public String getPassword() {
        return Password.get();
    }
    public void setpassword(String address) {
        this.Password.set(address);
    }
    public void setRole(String type) {
        this.Role.set(type);
    }

    public String getRole() {
        return this.Role.get();
    }
    public void setIDNumber(String phone) {
        this.IDNumber.set(phone);
    }
    public String getIDNumber() {
        return this.IDNumber.get();
    }
}
