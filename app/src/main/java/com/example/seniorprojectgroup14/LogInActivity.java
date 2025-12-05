package com.example.seniorprojectgroup14;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.backend.*;
import com.example.seniorprojectgroup14.retrofitAPICommunication.*;
import com.example.seniorprojectgroup14.retrofitAPICommunication.DataRepository.RepoCallback;
import com.example.seniorprojectgroup14.plainOldJavaObjects.*;

public class LogInActivity extends Activity {

    EditText usernameInput;
    EditText passwordInput;
    DataRepository dataRepository = DataRepository.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        usernameInput = (EditText) findViewById(R.id.usernameInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
    }

    public void buttonClicked(View view) {

        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (view.getId() == R.id.button) {
            if ((!username.isEmpty()) && !password.isEmpty()) {
                Intent intent = new Intent(LogInActivity.this, RiderStatusActivity.class);
                dataRepository.login(username, password, new RepoCallback<LoginResponse>() {
                    @Override
                    public void onSuccess(LoginResponse result) {
                        if (result.isSuccess()) {
                            Toast.makeText(LogInActivity.this, "Login successful", Toast.LENGTH_LONG).show();
                            SessionManager.getInstance().setSession(result.getUserId(), username);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LogInActivity.this, "SERVER DENIED: Check username/password rules.", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onError(String message) {
                        System.out.println(message);
                        Toast.makeText(LogInActivity.this, "Registration failed: " + message, Toast.LENGTH_LONG).show();
                    }
                });
            }
            else if (!username.isEmpty()) {
                CharSequence text = "Please enter a password";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(this , text, duration);
                toast.show();
            }
            else if (!password.isEmpty()) {
                CharSequence text = "Please enter a username";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(this , text, duration);
                toast.show();
            }
            else {
                CharSequence text = "Please enter a username and password";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(this , text, duration);
                toast.show();
            }
        }
    }
}