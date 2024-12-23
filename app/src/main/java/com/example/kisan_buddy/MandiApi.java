package com.example.kisan_buddy;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface MandiApi {
    @GET("mandi_locations")
    Call<List<Mandi>> getMandis();
}
