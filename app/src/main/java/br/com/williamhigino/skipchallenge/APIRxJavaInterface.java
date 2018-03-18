package br.com.williamhigino.skipchallenge;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by williamhigino on 11/03/2018.
 */

public interface APIRxJavaInterface {

    @POST("/api/v1/Customer")
    Observable<String> createCustomer(@Body CustomerModel customer);

    @FormUrlEncoded
    @POST("/api/v1/Customer/auth")
    Observable<String> authCustomer(@Field("email") String email, @Field("password") String password);

    @GET("/api/v1/Product")
    Observable<List<ProductModel>> getProducts();

    @GET("/api/v1/Order/customer")
    Observable<List<OrderModel>> getOrders(@Header("Authorization") String authorization);

    @POST("/api/v1/Order")
    Observable<OrderModel> placeOrder(@Header("Authorization") String authorization, @Body OrderModel orderModel);
}
