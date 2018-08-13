package com.bakingapp.lacourt.bakingapp;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.bakingapp.lacourt.bakingapp.preference.SharedPreference;
import com.bakingapp.lacourt.bakingapp.fragments.StepsListFragment;
import com.bakingapp.lacourt.bakingapp.fragments.VideoFragment;
import com.bakingapp.lacourt.bakingapp.model.RecipesResponse;
import com.bakingapp.lacourt.bakingapp.adapters.StepsAdapter.StepOnClickHandler;
import com.bakingapp.lacourt.bakingapp.model.Step;

import java.util.ArrayList;

public class StepsActivity extends AppCompatActivity implements StepOnClickHandler {

    private final String RECIPE = "recipe";
    private RecipesResponse recipe;
    private StepsListFragment mStepsListFragment;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState != null) {

            recipe = savedInstanceState.getParcelable(RECIPE);

        } else {

            recipe = SharedPreference.getRecipe(this);

            mStepsListFragment = new StepsListFragment();
            mStepsListFragment.setRecipe(recipe);

            FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.beginTransaction()
                    .add(R.id.steps_list_container, mStepsListFragment)
                    .commit();

            if( findViewById(R.id.video_player_container) != null ) {

                mTwoPane = true;

                ArrayList<Step> steps = new ArrayList<>();
                steps.addAll(recipe.getSteps());

                VideoFragment mVideoFragment = new VideoFragment();
                mVideoFragment.setPosition(0);
                mVideoFragment.setSteps(steps);
                mVideoFragment.setRecipe(recipe);
                mVideoFragment.setmTwoPane(mTwoPane);

                FragmentManager videoFragmentManager = getSupportFragmentManager();

                videoFragmentManager.beginTransaction()
                        .add(R.id.video_player_container, mVideoFragment)
                        .commit();

            } else {

                mTwoPane = false;

            }

        }

        setTitle(recipe.getName());

        Log.d("log", "" + recipe.getName());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(RECIPE, recipe);

    }

    @Override
    public void onClick(ArrayList<Step> steps, int position) {

        VideoFragment newVideoFragment = new VideoFragment();

        if(mTwoPane) {

            newVideoFragment.setPosition(position);
            newVideoFragment.setSteps(steps);
            newVideoFragment.setRecipe(recipe);
            newVideoFragment.setmTwoPane(mTwoPane);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.video_player_container, newVideoFragment)
                    .commit();

        } else {

            Intent intent = new Intent(this, VideoActivity.class);
            intent.putParcelableArrayListExtra("steps", steps);
            intent.putExtra("position", position);
            startActivity(intent);

        }

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
