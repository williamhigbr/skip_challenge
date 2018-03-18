package br.com.williamhigino.skipchallenge;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class OrderItemModel implements Serializable{

    @SerializedName("id")
    public int id;
    @SerializedName("orderId")
    public int orderId;
    @SerializedName("productId")
    public int productId;
    @SerializedName("product")
    public ProductModel product;
    @SerializedName("price")
    public double price;
    @SerializedName("quantity")
    public int quantity;
    @SerializedName("total")
    public double total;

    public OrderItemModel(ProductModel product, int quantity, int orderId) {
        this.product = product;
        this.productId = product.id;
        this.orderId = orderId;
        this.quantity = quantity;
        this.total = quantity * product.price;
    }

    public String formattedPrice() {
        NumberFormat formatter = new DecimalFormat("$ #,###.##");
        return formatter.format(total);
    }


}
