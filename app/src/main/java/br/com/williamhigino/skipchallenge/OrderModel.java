package br.com.williamhigino.skipchallenge;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderModel implements Serializable{

    @SerializedName("id")
    public int id;
    @SerializedName("date")
    public String date;
    @SerializedName("customerId")
    public int customerId;
    @SerializedName("deliveryAddress")
    public String deliveryAddress;
    @SerializedName("contact")
    public String contact;
    @SerializedName("storeId")
    public int storeId;
    @SerializedName("orderItems")
    public List<OrderItemModel> orderItems;
    @SerializedName("total")
    public double total;
    @SerializedName("status")
    public String status;
    @SerializedName("lastUpdate")
    public String lastUpdate;

    public String formattedPrice() {
        NumberFormat formatter = new DecimalFormat("$ #,###.##");
        return formatter.format(total);
    }

    public static List<OrderModel> getOrdersFromChart(ChartModel chart) {

        HashMap<Integer, OrderModel> orderModelHashMap = new HashMap<>();
        //goest through chart items and creates one order for each restaurant
        for (OrderItemModel orderItem: chart.orderItems) {
            int storeId = orderItem.product.storeId;
            OrderModel storeOrderModel;
            if(!orderModelHashMap.containsKey(storeId)) {
                storeOrderModel = new OrderModel();
                storeOrderModel.deliveryAddress = "TESTE";
                storeOrderModel.contact = "TESTE";
                storeOrderModel.storeId = storeId;
                storeOrderModel.status = "waiting";
                storeOrderModel.orderItems = new ArrayList<>();
                orderModelHashMap.put(storeId, storeOrderModel);
            }
            //puts specific information
            storeOrderModel = orderModelHashMap.get(storeId);
            storeOrderModel.orderItems.add(orderItem);
            storeOrderModel.total += orderItem.total;
        }

        List<OrderModel> orderModels = new ArrayList<>(orderModelHashMap.values());
        return orderModels;
    }

}