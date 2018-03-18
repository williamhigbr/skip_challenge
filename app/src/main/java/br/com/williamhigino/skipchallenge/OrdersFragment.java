package br.com.williamhigino.skipchallenge;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static br.com.williamhigino.skipchallenge.PersistentDataManager.CURRENT_CUSTOMER;


public class OrdersFragment extends Fragment {

    @BindView(R.id.orders_recycler)
    RecyclerView ordersRecycler;

    private OrdersAdapter adapter;

    private APIRxJavaInterface apiInterface;

    private Activity mActivity;

    private PersistentDataManager persistentDataManager;

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

        apiInterface = APIClient.getRxJavaClient().create(APIRxJavaInterface.class);

        persistentDataManager = PersistentDataManager.getInstance(mActivity);

        //initializes
        adapter = new OrdersAdapter(new Consumer<OrderModel>() {
            @Override
            public void accept(final OrderModel item) throws Exception {
                //TODO: manage order click
            }
        });
        ordersRecycler.setLayoutManager(new GridLayoutManager(mActivity, 1));
        ordersRecycler.setAdapter(adapter);

        //loads current chart
        loadOrders();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden) {
            loadOrders();
        }
    }


    private void loadOrders() {

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
                        adapter.setItems(orderModels);
                    }
                }).subscribe();


    }
}

