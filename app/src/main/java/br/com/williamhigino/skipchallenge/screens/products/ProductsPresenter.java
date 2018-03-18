package br.com.williamhigino.skipchallenge.screens.products;

import android.util.Log;

import java.util.List;

import br.com.williamhigino.skipchallenge.screens.cart.ChartModel;
import br.com.williamhigino.skipchallenge.screens.cart.OrderItemModel;
import br.com.williamhigino.skipchallenge.util.APIRxJavaInterface;
import br.com.williamhigino.skipchallenge.util.PersistentDataManager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by williamhigino on 18/03/2018.
 */

public class ProductsPresenter {

    private ProductsView view;
    private PersistentDataManager persistentDataManager;
    private APIRxJavaInterface apiInterface;

    public ProductsPresenter(ProductsView view,
                             APIRxJavaInterface apiInterface,
                             PersistentDataManager persistentDataManager) {
        this.view = view;
        this.apiInterface = apiInterface;
        this.persistentDataManager = persistentDataManager;

    }

    public void getProducts() {

        apiInterface.getProducts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("TAG", "error: " + throwable, throwable);
                    }
                })
                .doOnNext(new Consumer<List<ProductModel>>() {
                    @Override
                    public void accept(List<ProductModel> productModels) throws Exception {
                        view.addItems(productModels);
                    }
                }).subscribe();

    }

    public void manageProductClick(ProductModel item) {
        //saves current chart in prefs
        ChartModel currentChart = persistentDataManager.ReadModel(PersistentDataManager.CURRENT_CHART, ChartModel.class);
        currentChart.orderItems.add(new OrderItemModel(item, 1));
        persistentDataManager.SaveModel(currentChart, PersistentDataManager.CURRENT_CHART);
    }

}
