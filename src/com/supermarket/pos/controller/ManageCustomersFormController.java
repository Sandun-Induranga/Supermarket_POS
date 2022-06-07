package com.supermarket.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.supermarket.pos.bo.BOFactory;
import com.supermarket.pos.bo.custom.CustomerBO;
import com.supermarket.pos.dto.CustomerDTO;
import com.supermarket.pos.util.ValidationUtil;
import com.supermarket.pos.view.tdm.CustomerTM;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

public class ManageCustomersFormController {
    public AnchorPane context;
    public JFXTextField txtID;
    public JFXTextField txtTitle;
    public JFXTextField txtName;
    public JFXTextField txtAddress;
    public JFXTextField txtCity;
    public JFXTextField txtProvince;
    public JFXTextField txtPostalCode;
    public JFXButton btnSave;
    public JFXButton btnDelete;
    public TableView<CustomerTM> tblCustomers;
    private final CustomerBO customerBO = (CustomerBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.CUSTOMER);
    public JFXButton btnAddNew;
    public TextField txtSearch;
    public JFXComboBox<String> cmbSearchField;
    LinkedHashMap<JFXTextField, Pattern> map = ValidationUtil.getPatternMap();

    public void initialize() {
        tblCustomers.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("cusID"));
        tblCustomers.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("title"));
        tblCustomers.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblCustomers.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("address"));
        tblCustomers.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("city"));
        tblCustomers.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("province"));
        tblCustomers.getColumns().get(6).setCellValueFactory(new PropertyValueFactory<>("postalCode"));

        tblCustomers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnDelete.setDisable(newValue == null);
            btnSave.setText(newValue != null ? "Update" : "Save");
            btnSave.setDisable(newValue == null);

            if (newValue != null) {
                txtID.setText(newValue.getCusID());
                txtTitle.setText(newValue.getTitle());
                txtName.setText(newValue.getName());
                txtAddress.setText(newValue.getAddress());
                txtCity.setText(newValue.getCity());
                txtProvince.setText(newValue.getProvince());
                txtPostalCode.setText(newValue.getPostalCode());

                txtID.setDisable(false);
                txtTitle.setDisable(false);
                txtName.setDisable(false);
                txtAddress.setDisable(false);
                txtCity.setDisable(false);
                txtProvince.setDisable(false);
                txtPostalCode.setDisable(false);
            }
        });
        cmbSearchField.getItems().addAll("Customer ID", "Title", "Customer Name", "City", "Province");
        cmbSearchField.setValue("Customer ID");
        initUI();
        loadAllCustomers();
        setValidations();
    }

    private void setValidations() {
        Pattern idPattern = Pattern.compile("^(CUS)[0-9]{3}$");
        Pattern titlePattern = Pattern.compile("^[A-z ]{3,15}$");
        Pattern namePattern = Pattern.compile("^[A-z ]{3,15}$");
        Pattern addressPattern = Pattern.compile("^[A-z0-9 ,/]{4,20}$");
        Pattern cityPattern = Pattern.compile("^[A-z0-9 ,/]{4,20}$");
        Pattern provincePattern = Pattern.compile("^[A-z0-9 ,/]{4,20}$");
        Pattern postalCodePattern = Pattern.compile("^[A-z0-9 ,/]{4,20}$");

        map.put(txtID, idPattern);
        map.put(txtTitle, titlePattern);
        map.put(txtName, namePattern);
        map.put(txtAddress, addressPattern);
        map.put(txtCity, cityPattern);
        map.put(txtProvince, provincePattern);
        map.put(txtPostalCode, postalCodePattern);
    }

    private void loadAllCustomers() {
        tblCustomers.getItems().clear();
        /*Get all customers*/
        try {
            ArrayList<CustomerDTO> allCustomers = customerBO.getAllCustomer();
            for (CustomerDTO customer : allCustomers) {
                tblCustomers.getItems().add(new CustomerTM(customer.getCusID(), customer.getTitle(), customer.getName(), customer.getAddress(), customer.getCity(), customer.getProvince(), customer.getPostalCode()));
            }
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    public void textFieldsKeyReleasedOnAction(KeyEvent keyEvent) {
        ValidationUtil.validate(map, btnSave);

        if (keyEvent.getCode() == KeyCode.ENTER) {
            Object response = ValidationUtil.validate(map, btnSave);

            if (response instanceof JFXTextField) {
                JFXTextField textField = (JFXTextField) response;
                textField.requestFocus();
            } else if (response instanceof Boolean) {
                System.out.println("Work");
                saveCustomer();
            }
        }
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        saveCustomer();
    }

    private void saveCustomer() {
        if (btnSave.getText().equalsIgnoreCase("save")) {
            /*Save Customer*/
            try {
                if (existCustomer(txtID.getText())) {
                    new Alert(Alert.AlertType.ERROR, txtID.getText() + " already exists").show();
                }
                customerBO.saveCustomer(new CustomerDTO(txtID.getText(), txtTitle.getText(), txtName.getText(), txtAddress.getText(), txtCity.getText(), txtProvince.getText(), txtPostalCode.getText()));
                tblCustomers.getItems().add(new CustomerTM(txtID.getText(), txtTitle.getText(), txtName.getText(), txtAddress.getText(), txtCity.getText(), txtProvince.getText(), txtPostalCode.getText()));
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Failed to save the customer " + e.getMessage()).show();
                e.printStackTrace();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        } else {
            /*Update customer*/
            try {
                if (!existCustomer(txtID.getText())) {
                    new Alert(Alert.AlertType.ERROR, "There is no such customer associated with the id " + txtID.getText()).show();
                }
                customerBO.updateCustomer(new CustomerDTO(txtID.getText(), txtTitle.getText(), txtName.getText(), txtAddress.getText(), txtCity.getText(), txtProvince.getText(), txtPostalCode.getText()));

            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Failed to update the customer " + txtID.getText() + e.getMessage()).show();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            CustomerTM selectedCustomer = tblCustomers.getSelectionModel().getSelectedItem();
            selectedCustomer.setTitle(txtTitle.getText());
            selectedCustomer.setName(txtName.getText());
            selectedCustomer.setAddress(txtAddress.getText());
            selectedCustomer.setCity(txtCity.getText());
            selectedCustomer.setProvince(txtProvince.getText());
            selectedCustomer.setPostalCode(txtPostalCode.getText());
            tblCustomers.refresh();
        }
        btnAddNew.fire();
    }

    boolean existCustomer(String id) throws SQLException, ClassNotFoundException {
        return customerBO.exitsCustomer(id);
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        /*Delete Customer*/
        String id = tblCustomers.getSelectionModel().getSelectedItem().getCusID();
        try {
            if (!existCustomer(id)) {
                new Alert(Alert.AlertType.ERROR, "There is no such customer associated with the id " + id).show();
            }
            customerBO.deleteCustomer(id);

            tblCustomers.getItems().remove(tblCustomers.getSelectionModel().getSelectedItem());
            tblCustomers.getSelectionModel().clearSelection();
            initUI();

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to delete the customer " + id).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        txtID.setDisable(true);
        txtTitle.setDisable(true);
        txtName.setDisable(true);
        txtAddress.setDisable(true);
        txtCity.setDisable(true);
        txtProvince.setDisable(true);
        txtPostalCode.setDisable(true);
        btnSave.setDisable(true);
        btnDelete.setDisable(true);
    }

    public void navigateToHome(MouseEvent mouseEvent) throws IOException {
        URL resource = this.getClass().getResource("/com/supermarket/pos/view/main-form.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) (this.context.getScene().getWindow());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        Platform.runLater(primaryStage::sizeToScene);
    }

    public void btnAddNewOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        txtID.setDisable(false);
        txtTitle.setDisable(false);
        txtName.setDisable(false);
        txtAddress.setDisable(false);
        txtCity.setDisable(false);
        txtProvince.setDisable(false);
        txtPostalCode.setDisable(false);

        txtID.clear();
        txtTitle.clear();
        txtCity.clear();
        txtName.clear();
        txtAddress.clear();
        txtProvince.clear();
        txtPostalCode.clear();

        txtID.setText(generateNewId());
        txtTitle.clear();
        txtName.clear();
        txtCity.clear();
        txtAddress.clear();
        txtProvince.clear();
        txtPostalCode.clear();
        txtTitle.requestFocus();
        btnSave.setDisable(false);
        btnSave.setText("Save");
        tblCustomers.getSelectionModel().clearSelection();
    }

    private String generateNewId() throws SQLException, ClassNotFoundException {
        return customerBO.generateNewCustomerId();
    }

    public void searchOnAction(KeyEvent keyEvent) {
        String field = cmbSearchField.getValue();
        switch (field) {
            case "Customer ID":
                field = "cusID";
                break;
            case "Customer Name":
                field = "name";
                break;
            case "Title":
                field = "title";
                break;
            case "City":
                field = "city";
                break;
            case "Province":
                field = "province";
                break;
        }
        tblCustomers.getItems().clear();
        try {
            ArrayList<CustomerDTO> customers = customerBO.filterCustomers(field, txtSearch.getText());
            for (CustomerDTO customer : customers) {
                tblCustomers.getItems().add(new CustomerTM(customer.getCusID(), customer.getTitle(), customer.getName(), customer.getAddress(), customer.getCity(), customer.getProvince(), customer.getPostalCode()));
            }
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }
}
