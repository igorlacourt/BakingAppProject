package com.bakingapp.lacourt.bakingapp;

import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import com.bakingapp.lacourt.bakingapp.fragments.VideoFragment;
import com.bakingapp.lacourt.bakingapp.model.Step;

import java.util.ArrayList;

public class VideoActivity extends AppCompatActivity {

    private OnVideoClick callBack;

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        if(fragment instanceof VideoFragment) {
            callBack = (OnVideoClick) fragment;
        }

    }

    private ArrayList<Step> steps;
    private int position;

    public interface OnVideoClick {
        void onNext();
        void onPrevious();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        position = getIntent().getIntExtra("position", -1);

        steps = getIntent().getParcelableArrayListExtra("steps");

        if(savedInstanceState == null) {

            VideoFragment videoFragment = new VideoFragment();
            videoFragment.setPosition(position);
            videoFragment.setSteps(steps);

            FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.beginTransaction()
                    .add(R.id.video_player_container, videoFragment)
                    .commit();


        }

        setStepTitle();

        int orientation = getResources().getConfiguration().orientation;
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.next_and_previous_layout);

        if( orientation == Configuration.ORIENTATION_LANDSCAPE) {

            frameLayout.setVisibility(View.INVISIBLE);

        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {

            frameLayout.setVisibility(View.VISIBLE);

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

    private void setStepTitle() {
        if(position == 0) {
            setTitle("Recipe Introduction");
        } else {
            setTitle(steps.get(position).getShortDescription());
        }
    }

    public void onNextStepClick(View view) {

        if(position < (steps.size() - 1)) {

            position++;

            setVideoTitle();

        }

        callBack.onNext();

    }

    public void onPreviousStepClick(View view) {

        if(position > 0) {

            position--;

            setVideoTitle();

        }

        callBack.onPrevious();

    }

    private void setVideoTitle() {

        if(position == 0) {
            setTitle("Recipe Introduction");
        } else {
            setTitle(steps.get(position).getShortDescription());
        }

    }

}