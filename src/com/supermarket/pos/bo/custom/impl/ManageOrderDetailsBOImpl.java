package com.supermarket.pos.bo.custom.impl;

import com.supermarket.pos.bo.SuperBO;
import com.supermarket.pos.bo.custom.ManageOrderDetailsBO;
import com.supermarket.pos.dao.DAOFactory;
import com.supermarket.pos.dao.custom.ItemDAO;
import com.supermarket.pos.dao.custom.OrderDAO;
import com.supermarket.pos.dao.custom.OrderDetailDAO;
import com.supermarket.pos.dao.custom.QueryDAO;
import com.supermarket.pos.dto.CustomDTO;
import com.supermarket.pos.dto.ItemDTO;
import com.supermarket.pos.dto.OrderDetailDTO;
import com.supermarket.pos.entity.CustomEntity;
import com.supermarket.pos.entity.Item;
import com.supermarket.pos.entity.OrderDetail;
import javafx.scene.chart.XYChart;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManageOrderDetailsBOImpl implements ManageOrderDetailsBO, SuperBO {
    private final QueryDAO queryDAO = (QueryDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.QUERY_DAO);
    private final OrderDetailDAO orderDetailDAO = (OrderDetailDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.ORDER_DETAILS);
    private final ItemDAO itemDAO = (ItemDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.ITEM);
    private final OrderDAO orderDAO = (OrderDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.ORDER);

    @Override
    public ArrayList<CustomDTO> getAllOrders() throws SQLException, ClassNotFoundException {
        ArrayList<CustomEntity> all = queryDAO.getAllOrders();
        ArrayList<CustomDTO> allOrders = new ArrayList<>();
        for (CustomEntity ent :
                all) {
            allOrders.add(new CustomDTO(ent.getOrderID(), ent.getOrderDate(), ent.getCusID(), ent.getName()));
        }
        return allOrders;
    }

    @Override
    public ArrayList<String> getAllItemCodes() throws SQLException, ClassNotFoundException {
        ArrayList<Item> items = itemDAO.getAll();
        ArrayList<String> codes = new ArrayList<>();
        for (Item item :
                items) {
            codes.add(item.getItemCode());
        }
        return codes;
    }

    @Override
    public ItemDTO getItem(String code) throws SQLException, ClassNotFoundException {
        Item item = itemDAO.search(code);
        if (code!=null){
            return new ItemDTO(item.getItemCode(), item.getDescription(), item.getPackSize(), item.getUnitPrice(), item.getQtyOnHand());
        }
        return null;
    }

    @Override
    public ArrayList<CustomDTO> getAllOrderDetails(String orderID) throws SQLException, ClassNotFoundException {
        ArrayList<CustomEntity> all = queryDAO.getAllOrderDetails(orderID);
        ArrayList<CustomDTO> allOrderDetails = new ArrayList<>();
        for (CustomEntity ent :
                all) {
            allOrderDetails.add(new CustomDTO(ent.getItemCode(), ent.getDescription(), ent.getPackSize(), ent.getQty(), ent.getUnitPrice(), ent.getDiscount()));
        }
        return allOrderDetails;
    }

    @Override
    public ArrayList<CustomDTO> filterOrders(String field, String value) throws SQLException, ClassNotFoundException {
        ArrayList<CustomEntity> all = queryDAO.filterOrders(field, value);
        ArrayList<CustomDTO> allOrders = new ArrayList<>();
        for (CustomEntity ent :
                all) {
            allOrders.add(new CustomDTO(ent.getOrderID(), ent.getOrderDate(), ent.getCusID(), ent.getName()));
        }
        return allOrders;
    }

    @Override
    public Map getMonthlyReportParams() throws SQLException, ClassNotFoundException {
        HashMap paramMap = new HashMap();
        paramMap.put("total", orderDetailDAO.getMonthlyIncome() + " RS");
        paramMap.put("yearTotal", orderDetailDAO.getYearlyIncome() + " RS");
        paramMap.put("mostSell", queryDAO.getMostSoldItem());
        paramMap.put("leastSell", queryDAO.getLeastSoldItem());

        return paramMap;
    }

    @Override
    public XYChart.Series setChartDetails() throws SQLException, ClassNotFoundException {
        XYChart.Series series = new XYChart.Series();
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        try {
            ArrayList<Double> incomes = orderDetailDAO.getChartData();
            while (incomes.size() < 12) {
                incomes.add(0.0);
            }
            int count = 0;
            for (Double d :
                    incomes) {
                series.getData().add(new XYChart.Data(months[count++], d));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return series;
    }

    @Override
    public boolean saveOrderDetail(OrderDetailDTO detailDTO) throws SQLException, ClassNotFoundException {
        return orderDetailDAO.save(new OrderDetail(detailDTO.getOrderID(), detailDTO.getItemCode(), detailDTO.getQty(), detailDTO.getDiscount(), detailDTO.getUnitPrice()));
    }

    @Override
    public boolean updateOrderDetail(OrderDetailDTO detailDTO) throws SQLException, ClassNotFoundException {
        return orderDetailDAO.update(new OrderDetail(detailDTO.getOrderID(), detailDTO.getItemCode(), detailDTO.getQty(), detailDTO.getDiscount(), detailDTO.getUnitPrice()));
    }

    @Override
    public boolean exitOrder(String orderID, String code) throws SQLException, ClassNotFoundException {
        return queryDAO.exitItemInOrder(orderID, code);
    }

    @Override
    public boolean deleteItemInOrder(String orderID, String code) throws SQLException, ClassNotFoundException {
        return queryDAO.deleteItemInOrder(orderID, code);
    }
}
