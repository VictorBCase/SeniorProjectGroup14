package com.example.seniorprojectgroup14;

import com.example.backend.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.seniorprojectgroup14.retrofitAPICommunication.*;
import com.example.seniorprojectgroup14.retrofitAPICommunication.DataRepository.*;
import com.example.seniorprojectgroup14.plainOldJavaObjects.*;

public class JoinGroupActivity extends Activity {

    EditText groupNameInput;
    DataRepository dataRepository = DataRepository.getInstance();
    private SessionManager sessionManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_group_layout);

        groupNameInput = (EditText) findViewById(R.id.groupName);
    }

    public void buttonClicked(View view) {

        String groupName = groupNameInput.getText().toString();
        Intent intent = new Intent(JoinGroupActivity.this, QueueActivity.class);

        if (view.getId() == R.id.checkStatus) {
            dataRepository.isMember(groupName, sessionManager.getUserId(), new RepoCallback<IsMemberResponse>() {
                @Override
                public void onSuccess(IsMemberResponse result) {
                    if (result.getIsMember()) {
                        startActivity(intent);
                    }
                    else {
                        //logic
                    }
                }
                @Override
                public void onError(String message) {
                }
            });
        }

        if (view.getId() == R.id.back)
        {
            Intent intent2 = new Intent(JoinGroupActivity.this, RiderStatusActivity.class);
            startActivity(intent2);
        }
    }
}
