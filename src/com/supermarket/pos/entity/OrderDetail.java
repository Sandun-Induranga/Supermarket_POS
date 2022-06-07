package com.supermarket.pos.entity;

import java.math.BigDecimal;

public class OrderDetail {
    private String orderID;
    private String itemCode;
    private int qty;
    private BigDecimal discount;
    private BigDecimal unitPrice;

    public OrderDetail(String orderID, String itemCode, int qty, BigDecimal discount, BigDecimal unitPrice) {
        this.orderID = orderID;
        this.itemCode = itemCode;
        this.qty = qty;
        this.discount = discount;
        this.unitPrice = unitPrice;
    }

    public OrderDetail() {
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
