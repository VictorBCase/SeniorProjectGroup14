package com.example.seniorprojectgroup14.retrofitAPICommunication;

import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.seniorprojectgroup14.plainOldJavaObjects.*;

import java.io.IOException;
import java.lang.Throwable;

public class DataRepository {

    private static DataRepository instance;
    private final ApiService apiService = RetrofitClient.getApiService();
    private static final String TAG = "DataRepository";

    private DataRepository() {
    }

    public static synchronized DataRepository getInstance() {
        if (instance == null) {
            instance = new DataRepository();
        }
        return instance;
    }

    public interface RepoCallback<T> {
        void onSuccess(T result);

        void onError(String message);
    }

    private <T> void handleError(Call<T> call, Throwable t, RepoCallback<T> callback) {
        String errorMsg = "Network Failure: " + t.getMessage();
        Log.e(TAG, errorMsg);
        callback.onError("server connection failure");
    }

    private <T> void handleResponseError(Response<T> response, RepoCallback<T> callback) {
        try {
            String errorJson = response.errorBody() != null ? response.errorBody().string() : "{}";
            String message = "request error: " + response.code();
            Log.e(TAG, "Response Error: " + response.code() + ", Body: " + errorJson);

            callback.onError(message);

        } catch (IOException e) {
            Log.e(TAG, "error: " + e.getMessage());
            callback.onError("unexpected server error");
        }
    }

    public void register(String username, String password, final RepoCallback<RegisterResponse> callback) {

        RegisterRequest request = new RegisterRequest(username, password);

        Call<RegisterResponse> call = apiService.register(request);

            call.enqueue(new Callback<RegisterResponse>() {

            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    handleResponseError(response, callback);
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                handleError(call, t, callback);
            }
        });
    }

    public void login(String username, String password, final RepoCallback<LoginResponse> callback) {

        LoginRequest request = new LoginRequest(username, password);

        Call<LoginResponse> call = apiService.login(request);

        call.enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    handleResponseError(response, callback);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                handleError(call, t, callback);
            }
        });
    }

    public void scanToJoin(String rideId, String entityId, String scannedCode, final RepoCallback<ScanToJoinResponse> callback) {

        ScanToJoinRequest request = new ScanToJoinRequest(rideId, entityId, scannedCode);

        Call<ScanToJoinResponse> call = apiService.scanToJoin(request);

        call.enqueue(new Callback<ScanToJoinResponse>() {

            @Override
            public void onResponse(Call<ScanToJoinResponse> call, Response<ScanToJoinResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    handleResponseError(response, callback);
                }
            }

            @Override
            public void onFailure(Call<ScanToJoinResponse> call, Throwable t) {
                handleError(call, t, callback);
            }
        });
    }

    public void scanToLeave(String rideId, String entityId, String scannedCode, final RepoCallback<ScanToLeaveResponse> callback) {

        ScanToLeaveRequest request = new ScanToLeaveRequest(rideId, entityId, scannedCode);

        Call<ScanToLeaveResponse> call = apiService.scanToLeave(request);

        call.enqueue(new Callback<ScanToLeaveResponse>() {

            @Override
            public void onResponse(Call<ScanToLeaveResponse> call, Response<ScanToLeaveResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    handleResponseError(response, callback);
                }
            }

            @Override
            public void onFailure(Call<ScanToLeaveResponse> call, Throwable t) {
                handleError(call, t, callback);
            }
        });
    }

    public void viewWaitTime(String rideId, final RepoCallback<ViewWaitTimeResponse> callback) {

        ViewWaitTimeRequest request = new ViewWaitTimeRequest(rideId);

        Call<ViewWaitTimeResponse> call = apiService.viewWaitTime(request);

        call.enqueue(new Callback<ViewWaitTimeResponse>() {

            @Override
            public void onResponse(Call<ViewWaitTimeResponse> call, Response<ViewWaitTimeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    handleResponseError(response, callback);
                }
            }

            @Override
            public void onFailure(Call<ViewWaitTimeResponse> call, Throwable t) {
                handleError(call, t, callback);
            }
        });
    }

    public void viewQueue(String rideId, final RepoCallback<ViewQueueResponse> callback) {

        ViewQueueRequest request = new ViewQueueRequest(rideId);

        Call<ViewQueueResponse> call = apiService.viewQueue(request);

        call.enqueue(new Callback<ViewQueueResponse>() {

            @Override
            public void onResponse(Call<ViewQueueResponse> call, Response<ViewQueueResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    handleResponseError(response, callback);
                }
            }

            @Override
            public void onFailure(Call<ViewQueueResponse> call, Throwable t) {
                handleError(call, t, callback);
            }
        });
    }

    public void userPosition(String rideId, String entityId, final RepoCallback<UserPositionResponse> callback) {

        UserPositionRequest request = new UserPositionRequest(rideId, entityId);

        Call<UserPositionResponse> call = apiService.userPosition(request);

        call.enqueue(new Callback<UserPositionResponse>() {

            @Override
            public void onResponse(Call<UserPositionResponse> call, Response<UserPositionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    handleResponseError(response, callback);
                }
            }

            @Override
            public void onFailure(Call<UserPositionResponse> call, Throwable t) {
                handleError(call, t, callback);
            }
        });
    }

    public void createGroup(String groupName, String ownerId, final RepoCallback<CreateGroupResponse> callback) {

        CreateGroupRequest request = new CreateGroupRequest(groupName, ownerId);

        Call<CreateGroupResponse> call = apiService.createGroup(request);

        call.enqueue(new Callback<CreateGroupResponse>() {

            @Override
            public void onResponse(Call<CreateGroupResponse> call, Response<CreateGroupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    handleResponseError(response, callback);
                }
            }

            @Override
            public void onFailure(Call<CreateGroupResponse> call, Throwable t) {
                handleError(call, t, callback);
            }
        });
    }

    public void scanToJoinGroup(String rideId, String groupId, String scannedCode, final RepoCallback<ScanToJoinGroupResponse> callback) {

        ScanToJoinGroupRequest request = new ScanToJoinGroupRequest(rideId, groupId, scannedCode);

        Call<ScanToJoinGroupResponse> call = apiService.scanToJoinGroup(request);

        call.enqueue(new Callback<ScanToJoinGroupResponse>() {

            @Override
            public void onResponse(Call<ScanToJoinGroupResponse> call, Response<ScanToJoinGroupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    handleResponseError(response, callback);
                }
            }

            @Override
            public void onFailure(Call<ScanToJoinGroupResponse> call, Throwable t) {
                handleError(call, t, callback);
            }
        });
    }

    public void scanToLeaveGroup(String rideId, String groupId, String scannedCode, final RepoCallback<ScanToLeaveGroupResponse> callback) {

        ScanToLeaveGroupRequest request = new ScanToLeaveGroupRequest(rideId, groupId, scannedCode);

        Call<ScanToLeaveGroupResponse> call = apiService.scanToLeaveGroup(request);

        call.enqueue(new Callback<ScanToLeaveGroupResponse>() {

            @Override
            public void onResponse(Call<ScanToLeaveGroupResponse> call, Response<ScanToLeaveGroupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    handleResponseError(response, callback);
                }
            }

            @Override
            public void onFailure(Call<ScanToLeaveGroupResponse> call, Throwable t) {
                handleError(call, t, callback);
            }
        });
    }

    public void addMemberToGroup(String groupId, String username, final RepoCallback<AddMemberToGroupResponse> callback) {

        AddMemberToGroupRequest request = new AddMemberToGroupRequest(groupId, username);

        Call<AddMemberToGroupResponse> call = apiService.addMemberToGroup(request);

        call.enqueue(new Callback<AddMemberToGroupResponse>() {

            @Override
            public void onResponse(Call<AddMemberToGroupResponse> call, Response<AddMemberToGroupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    handleResponseError(response, callback);
                }
            }

            @Override
            public void onFailure(Call<AddMemberToGroupResponse> call, Throwable t) {
                handleError(call, t, callback);
            }
        });
    }

    public void removeMemberFromGroup(String groupId, String username, final RepoCallback<RemoveMemberFromGroupResponse> callback) {

        RemoveMemberFromGroupRequest request = new RemoveMemberFromGroupRequest(groupId, username);

        Call<RemoveMemberFromGroupResponse> call = apiService.removeMemberFromGroup(request);

        call.enqueue(new Callback<RemoveMemberFromGroupResponse>() {

            @Override
            public void onResponse(Call<RemoveMemberFromGroupResponse> call, Response<RemoveMemberFromGroupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    handleResponseError(response, callback);
                }
            }

            @Override
            public void onFailure(Call<RemoveMemberFromGroupResponse> call, Throwable t) {
                handleError(call, t, callback);
            }
        });
    }

    public void isMember(String groupName, String userId, final RepoCallback<IsMemberResponse> callback) {

        IsMemberRequest request = new IsMemberRequest(groupName, userId);

        Call<IsMemberResponse> call = apiService.isMember(request);

        call.enqueue(new Callback<IsMemberResponse>() {

            @Override
            public void onResponse(Call<IsMemberResponse> call, Response<IsMemberResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    handleResponseError(response, callback);
                }
            }

            @Override
            public void onFailure(Call<IsMemberResponse> call, Throwable t) {
                handleError(call, t, callback);
            }
        });
    }
}