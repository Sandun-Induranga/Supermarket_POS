package com.supermarket.pos.entity;

public class Order {
    private String orderID;
    private String orderDate;
    private String cusID;

    public Order(String orderID, String orderDate, String cusID) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.cusID = cusID;
    }

    public Order() {
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getCusID() {
        return cusID;
    }

    public void setCusID(String cusID) {
        this.cusID = cusID;
    }
}
