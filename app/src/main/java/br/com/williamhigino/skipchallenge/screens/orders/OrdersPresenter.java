package br.com.williamhigino.skipchallenge.screens.orders;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.williamhigino.skipchallenge.screens.cart.ChartModel;
import br.com.williamhigino.skipchallenge.screens.cart.ChartView;
import br.com.williamhigino.skipchallenge.screens.cart.OrderItemModel;
import br.com.williamhigino.skipchallenge.screens.login.CustomerModel;
import br.com.williamhigino.skipchallenge.util.APIRxJavaInterface;
import br.com.williamhigino.skipchallenge.util.PersistentDataManager;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static br.com.williamhigino.skipchallenge.util.PersistentDataManager.CURRENT_CHART;
import static br.com.williamhigino.skipchallenge.util.PersistentDataManager.CURRENT_CUSTOMER;

/**
 * Created by williamhigino on 18/03/2018.
 */

public class OrdersPresenter {

    private OrdersView view;
    private PersistentDataManager persistentDataManager;
    private APIRxJavaInterface apiInterface;

    public OrdersPresenter(OrdersView view,
                           APIRxJavaInterface apiInterface,
                           PersistentDataManager persistentDataManager) {
        this.view = view;
        this.apiInterface = apiInterface;
        this.persistentDataManager = persistentDataManager;

    }

    public void loadOrders() {

        CustomerModel currentCustomer = persistentDataManager.ReadModel(CURRENT_CUSTOMER, CustomerModel.class);
        String authorization = "Bearer " + currentCustomer.token;
        apiInterface.getOrders(authorization)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("TAG", "error: " + throwable, throwable);
                    }
                })
                .doOnNext(new Consumer<List<OrderModel>>() {
                    @Override
                    public void accept(List<OrderModel> orderModels) throws Exception {
                        view.setItems(orderModels);
                    }
                }).subscribe();


    }

    public void cancelOrder(OrderModel orderModel) {

//        orderModel.status = "canceled";
//
//        CustomerModel currentCustomer = persistentDataManager.ReadModel(CURRENT_CUSTOMER, CustomerModel.class);
//        String authorization = "Bearer " + currentCustomer.token;
//        apiInterface.placeOrder(authorization, orderModel)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnError(new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Log.e("TAG", "error: " + throwable, throwable);
//                    }
//                })
//                .doOnNext(new Consumer<OrderModel>() {
//                    @Override
//                    public void accept(OrderModel orderModel) throws Exception {
//                        view.updateItems();
//                    }
//                }).subscribe();

    }

}
