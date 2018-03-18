package br.com.williamhigino.skipchallenge.screens.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import br.com.williamhigino.skipchallenge.util.APIClient;
import br.com.williamhigino.skipchallenge.util.APIRxJavaInterface;
import br.com.williamhigino.skipchallenge.screens.main.MainActivity;
import br.com.williamhigino.skipchallenge.util.PersistentDataManager;
import br.com.williamhigino.skipchallenge.R;
import butterknife.BindView;
import butterknife.ButterKnife;


public class LoginActivity extends AppCompatActivity implements LoginView {


    @BindView(R.id.email)
    TextView mEmailView;
    @BindView(R.id.password)
    EditText mPasswordView;
    @BindView(R.id.login_progress)
    View mProgressView;
    @BindView(R.id.login_form)
    View mLoginFormView;
    @BindView(R.id.email_sign_in_button)
    Button signInButton;

    private AppCompatActivity mActivity;

    private PersistentDataManager persistentDataManager;

    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mActivity = this;
        ButterKnife.bind(this);

        presenter = new LoginPresenter(
                this,
                APIClient.getStringRxJavaClient().create(APIRxJavaInterface.class),
                PersistentDataManager.getInstance(mActivity));

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();
                presenter.attemptLogin(email, password);
            }
        });

    }

    @Override
    public void goToMainActivity() {
        Intent intent = new Intent(mActivity, MainActivity.class);
        mActivity.startActivity(intent);
        finish();
    }

    @Override
    public void showLoadingViews() {
        mProgressView.setVisibility(View.VISIBLE);
        mLoginFormView.setVisibility(View.GONE);
    }

    @Override
    public void hideLoadingViews() {
        mProgressView.setVisibility(View.GONE);
        mLoginFormView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoginError() {
        new MaterialDialog.Builder(mActivity)
                .title(R.string.login_error_title)
                .content(R.string.login_error)
                .positiveText("Ok")
                .show();
    }
}

