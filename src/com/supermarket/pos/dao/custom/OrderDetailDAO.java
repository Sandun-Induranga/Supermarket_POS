package com.supermarket.pos.dao.custom;

import com.supermarket.pos.dao.CrudDAO;
import com.supermarket.pos.entity.OrderDetail;

import java.sql.SQLException;
import java.util.ArrayList;

public interface OrderDetailDAO extends CrudDAO<OrderDetail, String> {
    String getMonthlyIncome() throws SQLException, ClassNotFoundException;

    String getYearlyIncome() throws SQLException, ClassNotFoundException;

    ArrayList<Double> getChartData() throws SQLException, ClassNotFoundException;
}
