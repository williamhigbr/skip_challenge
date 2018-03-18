package br.com.williamhigino.skipchallenge.screens.cart;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

import br.com.williamhigino.skipchallenge.screens.products.ProductModel;

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
    @SerializedName("internalId")
    public int internalId;

    public OrderItemModel(ProductModel product, int quantity) {
        this.internalId = new Random().nextInt() % 100000;
        this.product = product;
        this.productId = product.id;
        this.orderId = product.storeId;
        this.quantity = quantity;
        this.total = quantity * product.price;
    }

    public String formattedPrice() {
        NumberFormat formatter = new DecimalFormat("$ #,###.##");
        return formatter.format(total);
    }


}
