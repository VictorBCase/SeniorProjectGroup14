package com.example.seniorprojectgroup14;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.backend.*;
import com.example.seniorprojectgroup14.retrofitAPICommunication.*;

public class MainActivity extends Activity {

    private final DataRepository dataRepository = DataRepository.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
    }

    public void buttonClicked(View view) {

        if (view.getId() == R.id.button) {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.button2) {
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            startActivity(intent);
        }
    }
}
