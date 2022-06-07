package com.supermarket.pos.bo.custom;

import com.supermarket.pos.bo.SuperBO;
import com.supermarket.pos.dto.CustomDTO;
import com.supermarket.pos.dto.ItemDTO;
import com.supermarket.pos.dto.OrderDetailDTO;
import javafx.scene.chart.XYChart;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public interface ManageOrderDetailsBO extends SuperBO {
    ArrayList<CustomDTO> getAllOrders() throws SQLException, ClassNotFoundException;

    ArrayList<String> getAllItemCodes() throws SQLException, ClassNotFoundException;

    ItemDTO getItem(String code) throws SQLException, ClassNotFoundException;

    ArrayList<CustomDTO> getAllOrderDetails(String orderID) throws SQLException, ClassNotFoundException;

    ArrayList<CustomDTO> filterOrders(String field, String value) throws SQLException, ClassNotFoundException;

    Map getMonthlyReportParams() throws SQLException, ClassNotFoundException;

    XYChart.Series setChartDetails() throws SQLException, ClassNotFoundException;

    boolean saveOrderDetail(OrderDetailDTO orderDetailDTO) throws SQLException, ClassNotFoundException;

    boolean updateOrderDetail(OrderDetailDTO orderDetailDTO) throws SQLException, ClassNotFoundException;

    boolean exitOrder(String orderID, String code) throws SQLException, ClassNotFoundException;

    boolean deleteItemInOrder(String orderID, String code) throws SQLException, ClassNotFoundException;
}
