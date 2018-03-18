package br.com.williamhigino.skipchallenge;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChartModel implements Serializable{

    @SerializedName("products")
    public List<OrderItemModel> orderItems;

    public ChartModel() {
        orderItems = new ArrayList<>();
    }

    public void removeItem(OrderItemModel orderItemModel) {

        //searches for order item
        int idxFound = -1;
        for (int idx = 0; idx < orderItems.size(); idx++) {
            OrderItemModel chartItem = orderItems.get(idx);
            if(chartItem.internalId == orderItemModel.internalId) {
                idxFound = idx;
            }
        }
        //deletes it if found
        if(idxFound >= 0) {
            orderItems.remove(idxFound);
        }

    }
}
