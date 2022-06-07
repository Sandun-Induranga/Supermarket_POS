package com.supermarket.pos.view.tdm;

public class OrderTM {
    private String orderID;
    private String orderDate;
    private String cusID;
    private String name;

    public OrderTM(String orderID, String orderDate, String cusID, String name) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.cusID = cusID;
        this.name = name;
    }

    public OrderTM() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
