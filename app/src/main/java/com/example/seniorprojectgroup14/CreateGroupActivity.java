package com.example.seniorprojectgroup14;

import com.example.backend.*;
import com.example.seniorprojectgroup14.retrofitAPICommunication.*;
import com.example.seniorprojectgroup14.retrofitAPICommunication.DataRepository.*;
import com.example.seniorprojectgroup14.plainOldJavaObjects.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class CreateGroupActivity extends Activity {

    EditText groupNameInput;
    EditText addMemberInput;
    DataRepository dataRepository = DataRepository.getInstance();
    private SessionManager sessionManager;

    private String groupId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group_layout);

        groupNameInput = (EditText) findViewById(R.id.groupName);
        addMemberInput = (EditText) findViewById(R.id.addMember);
        sessionManager = SessionManager.getInstance();
        findViewById(R.id.done).setEnabled(false);
        findViewById(R.id.addMember).setEnabled(false);
        findViewById(R.id.addToGroup).setEnabled(false);
    }

    public void buttonClicked(View view) {

        String groupName = groupNameInput.getText().toString();
        String addMember = addMemberInput.getText().toString();

        if (view.getId() == R.id.createGroup) {
            if ((!groupName.isEmpty())) {
                findViewById(R.id.createGroup).setEnabled(false);
                findViewById(R.id.groupName).setEnabled(false);
                dataRepository.createGroup(groupName, sessionManager.getUserId(), new RepoCallback<CreateGroupResponse>() {

                    @Override
                    public void onSuccess(CreateGroupResponse result) {
                        findViewById(R.id.addMember).setEnabled(true);
                        findViewById(R.id.addToGroup).setEnabled(true);
                        groupId = result.getGroupId();
                        Toast.makeText(CreateGroupActivity.this, "Group: " + groupName + " successfully created!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(String message) {
                        findViewById(R.id.createGroup).setEnabled(true);
                        findViewById(R.id.groupName).setEnabled(true);
                        Toast.makeText(CreateGroupActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                    }
                });
            }
            else {
                Toast.makeText(CreateGroupActivity.this, "Enter group name", Toast.LENGTH_LONG).show();
            }

        }

        if (view.getId() == R.id.addToGroup) {
            if (!addMember.isEmpty()) {
                dataRepository.addMemberToGroup(groupId, addMember, new RepoCallback<AddMemberToGroupResponse>() {

                    @Override
                    public void onSuccess(AddMemberToGroupResponse result) {
                        findViewById(R.id.done).setEnabled(true);
                        Toast.makeText(CreateGroupActivity.this, addMember + " added to group!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(CreateGroupActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

        if (view.getId() == R.id.done) {
            Intent intent = new Intent(CreateGroupActivity.this, QRScannerActivity.class);
            startActivity(intent);
        }
    }
}
