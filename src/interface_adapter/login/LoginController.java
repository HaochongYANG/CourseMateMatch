package interface_adapter.login;

import use_case.login.LoginInputBoundary;
import use_case.login.LoginInputData;

import java.io.IOException;

public class LoginController {

    final LoginInputBoundary interactor;

    public LoginController (LoginInputBoundary interactor){
        this.interactor = interactor;
    }

    public void execute (String username, String password) throws IOException {
        LoginInputData loginInputData = new LoginInputData(username, password);

        interactor.login(loginInputData);
    }
}
