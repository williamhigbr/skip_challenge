package br.com.williamhigino.skipchallenge;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static br.com.williamhigino.skipchallenge.PersistentDataManager.CURRENT_CHART;
import static br.com.williamhigino.skipchallenge.PersistentDataManager.CURRENT_CUSTOMER;


public class LoginActivity extends AppCompatActivity {


    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;

    private AppCompatActivity mActivity;

    private PersistentDataManager persistentDataManager;

    @BindView(R.id.email_sign_in_button)
    Button signInButton;

    private APIRxJavaInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mActivity = this;

        persistentDataManager = PersistentDataManager.getInstance(mActivity);

        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        mProgressView = findViewById(R.id.login_progress);


        ButterKnife.bind(this);
        apiInterface = APIClient.getStringRxJavaClient().create(APIRxJavaInterface.class);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmailView.setText("williamhigino@gmail.com");
                mPasswordView.setText("wh2807");

                String email = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();
                attemptLogin(email, password);
            }
        });


        CustomerModel currentCustomer = persistentDataManager.ReadModel(CURRENT_CUSTOMER, CustomerModel.class);
        if(currentCustomer != null) {
            //if we have a customer loged in, go to main activity
            goToMainActivity();
        }

        //TODO: signup (create customer)

    }


    private void attemptLogin(final String email, final String password) {

        mProgressView.setVisibility(View.VISIBLE);

        apiInterface.authCustomer(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("TAG", "error: " + throwable, throwable);
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
                        goToMainActivity();
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        persistentDataManager.SaveModel(new ChartModel(), CURRENT_CHART);
                        mProgressView.setVisibility(View.GONE);
                    }
                }).subscribe();

    }

    private void goToMainActivity() {
        Intent intent = new Intent(mActivity, MainActivity.class);
        mActivity.startActivity(intent);
        finish();
    }

}

