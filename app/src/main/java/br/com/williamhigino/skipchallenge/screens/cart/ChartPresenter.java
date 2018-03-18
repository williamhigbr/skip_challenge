package br.com.williamhigino.skipchallenge.screens.cart;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.williamhigino.skipchallenge.screens.login.CustomerModel;
import br.com.williamhigino.skipchallenge.screens.orders.OrderModel;
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

public class ChartPresenter {

    private ChartView view;
    private PersistentDataManager persistentDataManager;
    private APIRxJavaInterface apiInterface;

    public ChartPresenter(ChartView view,
                          APIRxJavaInterface apiInterface,
                          PersistentDataManager persistentDataManager) {
        this.view = view;
        this.apiInterface = apiInterface;
        this.persistentDataManager = persistentDataManager;

    }

    public void prepareOrder() {

        final ChartModel currentChart = persistentDataManager.ReadModel(PersistentDataManager.CURRENT_CHART, ChartModel.class);
        final List<OrderModel> orderModels = OrderModel.getOrdersFromChart(currentChart);

        if(orderModels.size() == 0) {
            view.showChartEmptyDialog();
        }
        else if(orderModels.size() > 1) {
            view.showSeveralRestaurantsDialog(orderModels);
        }
        else {
            placeOrders(orderModels);
        }

    }

    public void placeOrders(List<OrderModel> orderModels) {

        CustomerModel currentCustomer = persistentDataManager.ReadModel(CURRENT_CUSTOMER, CustomerModel.class);
        final String authorization = "Bearer " + currentCustomer.token;
        Observable.fromIterable(orderModels)
                .doOnNext(new Consumer<OrderModel>() {
                    @Override
                    public void accept(OrderModel orderModel) throws Exception {
                        apiInterface.placeOrder(authorization, orderModel)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnError(new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Log.e("TAG", "error: " + throwable, throwable);
                                    }
                                }).subscribe();

                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        view.showOrderPlacedDialog();
                        //clears latest chart info
                        persistentDataManager.SaveModel(new ChartModel(), CURRENT_CHART);
                        view.setItems(new ArrayList<OrderItemModel>());
                    }
                }).subscribe();

    }

    public void manageOrderItemClick(OrderItemModel item) {
        ChartModel currentChart = persistentDataManager.ReadModel(PersistentDataManager.CURRENT_CHART, ChartModel.class);
        currentChart.removeItem(item);
        persistentDataManager.SaveModel(currentChart, PersistentDataManager.CURRENT_CHART);
        loadChartItems();
    }

    public void loadChartItems() {
        ChartModel currentChart = persistentDataManager.ReadModel(PersistentDataManager.CURRENT_CHART, ChartModel.class);
        view.loadChartItems(currentChart);
    }

}
