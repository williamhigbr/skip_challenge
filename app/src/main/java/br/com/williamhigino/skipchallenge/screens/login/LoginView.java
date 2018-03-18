package br.com.williamhigino.skipchallenge.screens.login;

/**
 * Created by williamhigino on 18/03/2018.
 */

public interface LoginView {
    void showLoadingViews();
    void hideLoadingViews();
    void showLoginError();
    void goToMainActivity();
}
