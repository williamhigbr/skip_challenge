package br.com.williamhigino.skipchallenge.screens.orders;

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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import br.com.williamhigino.skipchallenge.util.APIClient;
import br.com.williamhigino.skipchallenge.util.APIRxJavaInterface;
import br.com.williamhigino.skipchallenge.util.PersistentDataManager;
import br.com.williamhigino.skipchallenge.R;
import br.com.williamhigino.skipchallenge.screens.login.CustomerModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static br.com.williamhigino.skipchallenge.util.PersistentDataManager.CURRENT_CUSTOMER;


public class OrdersFragment extends Fragment implements OrdersView {

    @BindView(R.id.orders_recycler)
    RecyclerView ordersRecycler;

    private OrdersAdapter adapter;

    private Activity mActivity;

    private OrdersPresenter presenter;

    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mActivity = getActivity();

        presenter = new OrdersPresenter(
                this,
                APIClient.getRxJavaClient().create(APIRxJavaInterface.class),
                PersistentDataManager.getInstance(mActivity)
        );

        //initializes
        adapter = new OrdersAdapter(new Consumer<OrderModel>() {
            @Override
            public void accept(final OrderModel item) throws Exception {
                new MaterialDialog.Builder(mActivity)
                        .title(R.string.orders_cancel_title)
                        .content(R.string.orders_cancel)
                        .positiveText(R.string.orders_cancel_yes)
                        .neutralText(R.string.orders_cancel_no)
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                presenter.cancelOrder(item);
                            }
                        })
                        .show();
            }
        });
        ordersRecycler.setLayoutManager(new GridLayoutManager(mActivity, 1));
        ordersRecycler.setAdapter(adapter);

        //loads current chart
        presenter.loadOrders();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden) {
            presenter.loadOrders();
        }
    }



    @Override
    public void setItems(List<OrderModel> orderModels) {
        adapter.setItems(orderModels);
    }

    @Override
    public void updateItems() {
        adapter.notifyDataSetChanged();
    }
}

