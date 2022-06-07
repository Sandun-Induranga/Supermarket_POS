package com.supermarket.pos.view.tdm;

import java.math.BigDecimal;

public class OrderDetailsTM {
    private String itemCode;
    private String description;
    private String packSize;
    private int qty;
    private BigDecimal discount;
    private BigDecimal unitPrice;
    private BigDecimal total;

    public OrderDetailsTM(String itemCode, String description, String packSize, BigDecimal unitPrice, int qty, BigDecimal discount, BigDecimal total) {
        this.itemCode = itemCode;
        this.description = description;
        this.packSize = packSize;
        this.qty = qty;
        this.discount = discount;
        this.unitPrice = unitPrice;
        this.total = total;
    }

    public OrderDetailsTM() {
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPackSize() {
        return packSize;
    }

    public void setPackSize(String packSize) {
        this.packSize = packSize;
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

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
