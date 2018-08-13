package com.bakingapp.lacourt.bakingapp.retrofit;

import com.bakingapp.lacourt.bakingapp.model.RecipesResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by igor on 16/04/2018.
 */

public interface RetrofitService {

    @GET("2017/May/59121517_baking/baking.json")
    Call<List<RecipesResponse>> getResponse();


}
