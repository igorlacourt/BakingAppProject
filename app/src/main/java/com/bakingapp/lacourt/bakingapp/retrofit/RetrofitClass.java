package com.bakingapp.lacourt.bakingapp.retrofit;

import com.bakingapp.lacourt.bakingapp.model.RecipesResponse;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClass {

    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/";
    private static Retrofit retrofit;

    private static RetrofitService client;
    public static Call<List<RecipesResponse>> call;

    public static void setRetrofit() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .connectTimeout(2, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        //Retrofit Client
        client = retrofit.create(RetrofitService.class);
    }

    public static void requestRecipies() {
        call = client.getResponse();

    }

}
