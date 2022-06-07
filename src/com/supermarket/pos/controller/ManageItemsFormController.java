package com.supermarket.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.supermarket.pos.bo.BOFactory;
import com.supermarket.pos.bo.custom.ItemBO;
import com.supermarket.pos.dto.ItemDTO;
import com.supermarket.pos.util.ValidationUtil;
import com.supermarket.pos.view.tdm.ItemTM;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

public class ManageItemsFormController {
    public JFXTextField txtCode;
    public JFXTextField txtDescription;
    public JFXTextField txtPackSize;
    public JFXTextField txtUnitPrice;
    public JFXTextField txtQtyOnHand;
    public JFXButton btnSave;
    public JFXButton btnDelete;
    public JFXButton btnAddNewItems;
    public FontAwesomeIconView btnBackToHome;
    public TableView<ItemTM> tblItems;
    public AnchorPane context;
    private final ItemBO itemBO = (ItemBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.ITEM);
    public JFXComboBox<String> cmbSearchFields;
    public TextField txtSearch;
    LinkedHashMap<JFXTextField, Pattern> map = ValidationUtil.getPatternMap();

    public void initialize() {
        tblItems.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblItems.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblItems.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("packSize"));
        tblItems.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblItems.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));

        tblItems.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnDelete.setDisable(newValue == null);
            btnSave.setText(newValue != null ? "Update" : "Save");
            btnSave.setDisable(newValue == null);

            if (newValue != null) {
                txtCode.setText(newValue.getItemCode());
                txtDescription.setText(newValue.getDescription());
                txtPackSize.setText(newValue.getPackSize());
                txtUnitPrice.setText(newValue.getUnitPrice().setScale(2).toString());
                txtQtyOnHand.setText(newValue.getQtyOnHand() + "");

                txtCode.setDisable(false);
                txtDescription.setDisable(false);
                txtPackSize.setDisable(false);
                txtUnitPrice.setDisable(false);
                txtQtyOnHand.setDisable(false);
            }
        });
        cmbSearchFields.getItems().addAll("Item Code", "Description");
        cmbSearchFields.setValue("Item Code");
        initUI();
        loadAllItems();
        setValidations();
    }

    private void setValidations() {
        Pattern codePattern = Pattern.compile("^(ITM)[0-9]{3}$");
        Pattern descriptionPattern = Pattern.compile("^[A-z ]{3,15}$");
        Pattern packSizePattern = Pattern.compile("^[A-z0-9]{2,15}$");
        Pattern qtyPattern = Pattern.compile("^[0-9]{1,6}$");
        Pattern itemPrice = Pattern.compile("^[1-9][0-9]*(.[0-9]{2})?$");

        map.put(txtCode, codePattern);
        map.put(txtDescription, descriptionPattern);
        map.put(txtPackSize, packSizePattern);
        map.put(txtUnitPrice, itemPrice);
        map.put(txtQtyOnHand, qtyPattern);
    }

    private void loadAllItems() {
        tblItems.getItems().clear();
        try {
            /*Get all items*/
            ArrayList<ItemDTO> allItems = itemBO.getAllItems();

            for (ItemDTO item : allItems) {
                tblItems.getItems().add(new ItemTM(item.getItemCode(), item.getDescription(), item.getPackSize(), item.getUnitPrice(), item.getQtyOnHand()));
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void textFieldsKeyReleased(KeyEvent keyEvent) {
        ValidationUtil.validate(map, btnSave);

        if (keyEvent.getCode() == KeyCode.ENTER) {
            Object response = ValidationUtil.validate(map, btnSave);

            if (response instanceof JFXTextField) {
                JFXTextField textField = (JFXTextField) response;
                textField.requestFocus();
            } else if (response instanceof Boolean) {
                saveItem();
            }
        }
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        saveItem();
    }

    private void saveItem() {
        if (btnSave.getText().equalsIgnoreCase("save")) {
            try {
                if (existItem(txtCode.getText())) {
                    new Alert(Alert.AlertType.ERROR, txtCode.getText() + " already exists").show();
                }
                //Save Item
                itemBO.saveItem(new ItemDTO(txtCode.getText(), txtDescription.getText(), txtPackSize.getText(), new BigDecimal(txtUnitPrice.getText()), Integer.parseInt(txtQtyOnHand.getText())));

                tblItems.getItems().add(new ItemTM(txtCode.getText(), txtDescription.getText(), txtPackSize.getText(), new BigDecimal(txtUnitPrice.getText()), Integer.parseInt(txtQtyOnHand.getText())));

            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (!existItem(txtCode.getText())) {
                    new Alert(Alert.AlertType.ERROR, "There is no such item associated with the id " + txtCode.getText()).show();
                }
                /*Update Item*/
                itemBO.updateItem(new ItemDTO(txtCode.getText(), txtDescription.getText(), txtPackSize.getText(), new BigDecimal(txtUnitPrice.getText()), Integer.parseInt(txtQtyOnHand.getText())));

                ItemTM selectedItem = tblItems.getSelectionModel().getSelectedItem();
                selectedItem.setDescription(txtDescription.getText());
                selectedItem.setPackSize(txtPackSize.getText());
                selectedItem.setQtyOnHand(Integer.parseInt(txtQtyOnHand.getText()));
                selectedItem.setUnitPrice(new BigDecimal(txtUnitPrice.getText()));
                tblItems.refresh();
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        btnAddNewItems.fire();
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        /*Delete Item*/
        String code = tblItems.getSelectionModel().getSelectedItem().getItemCode();
        try {
            if (!existItem(code)) {
                new Alert(Alert.AlertType.ERROR, "There is no such item associated with the id " + code).show();
            }

            itemBO.deleteItems(code);

            tblItems.getItems().remove(tblItems.getSelectionModel().getSelectedItem());
            tblItems.getSelectionModel().clearSelection();
            initUI();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to delete the item " + code).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void btnAddNewOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        txtCode.setDisable(false);
        txtDescription.setDisable(false);
        txtPackSize.setDisable(false);
        txtUnitPrice.setDisable(false);
        txtQtyOnHand.setDisable(false);
        txtCode.clear();
        txtCode.setText(generateNewId());
        txtDescription.clear();
        txtPackSize.clear();
        txtUnitPrice.clear();
        txtQtyOnHand.clear();
        txtDescription.requestFocus();
        btnSave.setDisable(false);
        btnSave.setText("Save");
        tblItems.getSelectionModel().clearSelection();
    }

    private String generateNewId() throws SQLException, ClassNotFoundException {
        return itemBO.generateNewItemCode();
    }

    private boolean existItem(String code) throws SQLException, ClassNotFoundException {
        return itemBO.exitsItem(code);
    }

    @FXML
    private void navigateToHome(MouseEvent event) throws IOException {
        URL resource = this.getClass().getResource("/com/supermarket/pos/view/main-form.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) (this.context.getScene().getWindow());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        Platform.runLater(primaryStage::sizeToScene);
    }

    private void initUI() {
        txtCode.clear();
        txtDescription.clear();
        txtPackSize.clear();
        txtUnitPrice.clear();
        txtQtyOnHand.clear();
        txtCode.setDisable(true);
        txtDescription.setDisable(true);
        txtPackSize.setDisable(true);
        txtUnitPrice.setDisable(true);
        txtQtyOnHand.setDisable(true);
        txtCode.setEditable(false);
        btnSave.setDisable(true);
        btnDelete.setDisable(true);
    }

    public void searchOnAction(KeyEvent keyEvent) {
        String field = cmbSearchFields.getValue() == "Item Code" ? "itemCode" : "description";
        tblItems.getItems().clear();
        try {
            ArrayList<ItemDTO> items = itemBO.filterItems(field, txtSearch.getText());
            for (ItemDTO item : items) {
                tblItems.getItems().add(new ItemTM(item.getItemCode(), item.getDescription(), item.getPackSize(), item.getUnitPrice(), item.getQtyOnHand()));
            }
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }
}
