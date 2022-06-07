package com.supermarket.pos.dao.custom.impl;

import com.supermarket.pos.dao.SQLUtil;
import com.supermarket.pos.dao.custom.OrderDetailDAO;
import com.supermarket.pos.entity.OrderDetail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderDetailDAOImpl implements OrderDetailDAO {
    @Override
    public ArrayList<OrderDetail> getAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean save(OrderDetail dto) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("INSERT INTO OrderDetail VALUES (?,?,?,?,?)", dto.getOrderID(), dto.getItemCode(), dto.getQty(), dto.getDiscount(), dto.getUnitPrice());
    }

    @Override
    public boolean update(OrderDetail dto) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("UPDATE OrderDetail SET orderID=?, itemCode=?, qty=?, discount=?, unitPrice=? WHERE orderID=? AND itemCode=?", dto.getOrderID(), dto.getItemCode(), dto.getQty(), dto.getDiscount(), dto.getUnitPrice(), dto.getOrderID(), dto.getItemCode());
    }

    @Override
    public OrderDetail search(String s) throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean exist(String id) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean delete(String s) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public String generateNewID() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public String getMonthlyIncome() throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT SUM(od.unitPrice) FROM OrderDetail od JOIN `Order` O on od.orderID = O.orderID WHERE MONTH(O.orderDate)=MONTH(now()) AND YEAR(O.orderDate)=YEAR(now())");
        if (rst.next()) {
            return rst.getString(1);
        }
        return "No Businesses Yet";
    }

    @Override
    public String getYearlyIncome() throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT SUM(od.unitPrice) FROM OrderDetail od JOIN `Order` O on od.orderID = O.orderID WHERE YEAR(O.orderDate)=YEAR(now())");
        if (rst.next()) {
            return rst.getString(1);
        }
        return "No Businesses Yet";
    }

    @Override
    public ArrayList<Double> getChartData() throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT SUM(od.unitPrice) FROM OrderDetail od JOIN `Order` O on od.orderID = O.orderID GROUP BY MONTH(O.orderDate) ORDER BY MONTH(O.orderDate)");
        ArrayList<Double> data = new ArrayList<>();
        while (rst.next()) {
            data.add(rst.getDouble(1));
        }
        return data;
    }
}
