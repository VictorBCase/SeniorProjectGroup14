package com.example.seniorprojectgroup14;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.backend.*;

public class SignUpActivity extends Activity {

    EditText usernameInput;
    EditText passwordInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);

        usernameInput = (EditText) findViewById(R.id.usernameInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
    }

    public void buttonClicked(View view) {

        if (view.getId() == R.id.button) {
            if ((usernameInput != null) && passwordInput != null) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else if (usernameInput != null) {
                CharSequence text = "Please enter a password";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(this , text, duration);
                toast.show();
            }
            else if (passwordInput != null) {
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