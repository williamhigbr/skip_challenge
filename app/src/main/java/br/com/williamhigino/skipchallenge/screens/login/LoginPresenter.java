package br.com.williamhigino.skipchallenge.screens.login;

import android.util.Log;

import br.com.williamhigino.skipchallenge.util.APIRxJavaInterface;
import br.com.williamhigino.skipchallenge.screens.cart.ChartModel;
import br.com.williamhigino.skipchallenge.util.PersistentDataManager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static br.com.williamhigino.skipchallenge.util.PersistentDataManager.CURRENT_CHART;
import static br.com.williamhigino.skipchallenge.util.PersistentDataManager.CURRENT_CUSTOMER;

/**
 * Created by williamhigino on 18/03/2018.
 */

public class LoginPresenter {

    private LoginView view;
    private PersistentDataManager persistentDataManager;
    private APIRxJavaInterface apiInterface;

    public LoginPresenter(LoginView view,
                          APIRxJavaInterface apiInterface,
                          PersistentDataManager persistentDataManager) {
        this.view = view;
        this.apiInterface = apiInterface;
        this.persistentDataManager = persistentDataManager;

        CustomerModel currentCustomer = persistentDataManager.ReadModel(CURRENT_CUSTOMER, CustomerModel.class);
        if(currentCustomer != null) {
            //if we have a customer loged in, go to main activity
            view.goToMainActivity();
        }
    }

    public void attemptLogin(final String email, final String password) {

        view.showLoadingViews();

        apiInterface.authCustomer(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("TAG", "error: " + throwable, throwable);
                        view.hideLoadingViews();
                        view.showLoginError();
                    }
                })
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        CustomerModel customerModel = new CustomerModel();
                        customerModel.email = email;
                        customerModel.password = password;
                        customerModel.token = s;
                        persistentDataManager.SaveModel(customerModel, CURRENT_CUSTOMER);
                        view.goToMainActivity();
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        persistentDataManager.SaveModel(new ChartModel(), CURRENT_CHART);
                        view.hideLoadingViews();
                    }
                }).subscribe();

    }

}
