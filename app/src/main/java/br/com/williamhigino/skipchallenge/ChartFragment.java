package br.com.williamhigino.skipchallenge;

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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static br.com.williamhigino.skipchallenge.PersistentDataManager.CURRENT_CHART;
import static br.com.williamhigino.skipchallenge.PersistentDataManager.CURRENT_CUSTOMER;


public class ChartFragment extends Fragment {

    @BindView(R.id.chart_recycler)
    RecyclerView chartRecycler;

    @BindView(R.id.place_order_button)
    Button placeOrderButton;

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
                                adapter.setItems(currentChart.orderItems);
                            }
                        })
                        .show();
            }
        });
        chartRecycler.setLayoutManager(new GridLayoutManager(mActivity, 1));
        chartRecycler.setAdapter(adapter);

        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
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

    }

    private void placeOrder() {

        final ChartModel currentChart = persistentDataManager.ReadModel(PersistentDataManager.CURRENT_CHART, ChartModel.class);
        List<OrderModel> orderModels = OrderModel.getOrdersFromChart(currentChart);

        CustomerModel currentCustomer = persistentDataManager.ReadModel(CURRENT_CUSTOMER, CustomerModel.class);
        String authorization = "Bearer " + currentCustomer.token;
        //TODO: send all orders
        apiInterface.placeOrder(authorization, orderModels.get(0))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("TAG", "error: " + throwable, throwable);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        //TODO: dialog de sucesso
                        //clears latest chart info
                        persistentDataManager.SaveModel(new ChartModel(), CURRENT_CHART);
                        adapter.setItems(new ArrayList<OrderItemModel>());
                    }
                }).subscribe();

        Log.d("ChartFragment", orderModels.toString());
    }

}

