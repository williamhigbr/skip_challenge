package br.com.williamhigino.skipchallenge;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CustomerModel implements Serializable{


    @SerializedName("id")
    public int id;
    @SerializedName("email")
    public String email;
    @SerializedName("name")
    public String name;
    @SerializedName("address")
    public String address;
    @SerializedName("creation")
    public String creation;
    @SerializedName("password")
    public String password;

    public CustomerModel() {
    }

    public CustomerModel(int id, String email, String name, String address, String creation, String password) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.address = address;
        this.creation = creation;
        this.password = password;
    }
}
