package com.supermarket.pos.dto;

import java.util.List;

public class OrderDTO {
    private String orderID;
    private String orderDate;
    private String cusID;
    private List<OrderDetailDTO> orderDetails;

    public OrderDTO(String orderID, String orderDate, String cusID, List<OrderDetailDTO> orderDetails) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.cusID = cusID;
        this.orderDetails = orderDetails;
    }

    public OrderDTO() {
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

    public List<OrderDetailDTO> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailDTO> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
