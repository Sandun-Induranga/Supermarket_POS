package com.supermarket.pos.dao.custom.impl;

import com.supermarket.pos.dao.SQLUtil;
import com.supermarket.pos.dao.custom.QueryDAO;
import com.supermarket.pos.entity.CustomEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class QueryDAOImpl implements QueryDAO {
    @Override
    public ArrayList<CustomEntity> getAllOrders() throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT o.orderID,o.orderDate,o.cusID,c.name FROM `Order` o JOIN Customer c on o.cusID = c.cusID");
        ArrayList<CustomEntity> allOrders = new ArrayList<>();
        while (rst.next()) {
            allOrders.add(new CustomEntity(rst.getString(1), rst.getString(2), rst.getString(3), rst.getString(4)));
        }
        return allOrders;
    }

    @Override
    public ArrayList<CustomEntity> getAllOrderDetails(String orderID) throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT d.itemCode,i.description, i.packSize,d.qty,d.unitPrice,d.discount FROM OrderDetail d JOIN Item i on d.itemCode = i.itemCode WHERE d.orderID=?", orderID);
        ArrayList<CustomEntity> allOrderDetails = new ArrayList<>();
        while (rst.next()) {
            allOrderDetails.add(new CustomEntity(rst.getString(1), rst.getString(2), rst.getString(3), rst.getInt(4), rst.getBigDecimal(5), rst.getBigDecimal(6)));
        }
        return allOrderDetails;
    }

    @Override
    public ArrayList<CustomEntity> filterOrders(String field, String value) throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT o.orderID,o.orderDate,o.cusID,c.name FROM `Order` o JOIN Customer c on o.cusID = c.cusID WHERE " + field + " LIKE '%" + value + "%'");
        ArrayList<CustomEntity> allOrders = new ArrayList<>();
        while (rst.next()) {
            allOrders.add(new CustomEntity(rst.getString(1), rst.getString(2), rst.getString(3), rst.getString(4)));
        }
        return allOrders;
    }

    @Override
    public String getMostSoldItem() throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT I.description, SUM(OD.unitPrice) FROM Item I JOIN OrderDetail OD on I.itemCode = OD.itemCode GROUP BY OD.itemCode ORDER BY SUM(OD.unitPrice) DESC LIMIT 1");
        if (rst.next()) {
            return rst.getString(1);
        }
        return "No Items Sold Yet";
    }

    @Override
    public String getLeastSoldItem() throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT I.description FROM Item I JOIN OrderDetail OD on I.itemCode = OD.itemCode GROUP BY OD.itemCode ORDER BY SUM(OD.unitPrice) ASC LIMIT 1");
        if (rst.next()) {
            return rst.getString(1);
        }
        return "No Items Sold Yet";
    }

    @Override
    public boolean exitItemInOrder(String orderID, String code) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeQuery("SELECT itemCode FROM `OrderDetail` WHERE itemCode=? AND orderID=?", code, orderID).next();
    }

    @Override
    public boolean deleteItemInOrder(String orderID, String code) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("DELETE FROM OrderDetail WHERE orderID=? AND itemCode=?", orderID, code);
    }
}
