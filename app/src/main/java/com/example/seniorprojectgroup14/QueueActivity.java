package com.example.seniorprojectgroup14;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class QueueActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.queue_view);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            Boolean isGroup = bundle.getBoolean("isGroup");
            if (isGroup) {
                TextView tv1 = (TextView) findViewById(R.id.textView4);
                tv1.setText("1. " + bundle.getString("testGroup"));
            } else if (!isGroup) {
                TextView tv1 = (TextView) findViewById(R.id.textView4);
                tv1.setText("1. " + bundle.getString("testUser"));
            }
        }
    }
}
