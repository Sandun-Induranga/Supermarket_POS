package com.supermarket.pos.bo;

import com.supermarket.pos.bo.custom.impl.CustomerBOImpl;
import com.supermarket.pos.bo.custom.impl.ItemBOImpl;
import com.supermarket.pos.bo.custom.impl.PurchaseOrderBOImpl;
import com.supermarket.pos.bo.custom.impl.ManageOrderDetailsBOImpl;

public class BOFactory {
    private static BOFactory boFactory;

    private BOFactory() {
    }

    public static BOFactory getInstance() {
        return boFactory == null ? new BOFactory() : boFactory;
    }

    public enum BOTypes {
        CUSTOMER, ITEM, PURCHASE_ORDER, SEARCH_ORDER
    }

    public SuperBO getBO(BOTypes types) {
        switch (types) {
            case CUSTOMER:
                return new CustomerBOImpl();
            case ITEM:
                return new ItemBOImpl();
            case PURCHASE_ORDER:
                return new PurchaseOrderBOImpl();
            case SEARCH_ORDER:
                return new ManageOrderDetailsBOImpl();
            default:
                return null;
        }
    }
}
