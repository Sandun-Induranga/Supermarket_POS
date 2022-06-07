package com.supermarket.pos.dao.custom;

import com.supermarket.pos.dao.SuperDAO;
import com.supermarket.pos.entity.CustomEntity;

import java.sql.SQLException;
import java.util.ArrayList;

public interface QueryDAO extends SuperDAO {
    ArrayList<CustomEntity> getAllOrders() throws SQLException, ClassNotFoundException;

    ArrayList<CustomEntity> getAllOrderDetails(String orderID) throws SQLException, ClassNotFoundException;

    ArrayList<CustomEntity> filterOrders(String field, String value) throws SQLException, ClassNotFoundException;

    String getMostSoldItem() throws SQLException, ClassNotFoundException;

    String getLeastSoldItem() throws SQLException, ClassNotFoundException;

    boolean exitItemInOrder(String orderID, String code) throws SQLException, ClassNotFoundException;

    boolean deleteItemInOrder(String orderID, String code) throws SQLException, ClassNotFoundException;
}
