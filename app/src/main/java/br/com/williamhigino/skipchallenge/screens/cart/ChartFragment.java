package br.com.williamhigino.skipchallenge.screens.cart;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import br.com.williamhigino.skipchallenge.util.APIClient;
import br.com.williamhigino.skipchallenge.util.APIRxJavaInterface;
import br.com.williamhigino.skipchallenge.screens.orders.OrderModel;
import br.com.williamhigino.skipchallenge.util.PersistentDataManager;
import br.com.williamhigino.skipchallenge.R;
import br.com.williamhigino.skipchallenge.screens.login.CustomerModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static br.com.williamhigino.skipchallenge.util.PersistentDataManager.CURRENT_CHART;
import static br.com.williamhigino.skipchallenge.util.PersistentDataManager.CURRENT_CUSTOMER;


public class ChartFragment extends Fragment {

    @BindView(R.id.chart_recycler)
    RecyclerView chartRecycler;

    @BindView(R.id.place_order_button)
    Button placeOrderButton;

    @BindView(R.id.empty_cart_text)
    TextView emptyCartText;

    private OrderItemsAdapter adapter;

    private APIRxJavaInterface apiInterface;

    private Activity mActivity;

    private PersistentDataManager persistentDataManager;

    public ChartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chart, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mActivity = getActivity();

        apiInterface = APIClient.getRxJavaClient().create(APIRxJavaInterface.class);

        persistentDataManager = PersistentDataManager.getInstance(mActivity);

        //initializes
        adapter = new OrderItemsAdapter(new Consumer<OrderItemModel>() {
            @Override
            public void accept(final OrderItemModel item) throws Exception {
                new MaterialDialog.Builder(mActivity)
                        .title(R.string.products_remove_from_chart)
                        .content(item.product.productShortString())
                        .positiveText(R.string.products_remove_from_chart_yes)
                        .neutralText(R.string.products_remove_from_chart_neutral)
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ChartModel currentChart = persistentDataManager.ReadModel(PersistentDataManager.CURRENT_CHART, ChartModel.class);
                                currentChart.removeItem(item);
                                persistentDataManager.SaveModel(currentChart, PersistentDataManager.CURRENT_CHART);
                                loadChartItems();
                            }
                        })
                        .show();
            }
        });
        chartRecycler.setLayoutManager(new GridLayoutManager(mActivity, 1));
        chartRecycler.setAdapter(adapter);

        chartRecycler.setVisibility(View.GONE);
        emptyCartText.setVisibility(View.GONE);

        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareOrder();
            }
        });

        //loads current chart
        loadChartItems();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden) {
            loadChartItems();
        }
    }


    private void loadChartItems() {

        ChartModel currentChart = persistentDataManager.ReadModel(PersistentDataManager.CURRENT_CHART, ChartModel.class);
        adapter.setItems(currentChart.orderItems);

        if(currentChart.orderItems.size() > 0) {
            chartRecycler.setVisibility(View.VISIBLE);
            emptyCartText.setVisibility(View.GONE);
        }
        else {
            chartRecycler.setVisibility(View.GONE);
            emptyCartText.setVisibility(View.VISIBLE);
        }

    }

    private void prepareOrder() {

        final ChartModel currentChart = persistentDataManager.ReadModel(PersistentDataManager.CURRENT_CHART, ChartModel.class);
        final List<OrderModel> orderModels = OrderModel.getOrdersFromChart(currentChart);

        if(orderModels.size() == 0) {
            new MaterialDialog.Builder(mActivity)
                    .title(R.string.chart_empty_title)
                    .content(R.string.chart_empty)
                    .positiveText("Ok")
                    .show();
        }
        else if(orderModels.size() > 1) {
            new MaterialDialog.Builder(mActivity)
                    .title(R.string.chart_several_restaurants_title)
                    .content(R.string.chart_several_restaurants)
                    .positiveText(R.string.chart_several_restaurants_yes)
                    .neutralText(R.string.chart_several_restaurants_no)
                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            placeOrders(orderModels);
                        }
                    })
                    .show();
        }
        else {
            placeOrders(orderModels);
        }

    }

    private void placeOrders(List<OrderModel> orderModels) {

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
                        new MaterialDialog.Builder(mActivity)
                                .title(R.string.chart_order_placed_title)
                                .content(R.string.chart_order_placed)
                                .positiveText("Ok")
                                .show();
                        //clears latest chart info
                        persistentDataManager.SaveModel(new ChartModel(), CURRENT_CHART);
                        adapter.setItems(new ArrayList<OrderItemModel>());
                    }
                }).subscribe();

    }

}

