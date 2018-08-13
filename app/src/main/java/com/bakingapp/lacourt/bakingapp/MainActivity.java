package com.bakingapp.lacourt.bakingapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.bakingapp.lacourt.bakingapp.preference.SharedPreference;
import com.bakingapp.lacourt.bakingapp.retrofit.RetrofitClass;
import com.bakingapp.lacourt.bakingapp.adapters.RecipeAdapter;
import com.bakingapp.lacourt.bakingapp.model.RecipesResponse;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.net.SocketTimeoutException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeOnClickHandler{

    private RecipeAdapter mRecipeAdapter;
    private RecipeAdapter.RecipeOnClickHandler recipeOnClickHandler;

    private LinearLayout lyPoorConnection;
    private RecyclerView recyclerView;

    private ShimmerFrameLayout shimmerViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setTitleTextColor(getResources().getColor(R.color.toolBarTitleColor));

        shimmerViewContainer = findViewById(R.id.shimmer_view_container);
        shimmerViewContainer.startShimmerAnimation();

        LinearLayout lyNoConnection = (LinearLayout) findViewById(R.id.ly_no_connection);
        lyPoorConnection = (LinearLayout) findViewById(R.id.ly_poor_connection);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);

        lyPoorConnection.setVisibility(View.INVISIBLE);
        lyNoConnection.setVisibility(View.INVISIBLE);
        appBarLayout.setExpanded(true, true);

        recipeOnClickHandler = this;

        if(AppStatus.getInstance(this).isOnline()){
            getOnlineResource();
        } else {
            appBarLayout.setExpanded(false, true);
//            progressBar.setVisibility(View.INVISIBLE);
            lyNoConnection.setVisibility(View.VISIBLE);
        }


        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
        }

        LinearLayoutManager mLayoutManager;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLayoutManager = new GridLayoutManager(this, numberOfColumns());
        } else {
            mLayoutManager = new LinearLayoutManager(this);
        }
        recyclerView.setLayoutManager(mLayoutManager);

    }

    private void getOnlineResource() {

        RetrofitClass.setRetrofit();

        RetrofitClass.requestRecipies();

        //Display data
        RetrofitClass.call.enqueue(new Callback<List<RecipesResponse>>() {

            @Override
            public void onResponse(Call<List<RecipesResponse>> call, Response<List<RecipesResponse>> response) {

                if (response.isSuccessful()) {

                    mRecipeAdapter = new RecipeAdapter(response.body(), recipeOnClickHandler);

                    recyclerView.setAdapter(mRecipeAdapter);
                    mRecipeAdapter.notifyDataSetChanged();

//                    progressBar.setVisibility(View.INVISIBLE);

                    Log.d("TAAG", "response body: " + response.body());

                    shimmerViewContainer.stopShimmerAnimation();
                    shimmerViewContainer.setVisibility(View.INVISIBLE);


                } else {
                    Log.d("TAAG", "response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<RecipesResponse>> call, Throwable t) {
                t.printStackTrace();
                Log.d("TAAG", "onFailure!" );

                t.printStackTrace();

                checkPoorConnection(t);


            }
        });
    }

    private int numberOfColumns() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int widthDivider = 600;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;

    }

    @Override
    public void onClick(RecipesResponse recipe) {

        SharedPreference.saveRecipe(this, recipe);

        Intent intent = new Intent(this, StepsActivity.class);
        startActivity(intent);

    }

    private void checkPoorConnection(Throwable t){
        //Checks poor connection
        if(t instanceof SocketTimeoutException){

            lyPoorConnection.setVisibility(View.VISIBLE);
//            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void onRetry(View view) {
        recreate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
