package br.com.williamhigino.skipchallenge.screens.products;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import br.com.williamhigino.skipchallenge.util.APIClient;
import br.com.williamhigino.skipchallenge.util.APIRxJavaInterface;
import br.com.williamhigino.skipchallenge.screens.cart.ChartModel;
import br.com.williamhigino.skipchallenge.screens.cart.OrderItemModel;
import br.com.williamhigino.skipchallenge.util.PersistentDataManager;
import br.com.williamhigino.skipchallenge.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class ProductsFragment extends Fragment {

    @BindView(R.id.products_recycler)
    RecyclerView productsRecycler;

    private ProductsAdapter adapter;

    private APIRxJavaInterface apiInterface;

    private Activity mActivity;

    public ProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_products, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mActivity = getActivity();

        apiInterface = APIClient.getRxJavaClient().create(APIRxJavaInterface.class);


        BottomNavigationView navigation = view.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        //initializes
        adapter = new ProductsAdapter(new Consumer<ProductModel>() {
            @Override
            public void accept(final ProductModel item) throws Exception {
                new MaterialDialog.Builder(mActivity)
                        .title(R.string.products_add_to_chart)
                        .content(item.productShortString())
                        .positiveText(R.string.products_add_to_chart_yes)
                        .negativeText(R.string.products_add_to_chart_no)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //saves current chart in prefs
                                PersistentDataManager persistentDataManager = PersistentDataManager.getInstance(mActivity);
                                ChartModel currentChart = persistentDataManager.ReadModel(PersistentDataManager.CURRENT_CHART, ChartModel.class);
                                currentChart.orderItems.add(new OrderItemModel(item, 1));
                                //TODO: include quantity in dialog
                                persistentDataManager.SaveModel(currentChart, PersistentDataManager.CURRENT_CHART);
                            }
                        })
                        .show();
            }
        });
        productsRecycler.setLayoutManager(new GridLayoutManager(mActivity, 1));
        productsRecycler.setAdapter(adapter);

        //gets products list
        getProducts();
    }

    private void getProducts() {

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
                        adapter.addItems(productModels);
                    }
                }).subscribe();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            SwitchFragment(item.getItemId());
            return true;
        }

    };
}

