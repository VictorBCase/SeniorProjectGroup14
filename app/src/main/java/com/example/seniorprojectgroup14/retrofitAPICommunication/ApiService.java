package com.example.seniorprojectgroup14.retrofitAPICommunication;
import com.example.seniorprojectgroup14.plainOldJavaObjects.*;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
// Import your new POJO classes

public interface ApiService {

    @POST("register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("scanToJoin")
    Call<ScanToJoinResponse> scanToJoin(@Body ScanToJoinRequest request);

    @POST("scanToLeave")
    Call<ScanToLeaveResponse> scanToLeave(@Body ScanToLeaveRequest request);

    @POST("viewWaitTime")
    Call<ViewWaitTimeResponse> viewWaitTime(@Body ViewWaitTimeRequest request);

    @POST("viewQueue")
    Call<ViewQueueResponse> viewQueue(@Body ViewQueueRequest request);

    @POST("userPosition")
    Call<UserPositionResponse> userPosition(@Body UserPositionRequest request);

    @POST("createGroup")
    Call<CreateGroupResponse> createGroup(@Body CreateGroupRequest request);

    @POST("addMemberToGroup")
    Call<AddMemberToGroupResponse> addMemberToGroup(@Body AddMemberToGroupRequest request);

    @POST("removeMemberFromGroup")
    Call<RemoveMemberFromGroupResponse> removeMemberFromGroup(@Body RemoveMemberFromGroupRequest request);

    @POST("scanToJoinGroup")
    Call<ScanToJoinGroupResponse> scanToJoinGroup(@Body ScanToJoinGroupRequest request);

    @POST("scanToLeaveGroup")
    Call<ScanToLeaveGroupResponse> scanToLeaveGroup(@Body ScanToLeaveGroupRequest request);

    @POST("isMember")
    Call<IsMemberResponse> isMember(@Body IsMemberRequest request);

    @POST("getRideName")
    Call<RideNameResponse> getRideName(@Body RideNameRequest request);
}