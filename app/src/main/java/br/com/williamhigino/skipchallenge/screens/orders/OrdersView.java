package br.com.williamhigino.skipchallenge.screens.orders;

import java.util.List;

import br.com.williamhigino.skipchallenge.screens.cart.ChartModel;
import br.com.williamhigino.skipchallenge.screens.cart.OrderItemModel;

/**
 * Created by williamhigino on 18/03/2018.
 */

public interface OrdersView {
    void setItems(List<OrderModel> orderModels);
    void updateItems();
}
