package com.supermarket.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.supermarket.pos.bo.BOFactory;
import com.supermarket.pos.bo.custom.PurchaseOrderBO;
import com.supermarket.pos.dto.CustomerDTO;
import com.supermarket.pos.dto.ItemDTO;
import com.supermarket.pos.dto.OrderDTO;
import com.supermarket.pos.dto.OrderDetailDTO;
import com.supermarket.pos.view.tdm.OrderDetailsTM;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PlaceOrderFormController {
    public JFXComboBox<String> cmbCusID;
    public JFXComboBox<String> cmbItemCode;
    public JFXTextField txtTitle;
    public JFXTextField txtName;
    public JFXTextField txtAddress;
    public JFXTextField txtCity;
    public JFXTextField txtDescription;
    public JFXTextField txtPackSize;
    public JFXTextField txtUnitPrice;
    public JFXTextField txtQtyOnHand;
    public JFXTextField txtQty;
    public JFXTextField txtDiscount;
    public JFXButton btnAddToCart;
    public TableView<OrderDetailsTM> tblOrderDetails;
    public JFXButton btnPlace;
    public Label lblTotal;
    public Label lblId;
    public Label lblDate;
    public AnchorPane context;
    private final PurchaseOrderBO purchaseOrderBO = (PurchaseOrderBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.PURCHASE_ORDER);
    private String orderId;

    public void initialize() throws SQLException, ClassNotFoundException {
        tblOrderDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblOrderDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblOrderDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("packSize"));
        tblOrderDetails.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblOrderDetails.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("qty"));
        tblOrderDetails.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("discount"));
        tblOrderDetails.getColumns().get(6).setCellValueFactory(new PropertyValueFactory<>("total"));
        tblOrderDetails.getColumns().get(7).setCellValueFactory(param -> {
            Button btnDelete = new Button("Delete");

            btnDelete.setOnAction(event -> {
                tblOrderDetails.getItems().remove(param.getValue());
                tblOrderDetails.getSelectionModel().clearSelection();
                calculateTotal();
                enableOrDisablePlaceOrderButton();
            });
            return new ReadOnlyObjectWrapper(btnDelete);
        });
        orderId = generateNewOrderId();
        lblId.setText(orderId);
        lblDate.setText(LocalDate.now().toString());
        btnPlace.setDisable(true);
        txtTitle.setFocusTraversable(false);
        txtTitle.setEditable(false);
        txtName.setFocusTraversable(false);
        txtName.setEditable(false);
        txtAddress.setFocusTraversable(false);
        txtAddress.setEditable(false);
        txtCity.setFocusTraversable(false);
        txtCity.setEditable(false);
        txtDescription.setFocusTraversable(false);
        txtDescription.setEditable(false);
        txtUnitPrice.setFocusTraversable(false);
        txtUnitPrice.setEditable(false);
        txtPackSize.setFocusTraversable(false);
        txtPackSize.setEditable(false);
        txtQtyOnHand.setFocusTraversable(false);
        txtQtyOnHand.setEditable(false);
        txtDiscount.setText("0.00");
        txtDiscount.setFocusTraversable(false);
        txtDiscount.setEditable(false);
        txtQty.setOnAction(event -> btnAddToCart.fire());
        txtQty.setEditable(false);
        btnAddToCart.setDisable(true);

        cmbCusID.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            enableOrDisablePlaceOrderButton();

            if (newValue != null) {
                /*Search Customer*/
                try {
                    if (!existCustomer(newValue + "")) {
                        new Alert(Alert.AlertType.ERROR, "There is no such customer associated with the id " + newValue + "").show();
                    }
                    CustomerDTO search = purchaseOrderBO.searchCustomer(newValue + "");
                    txtTitle.setText(search.getTitle());
                    txtName.setText(search.getName());
                    txtAddress.setText(search.getAddress());
                    txtCity.setText(search.getCity());

                } catch (SQLException | ClassNotFoundException e) {
                    new Alert(Alert.AlertType.ERROR, "Failed to find the customer " + newValue + "" + e).show();
                }

            } else {
                txtTitle.clear();
                txtName.clear();
                txtAddress.clear();
                txtCity.clear();
            }
        });
        cmbItemCode.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newItemCode) -> {
            txtQty.setEditable(newItemCode != null);
            btnAddToCart.setDisable(newItemCode == null);

            if (newItemCode != null) {
                /*Find Item*/
                try {
                    //Search Item
                    if (!existItem(newItemCode + "")) {
                        new Alert(Alert.AlertType.ERROR, "There is no such customer associated with the id " + newItemCode).show();
                    }
                    ItemDTO item = purchaseOrderBO.searchItem(newItemCode);

                    txtDescription.setText(item.getDescription());
                    txtPackSize.setText(item.getPackSize());
                    txtUnitPrice.setText(item.getUnitPrice().setScale(2).toString());
                    txtDiscount.setEditable(true);

//                    txtQtyOnHand.setText(tblOrderDetails.getItems().stream().filter(detail-> detail.getItemCode().equals(item.getItemCode())).<Integer>map(detail-> item.getQtyOnHand() - detail.getQty()).findFirst().orElse(item.getQtyOnHand()) + "");
                    Optional<OrderDetailsTM> optOrderDetail = tblOrderDetails.getItems().stream().filter(detail -> detail.getItemCode().equals(newItemCode)).findFirst();
                    txtQtyOnHand.setText((optOrderDetail.isPresent() ? item.getQtyOnHand() - optOrderDetail.get().getQty() : item.getQtyOnHand()) + "");

                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

            } else {
                txtDescription.clear();
                txtQty.clear();
                txtQtyOnHand.clear();
                txtUnitPrice.clear();
            }
        });
        loadAllCustomerIds();
        loadAllItemCodes();
    }

    private void loadAllCustomerIds() {
        try {
            ArrayList<CustomerDTO> all = purchaseOrderBO.getAllCustomers();
            for (CustomerDTO customerDTO : all) {
                cmbCusID.getItems().add(customerDTO.getCusID());
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load customer ids").show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadAllItemCodes() {
        try {
            /*Get all items*/
            ArrayList<ItemDTO> all = purchaseOrderBO.getAllItems();
            for (ItemDTO dto : all) {
                cmbItemCode.getItems().add(dto.getItemCode());
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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

    public void btnAddOnAction(ActionEvent actionEvent) {
        if (!txtQty.getText().matches("\\d+") || Integer.parseInt(txtQty.getText()) <= 0 ||
                Integer.parseInt(txtQty.getText()) > Integer.parseInt(txtQtyOnHand.getText())) {
            new Alert(Alert.AlertType.ERROR, "Invalid qty").show();
            txtQty.requestFocus();
            txtQty.selectAll();
            return;
        }
        if (!txtDiscount.getText().matches("^[0-9][0-9]*(.[0-9]{2})?$")) {
            new Alert(Alert.AlertType.ERROR, "Invalid Discount").show();
            txtDiscount.requestFocus();
            txtDiscount.selectAll();
            return;
        }

        boolean exists = tblOrderDetails.getItems().stream().anyMatch(detail -> detail.getItemCode().equals(cmbItemCode.getSelectionModel().getSelectedItem()));

        if (exists) {
            OrderDetailsTM orderDetailTM = tblOrderDetails.getItems().stream().filter(detail -> detail.getItemCode().equals(cmbItemCode.getSelectionModel().getSelectedItem())).findFirst().get();

            if (btnAddToCart.getText().equalsIgnoreCase("Update")) {
                orderDetailTM.setQty(Integer.parseInt(txtQty.getText()));
                orderDetailTM.setTotal((new BigDecimal(txtUnitPrice.getText()).multiply(new BigDecimal(txtQty.getText())).subtract(new BigDecimal(txtDiscount.getText())).setScale(2)));
                tblOrderDetails.getSelectionModel().clearSelection();
            } else {
                orderDetailTM.setQty(orderDetailTM.getQty() + Integer.parseInt(txtQty.getText()));
                orderDetailTM.setTotal(new BigDecimal(new BigDecimal(txtUnitPrice.getText()).setScale(2).multiply(new BigDecimal(orderDetailTM.getQty())).subtract(new BigDecimal(txtDiscount.getText()).setScale(2).setScale(2)).toString()));
                orderDetailTM.setDiscount(new BigDecimal(txtDiscount.getText()));
            }
            tblOrderDetails.refresh();
        } else {

            tblOrderDetails.getItems().add(new OrderDetailsTM(cmbItemCode.getSelectionModel().getSelectedItem(), txtDescription.getText(), txtPackSize.getText(), new BigDecimal(txtUnitPrice.getText()).setScale(2), Integer.parseInt(txtQty.getText()), new BigDecimal(txtDiscount.getText()).setScale(2), new BigDecimal(String.valueOf(new BigDecimal(txtUnitPrice.getText()).multiply(new BigDecimal(txtQty.getText())).subtract(new BigDecimal(txtDiscount.getText()).setScale(2))))));
        }
        cmbItemCode.getSelectionModel().clearSelection();
        cmbItemCode.requestFocus();
        txtPackSize.clear();
        txtDiscount.clear();
        calculateTotal();
        enableOrDisablePlaceOrderButton();
    }

    public void btnPlaceOrderOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        boolean b = saveOrder(orderId, LocalDate.now(), cmbCusID.getValue(),
                tblOrderDetails.getItems().stream().map(tm -> new OrderDetailDTO(orderId, tm.getItemCode(), tm.getQty(), tm.getDiscount(), tm.getUnitPrice())).collect(Collectors.toList()));
        if (b) {
            new Alert(Alert.AlertType.INFORMATION, "Order has been placed successfully").show();
        } else {
            new Alert(Alert.AlertType.ERROR, "Order has not been placed successfully").show();
        }

        orderId = generateNewOrderId();
        lblId.setText("Order Id: " + orderId);
        cmbCusID.getSelectionModel().clearSelection();
        cmbItemCode.getSelectionModel().clearSelection();
        tblOrderDetails.getItems().clear();
        txtQty.clear();
        txtDiscount.clear();
        txtPackSize.clear();
        calculateTotal();
    }

    public boolean saveOrder(String orderId, LocalDate orderDate, String customerId, List<OrderDetailDTO> orderDetails) {
        try {
//            return purchaseOrderBO.purchaseOrder(orderId, orderDate, customerId, orderDetails);
            return purchaseOrderBO.purchaseOrder(new OrderDTO(orderId, orderDate.toString(), customerId, orderDetails));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean existCustomer(String id) throws SQLException, ClassNotFoundException {
        return purchaseOrderBO.checkCustomerIsAvailable(id);
    }

    private boolean existItem(String code) throws SQLException, ClassNotFoundException {
        return purchaseOrderBO.checkItemIsAvailable(code);
    }

    private String generateNewOrderId() throws SQLException, ClassNotFoundException {
        return purchaseOrderBO.generateNewOrderID();
    }

    private void enableOrDisablePlaceOrderButton() {
        btnPlace.setDisable(!(cmbCusID.getSelectionModel().getSelectedItem() != null && !tblOrderDetails.getItems().isEmpty()));
    }

    private void calculateTotal() {
        BigDecimal total = new BigDecimal(0);

        for (OrderDetailsTM detail : tblOrderDetails.getItems()) {
            total = total.add(detail.getTotal());
        }
        lblTotal.setText(String.valueOf(total));
    }
}
