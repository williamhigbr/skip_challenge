package br.com.williamhigino.skipchallenge;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ProductModel implements Serializable{

    @SerializedName("id")
    public int id;
    @SerializedName("storeId")
    public int storeId;
    @SerializedName("name")
    public String name;
    @SerializedName("description")
    public String description;
    @SerializedName("price")
    public Double price;

    public String formattedPrice() {
        NumberFormat formatter = new DecimalFormat("$ #,###.##");
        return formatter.format(price);
    }

    public String productShortString() {
        return name + "\n" + formattedPrice();
    }

}
