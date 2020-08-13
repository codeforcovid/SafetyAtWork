package com.safeatwork.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.messaging.FirebaseMessaging;
import com.safeatwork.R;
import com.safeatwork.common.PrefSession;
import com.safeatwork.data.DummyUserDataProvider;
import com.safeatwork.ui.DashBoardActivity;

import static com.safeatwork.model.Constants.ADMIN_ACCESS;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    PrefSession session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new PrefSession(this);
        String user_session = session.getUserEmailLoginSession();
        if (!user_session.equals("0")) {
            //already logged in
            launchDashBoard(user_session);
            finish();
        }

        setContentView(R.layout.activity_login);

        DummyUserDataProvider dummyUserDataProvider = new DummyUserDataProvider();
        dummyUserDataProvider.createDummyUserData(getApplicationContext());

        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory(getApplicationContext()))
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.emp_password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }

                if (loginResult.getSuccess() != null) {
                    LoggedInUserView loggedInUserView = loginResult.getSuccess();
                    updateUiWithUser(loggedInUserView);
                    session.setUserEmailLoginSession(loggedInUserView.getEmpEmail());
                    session.setAdminAccess(loggedInUserView.getAdminAccess());
                    session.setUserName(loggedInUserView.getDisplayName());
                    if(loggedInUserView.getAdminAccess().equals(ADMIN_ACCESS)) {
                        FirebaseMessaging.getInstance().subscribeToTopic("admin");
                    }
                    launchDashBoard(loggedInUserView.getEmpEmail());

                }

                setResult(Activity.RESULT_OK);

            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });
    }

    void launchDashBoard(String user_email){
        Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
        intent.putExtra("emp_email", user_email);
        startActivity(intent);
        finish();
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
