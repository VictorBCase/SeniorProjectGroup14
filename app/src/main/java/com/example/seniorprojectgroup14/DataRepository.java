package com.example.seniorprojectgroup14;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class DataRepository {

    private static final String TAG = "DataRepository";

    /* You need an interface to communicate the result back to the Activity/UI
    public interface UserCallback {
        void onSuccess(List<User> users);
        void onError(String message);
    }

    public void fetchUsers(final UserCallback callback) {

        // 1. Get the service interface from the client
        ApiService service = RetrofitClient.getApiService();

        // 2. Create the Call object (using the getUsers() method defined earlier)
        Call<List<User>> call = service.getUsers();

        // 3. Execute the call asynchronously
        call.enqueue(new Callback<List<User>>() {

            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // SUCCESS: Pass the data back to the Activity via the callback
                    callback.onSuccess(response.body());
                } else {
                    // ERROR: Handle non-2xx response codes
                    String errorMessage = "Server error: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                // FAILURE: Handle network issues (connection refused, server down, etc.)
                String errorMessage = "Network failure: " + t.getMessage();
                Log.e(TAG, errorMessage);
                callback.onError(errorMessage);
            }
        });
    } */
}