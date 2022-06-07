package com.supermarket.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.supermarket.pos.bo.BOFactory;
import com.supermarket.pos.bo.custom.ManageOrderDetailsBO;
import com.supermarket.pos.db.DBConnection;
import com.supermarket.pos.dto.CustomDTO;
import com.supermarket.pos.dto.ItemDTO;
import com.supermarket.pos.dto.OrderDetailDTO;
import com.supermarket.pos.util.ValidationUtil;
import com.supermarket.pos.view.tdm.OrderDetailsTM;
import com.supermarket.pos.view.tdm.OrderTM;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ManageOrderDetailsFormController {
    public TableView<OrderTM> tblOrders;
    public TableView<OrderDetailsTM> tblOrderDetails;
    public TextField txtSearch;
    public JFXComboBox<String> cmbField;
    public BarChart chartIncome;
    public AnchorPane context;
    public JFXTextField txtDescription;
    public JFXTextField txtPackSize;
    public JFXTextField txtQty;
    public JFXTextField txtUnitPrice;
    public JFXTextField txtDiscount;
    public JFXButton btnSave;
    public JFXButton btnDelete;
    public Label txtOrderID;
    public JFXButton btnAddNew;
    public JFXComboBox<String> cmbItemCode;
    public JFXButton btnPrintBill;
    public Tab tabReports;
    BigDecimal total = BigDecimal.valueOf(0);
    String cusName;
    LinkedHashMap<JFXTextField, Pattern> map = ValidationUtil.getPatternMap();
    ManageOrderDetailsBO manageOrderDetailsBO = (ManageOrderDetailsBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.SEARCH_ORDER);

    public void initialize() {
        tblOrders.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("orderID"));
        tblOrders.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        tblOrders.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("CusID"));
        tblOrders.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("name"));

        tblOrderDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblOrderDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblOrderDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("packSize"));
        tblOrderDetails.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblOrderDetails.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("qty"));
        tblOrderDetails.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("discount"));

        tblOrders.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setOrdersData(newValue);
        });

        tblOrderDetails.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setOrderDetailsData(newValue);
        });

        cmbField.getItems().addAll("Order ID", "Date", "Customer ID");
        cmbField.setValue("Order ID");
        try {
            cmbItemCode.getItems().addAll(manageOrderDetailsBO.getAllItemCodes());
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }

        cmbItemCode.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            txtQty.clear();
            txtUnitPrice.clear();
            txtDiscount.clear();
            try {
                if (newValue != null) {
                    ItemDTO itemDTO = manageOrderDetailsBO.getItem(cmbItemCode.getValue());
                    txtDescription.setText(itemDTO.getDescription());
                    txtPackSize.setText(itemDTO.getPackSize());
                }
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }
        });

        if (!LoginFormController.isAdmin) {
            tabReports.setDisable(true);
        }

        btnPrintBill.setDisable(true);
        setValidations();
        loadAllOrders();
        loadChart();
    }

    private void setValidations() {
        Pattern qtyPattern = Pattern.compile("^[0-9]{1,6}$");
        Pattern itemPrice = Pattern.compile("^[1-9][0-9]*(.[0-9]{2})?$");
        Pattern discount = Pattern.compile("^[1-9][0-9]*(.[0-9]{2})?$");

        map.put(txtUnitPrice, itemPrice);
        map.put(txtQty, qtyPattern);
        map.put(txtDiscount, discount);
    }

    public void textFieldsKeyReleased(KeyEvent keyEvent) {
        ValidationUtil.validate(map, btnSave);

        if (keyEvent.getCode() == KeyCode.ENTER) {
            Object response = ValidationUtil.validate(map, btnSave);

            if (response instanceof JFXTextField) {
                JFXTextField textField = (JFXTextField) response;
                textField.requestFocus();
            } else if (response instanceof Boolean) {
                saveOrderDetails();
            }
        }
    }

    private void loadChart() {
        try {
            chartIncome.getData().setAll(manageOrderDetailsBO.setChartDetails());
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    private void setOrdersData(OrderTM newValue) {
        if (newValue != null) {
            btnPrintBill.setDisable(false);
            cusName = newValue.getName();
            tblOrderDetails.getItems().clear();
            try {
                txtOrderID.setText(newValue.getOrderID());
                ArrayList<CustomDTO> allOrders = manageOrderDetailsBO.getAllOrderDetails(newValue.getOrderID());
                for (CustomDTO order :
                        allOrders) {
                    tblOrderDetails.getItems().add(new OrderDetailsTM(order.getItemCode(), order.getDescription(), order.getPackSize(), order.getUnitPrice(), order.getQty(), order.getDiscount(), null));
                    total = total.add(order.getUnitPrice().multiply(new BigDecimal(order.getQty()))).subtract(order.getDiscount());
                }
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    private void setOrderDetailsData(OrderDetailsTM newValue) {
        if (newValue != null) {
            btnSave.setText("Update");
            cmbItemCode.setValue(newValue.getItemCode());
            txtDescription.setText(newValue.getDescription());
            txtPackSize.setText(newValue.getPackSize());
            txtQty.setText(String.valueOf(newValue.getQty()));
            txtUnitPrice.setText(String.valueOf(newValue.getUnitPrice()));
            txtDiscount.setText(String.valueOf(newValue.getDiscount()));
        }
    }

    private void loadAllOrders() {
        tblOrders.getItems().clear();
        try {
            ArrayList<CustomDTO> allOrders = manageOrderDetailsBO.getAllOrders();
            for (CustomDTO order :
                    allOrders) {
                tblOrders.getItems().add(new OrderTM(order.getOrderID(), order.getOrderDate(), order.getCusID(), order.getName()));
            }
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    public void txtSearchOnAction(KeyEvent keyEvent) {
        tblOrders.getItems().clear();
        String field = cmbField.getValue();
        switch (field) {
            case "Order ID":
                field = "orderID";
                break;
            case "Date":
                field = "orderDate";
                break;
            case "Customer ID":
                field = "c.cusID";
                break;
            default:
                field = null;
        }
        try {
            ArrayList<CustomDTO> allOrders = manageOrderDetailsBO.filterOrders(field, txtSearch.getText());
            for (CustomDTO order :
                    allOrders) {
                tblOrders.getItems().add(new OrderTM(order.getOrderID(), order.getOrderDate(), order.getCusID(), order.getName()));
            }
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    public void btnViewMonthlyReportOnAction(ActionEvent actionEvent) {
        try {
            JasperReport compileReport = (JasperReport) JRLoader.loadObject(this.getClass().getResource("../view/report/monthly-income-report-form.jasper"));

            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, manageOrderDetailsBO.getMonthlyReportParams(), new JREmptyDataSource(1));
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void btnBackOnAction(MouseEvent mouseEvent) throws IOException {
        URL resource = this.getClass().getResource("/com/supermarket/pos/view/main-form.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) (this.context.getScene().getWindow());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        Platform.runLater(primaryStage::sizeToScene);
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        saveOrderDetails();
    }

    private void saveOrderDetails() {
        if (btnSave.getText().equalsIgnoreCase("save")) {
            /*Save Order Details*/
            try {
                if (existOrderItem(txtOrderID.getText(), cmbItemCode.getValue())) {
                    new Alert(Alert.AlertType.ERROR, cmbItemCode.getValue() + " already exists").show();
                }
                manageOrderDetailsBO.saveOrderDetail(new OrderDetailDTO(txtOrderID.getText(), cmbItemCode.getValue(), Integer.parseInt(txtQty.getText()), new BigDecimal(txtDiscount.getText()), new BigDecimal(txtUnitPrice.getText())));
                tblOrders.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    setOrdersData(newValue);
                });
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Failed to save the customer " + e.getMessage()).show();
                e.printStackTrace();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            /*Update Order Details*/
            try {
                if (!existOrderItem(txtOrderID.getText(), cmbItemCode.getValue())) {
                    new Alert(Alert.AlertType.ERROR, "There is no such customer associated with the id " + txtOrderID.getText()).show();
                }
                manageOrderDetailsBO.updateOrderDetail(new OrderDetailDTO(txtOrderID.getText(), cmbItemCode.getValue(), Integer.parseInt(txtQty.getText()), new BigDecimal(txtDiscount.getText()), new BigDecimal(txtUnitPrice.getText())));

            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Failed to update the customer " + txtOrderID.getText() + e.getMessage()).show();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        try {
            tblOrderDetails.getItems().clear();
            ArrayList<CustomDTO> allOrders = manageOrderDetailsBO.getAllOrderDetails(txtOrderID.getText());
            for (CustomDTO order :
                    allOrders) {
                tblOrderDetails.getItems().add(new OrderDetailsTM(order.getItemCode(), order.getDescription(), order.getPackSize(), order.getUnitPrice(), order.getQty(), order.getDiscount(), null));
            }
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }

        tblOrderDetails.refresh();
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        try {
            if (manageOrderDetailsBO.deleteItemInOrder(txtOrderID.getText(), cmbItemCode.getValue())) {
                new Alert(Alert.AlertType.INFORMATION, "Deleted").show();
                tblOrders.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    setOrdersData(newValue);
                });
            }
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    private boolean existOrderItem(String orderID, String code) throws SQLException, ClassNotFoundException {
        return manageOrderDetailsBO.exitOrder(orderID, code);
    }

    public void btnAddNewOnAction(ActionEvent actionEvent) {
        cmbItemCode.setDisable(false);
        txtDescription.setDisable(false);
        txtPackSize.setDisable(false);
        txtUnitPrice.setDisable(false);
        txtQty.setDisable(false);
        cmbItemCode.getSelectionModel().clearSelection();
        txtDescription.clear();
        txtPackSize.clear();
        txtUnitPrice.clear();
        txtQty.clear();
        txtDiscount.clear();
        btnSave.setDisable(false);
        btnSave.setText("Save");
        tblOrderDetails.getSelectionModel().clearSelection();
    }

    public void btnViewMonthReportOnAction(ActionEvent actionEvent) {
        try {
            JasperReport compileReport = (JasperReport) JRLoader.loadObject(this.getClass().getResource("../view/report/monthly-sales.jasper"));

            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, null, DBConnection.getDbConnection().getConnection());
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void btnViewDayReportOnAction(ActionEvent actionEvent) {
        try {
            JasperReport compileReport = (JasperReport) JRLoader.loadObject(this.getClass().getResource("../view/report/today-sales-report.jasper"));

            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, null, DBConnection.getDbConnection().getConnection());
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void btnViewYearReportOnAction(ActionEvent actionEvent) {
        try {
            JasperReport compileReport = (JasperReport) JRLoader.loadObject(this.getClass().getResource("../view/report/year-sales-report.jasper"));

            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, null, DBConnection.getDbConnection().getConnection());
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void btnViewAllReportOnAction(ActionEvent actionEvent) {
        try {
            JasperReport compileReport = (JasperReport) JRLoader.loadObject(this.getClass().getResource("../view/report/all-sales-report.jasper"));

            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, null, DBConnection.getDbConnection().getConnection());
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void btnPrintOnAction(ActionEvent actionEvent) {
        Map map = new HashMap();
        map.put("orderId", txtOrderID.getText());
        map.put("name", cusName);
        map.put("total", String.valueOf(total));
        total = BigDecimal.valueOf(0);
        try {
            JasperReport compileReport = (JasperReport) JRLoader.loadObject(this.getClass().getResource("../view/report/bill-report.jasper"));

            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, map, DBConnection.getDbConnection().getConnection());
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
