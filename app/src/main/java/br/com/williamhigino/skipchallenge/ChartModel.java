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
}
