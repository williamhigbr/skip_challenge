package br.com.williamhigino.skipchallenge.screens.cart;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import br.com.williamhigino.skipchallenge.R;
import br.com.williamhigino.skipchallenge.screens.orders.OrderModel;
import br.com.williamhigino.skipchallenge.util.APIClient;
import br.com.williamhigino.skipchallenge.util.APIRxJavaInterface;
import br.com.williamhigino.skipchallenge.util.PersistentDataManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;


public class ChartFragment extends Fragment implements ChartView {

    @BindView(R.id.chart_recycler)
    RecyclerView chartRecycler;

    @BindView(R.id.place_order_button)
    Button placeOrderButton;

    @BindView(R.id.empty_cart_text)
    TextView emptyCartText;

    private OrderItemsAdapter adapter;

    private Activity mActivity;

    private ChartPresenter presenter;

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

        presenter = new ChartPresenter(
                this,
                APIClient.getRxJavaClient().create(APIRxJavaInterface.class),
                PersistentDataManager.getInstance(mActivity)
        );

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
                                presenter.manageOrderItemClick(item);
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
                presenter.prepareOrder();
            }
        });

        //loads current chart
        presenter.loadChartItems();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden) {
            presenter.loadChartItems();
        }
    }

    @Override
    public void loadChartItems(ChartModel currentChart) {

        setItems(currentChart.orderItems);

        if(currentChart.orderItems.size() > 0) {
            chartRecycler.setVisibility(View.VISIBLE);
            emptyCartText.setVisibility(View.GONE);
        }
        else {
            chartRecycler.setVisibility(View.GONE);
            emptyCartText.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void showChartEmptyDialog() {
        new MaterialDialog.Builder(mActivity)
                .title(R.string.chart_empty_title)
                .content(R.string.chart_empty)
                .positiveText("Ok")
                .show();
    }

    @Override
    public void showSeveralRestaurantsDialog(final List<OrderModel> orderModels) {
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
                        presenter.placeOrders(orderModels);
                    }
                })
                .show();
    }

    @Override
    public void showOrderPlacedDialog() {
        new MaterialDialog.Builder(mActivity)
                .title(R.string.chart_order_placed_title)
                .content(R.string.chart_order_placed)
                .positiveText("Ok")
                .show();
    }

    @Override
    public void setItems(List<OrderItemModel> orderItemModels) {
        adapter.setItems(orderItemModels);
    }

}

