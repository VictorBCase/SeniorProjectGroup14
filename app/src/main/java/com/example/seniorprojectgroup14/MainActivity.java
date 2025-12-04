package com.example.seniorprojectgroup14;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.backend.*;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
    }

    public void buttonClicked(View view) {

        if (view.getId() == R.id.button) {
            Intent intent = new Intent(MainActivity.this, QRScannerActivity.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.button2) {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        }
    }
}
