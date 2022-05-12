package com.example.journal;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface JournalAPI {
    @POST("add_user.php")
    Call<User> addUser(@Body User user);
}
