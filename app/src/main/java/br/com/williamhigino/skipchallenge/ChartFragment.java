package br.com.williamhigino.skipchallenge;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class ChartFragment extends Fragment {

    @BindView(R.id.chart_recycler)
    RecyclerView chartRecycler;

    private OrderItemsAdapter adapter;

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

        persistentDataManager = PersistentDataManager.getInstance(mActivity);

        //initializes
        adapter = new OrderItemsAdapter(new Consumer<OrderItemModel>() {
            @Override
            public void accept(final OrderItemModel item) throws Exception {
                new MaterialDialog.Builder(mActivity)
                        .title(R.string.products_add_to_chart)
                        .content(item.product.name)
                        .show();
            }
        });
        chartRecycler.setLayoutManager(new GridLayoutManager(mActivity, 1));
        chartRecycler.setAdapter(adapter);

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

        Observable.just(persistentDataManager.ReadModel(PersistentDataManager.CURRENT_CHART, ChartModel.class))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<ChartModel>() {
                    @Override
                    public void accept(ChartModel chartModel) throws Exception {
                        adapter.setItems(chartModel.orderItems);
                    }
                }).subscribe();

    }
}

