package br.com.williamhigino.skipchallenge.screens.cart;

import java.util.List;

import br.com.williamhigino.skipchallenge.screens.orders.OrderModel;
import br.com.williamhigino.skipchallenge.screens.products.ProductModel;

/**
 * Created by williamhigino on 18/03/2018.
 */

public interface ChartView {
    void setItems(List<OrderItemModel> orderItemModels);
    void loadChartItems(ChartModel currentChart);
    void showOrderPlacedDialog();
    void showChartEmptyDialog();
    void showSeveralRestaurantsDialog(final List<OrderModel> orderModels);
}
