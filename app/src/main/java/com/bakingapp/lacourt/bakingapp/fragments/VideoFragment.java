package com.bakingapp.lacourt.bakingapp.fragments;

import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bakingapp.lacourt.bakingapp.AppStatus;
import com.bakingapp.lacourt.bakingapp.R;
import com.bakingapp.lacourt.bakingapp.VideoActivity;
import com.bakingapp.lacourt.bakingapp.model.RecipesResponse;
import com.bakingapp.lacourt.bakingapp.model.Step;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class VideoFragment extends Fragment implements VideoActivity.OnVideoClick {

    private ArrayList<Step> steps;
    private RecipesResponse recipe;

    private int position;

    private boolean mTwoPane = false;

    private Toast toast;

    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;

    private DataSource.Factory mediaDataSourceFactory;
    private DefaultTrackSelector trackSelector;
    private boolean shouldAutoPlay;
    private BandwidthMeter bandwidthMeter;

    private Dialog mFullScreenDialog;

    private String videoUrl = null;
    private String thumbnailUrl = null;

    //Video state
    private int mResumeWindow;
    private long mResumePosition;
    private boolean mExoPlayerFullscreen = false;

    private final String POSITION = "position";
    private final String STATE_RESUME_WINDOW = "resumeWindow";
    private final String STATE_RESUME_POSITION = "resumePosition";
    private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";
    private final String VIDEO_URL = "videoUrl";
    private static final String STEPS = "steps";
    private static final String RECIPE = "recipe";
    private final String TWO_PANE = "twoPane";
    private static final String THUMBNAIL = "thumbnailUrl";

    private RelativeLayout root;
    private TextView tvDescription;

    private View rootView;
    private TextView tvNoVideo;
    private ImageView imgThumbnail = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_video_player, container, false);

        tvDescription = new TextView(getActivity());

        //Video state
        if (savedInstanceState != null) {
            mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
            mExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
            position = savedInstanceState.getInt(POSITION);
            videoUrl = savedInstanceState.getString(VIDEO_URL);
            steps = savedInstanceState.getParcelableArrayList(STEPS);
            recipe = savedInstanceState.getParcelable(RECIPE);
            mTwoPane = savedInstanceState.getBoolean(TWO_PANE);
            thumbnailUrl = savedInstanceState.getString(THUMBNAIL);

        } else {
            videoUrl = steps.get(position).getVideoURL();
            thumbnailUrl = steps.get(position).getThumbnailURL();
        }

        tvNoVideo = (TextView) rootView.findViewById(R.id.tv_no_video);

        int width = tvVideoWidth();


        tvNoVideo.setWidth(width);
        tvNoVideo.setHeight(width / 3);

        tvNoVideo.setVisibility(View.VISIBLE);

        if(AppStatus.getInstance(getActivity().getApplication()).isOnline()){
            tvNoVideo.setText(R.string.step_has_no_video);
        } else {
            tvNoVideo.setText(R.string.no_internet_connection);
        }

        shouldAutoPlay = true;
        bandwidthMeter = new DefaultBandwidthMeter();
        mediaDataSourceFactory = new DefaultDataSourceFactory(getActivity(), Util.getUserAgent(getActivity(), "BakingAppProject"), (TransferListener<? super DataSource>) bandwidthMeter);

        root = (RelativeLayout) rootView.findViewById(R.id.root_video_fragment);

        return rootView;
    }

    private int tvVideoWidth(){

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.x;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Video state
        outState.putInt(STATE_RESUME_WINDOW, mResumeWindow);
        outState.putLong(STATE_RESUME_POSITION, mResumePosition);
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, mExoPlayerFullscreen);
        outState.putInt(POSITION, position);
        outState.putString(VIDEO_URL, videoUrl);
        outState.putString(THUMBNAIL, thumbnailUrl);
        outState.putParcelableArrayList(STEPS, steps);
        outState.putParcelable(RECIPE, recipe);
        outState.putBoolean(TWO_PANE, mTwoPane);

        super.onSaveInstanceState(outState);
    }

    private void initializePlayer() {

        if(AppStatus.getInstance(getActivity().getApplication()).isOnline()) {

            simpleExoPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.player_view);
            simpleExoPlayerView.setVisibility(View.VISIBLE);
            simpleExoPlayerView.requestFocus();

            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);

            trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

            player = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);

            simpleExoPlayerView.setPlayer(player);

            //Video state
            boolean haveResumePosition = mResumeWindow != C.INDEX_UNSET;
            if (haveResumePosition) {
                simpleExoPlayerView.getPlayer().seekTo(mResumeWindow, mResumePosition);
            }

            player.setPlayWhenReady(shouldAutoPlay);

            DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(videoUrl),
                    mediaDataSourceFactory, extractorsFactory, null, null);

            player.prepare(mediaSource);

            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && !videoUrl.isEmpty() && !mTwoPane){
                initFullscreenDialog();
                openFullscreenDialog();
            }

        }

        createDescriptionTextView();
    }

    private void initFullscreenDialog() {

        mFullScreenDialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (mExoPlayerFullscreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }

    private void openFullscreenDialog() {

        ((ViewGroup) simpleExoPlayerView.getParent()).removeView(simpleExoPlayerView);
        mFullScreenDialog.addContentView(simpleExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
    }

    private void closeFullscreenDialog() {
        ((ViewGroup) simpleExoPlayerView.getParent()).removeView(simpleExoPlayerView);
//        ((RelativeLayout) findViewById(R.id.root)).addView(simpleExoPlayerView);
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();
    }

    private void releasePlayer() {
        if (player != null) {
            shouldAutoPlay = player.getPlayWhenReady();
            player.release();
            player = null;
            trackSelector = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        //This line is for testing the thumbnail.
//        thumbnailUrl = "https://sites.google.com/site/intwalasgdsyhgrd/sign-in-error-android/android_man_mobile_.png";
//        videoUrl = "";

        if (Util.SDK_INT > 23) {

            if(!videoUrl.isEmpty()) {
                initializePlayer();

            }
            else if(!thumbnailUrl.isEmpty()){
                loadThumbnail();
            }
            else {
                createDescriptionTextView();
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Util.SDK_INT <= 23 || player == null) {

            if(!videoUrl.isEmpty()) {
                initializePlayer();
            }
            else if(!thumbnailUrl.isEmpty() && isThumbnailCorrect()) {
                loadThumbnail();

            }
            else {
                createDescriptionTextView();
            }

        } else {
            root.removeView(tvDescription);
            createDescriptionTextView();
        }

    }

    private boolean isThumbnailCorrect() {
        return thumbnailUrl.endsWith(".jpg") || thumbnailUrl.endsWith(".png") || thumbnailUrl.endsWith(".bmp");

    }

    private void loadThumbnail() {

        tvNoVideo.setVisibility(View.INVISIBLE);

        final ProgressBar loadingThumbnail = (ProgressBar)getActivity().findViewById(R.id.loading_thumbnail);
        loadingThumbnail.setVisibility(View.VISIBLE);

        imgThumbnail = (ImageView)getActivity().findViewById(R.id.img_thumbnail);
        imgThumbnail.setMaxHeight(tvVideoWidth()/3);

        Picasso.get()
                .load(thumbnailUrl)
                .into(imgThumbnail, new Callback() {
                    @Override
                    public void onSuccess() {
                        loadingThumbnail.setVisibility(View.INVISIBLE);
                        createDescriptionTextView();
                    }

                    @Override
                    public void onError(Exception e) {
                        loadingThumbnail.setVisibility(View.INVISIBLE);
                        tvNoVideo.setVisibility(View.VISIBLE);
                        tvNoVideo.setText(R.string.error_loading_image);
                        createDescriptionTextView();
                    }
                });

    }

    @Override
    public void onPause() {
        super.onPause();

        if (simpleExoPlayerView != null && simpleExoPlayerView.getPlayer() != null) {
            mResumeWindow = simpleExoPlayerView.getPlayer().getCurrentWindowIndex();
            mResumePosition = Math.max(0, simpleExoPlayerView.getPlayer().getContentPosition());

            simpleExoPlayerView.getPlayer().release();
        }

        if (mFullScreenDialog != null)
            mFullScreenDialog.dismiss();

        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        } else {
            createDescriptionTextView();
        }
    }

    private void createDescriptionTextView() {

        root.removeView(tvDescription);

        if(position == 0)
            tvDescription.setText(R.string.recipe_introduction);

        tvDescription.setText(steps.get(position).getDescription());
        tvDescription.setTextSize(getResources().getDimension(R.dimen.tv_description_size));
        tvDescription.setPadding(25, 25, 25, 25);
        tvDescription.setTextColor(getResources().getColor(R.color.tvVideoTextColor));

        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        if(simpleExoPlayerView != null) {
            p.addRule(RelativeLayout.BELOW, R.id.player_view);
        } else if (imgThumbnail != null){
            p.addRule(RelativeLayout.BELOW, R.id.img_thumbnail);
        } else {
            p.addRule(RelativeLayout.BELOW, R.id.tv_no_video);
        }

        tvDescription.setLayoutParams(p);

        root.addView(tvDescription);

    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }

    public void setRecipe(RecipesResponse recipe) {
        this.recipe = recipe;
    }

    @Override
    public void onNext() {

        if(position < (steps.size() - 1)){

            position++;

            tvDescription.setText(steps.get(position).getDescription());
            videoUrl = steps.get(position).getVideoURL();
            thumbnailUrl = steps.get(position).getThumbnailURL();

            //This line is for testing the thumbnail.
//            thumbnailUrl = "https://sites.google.com/site/intwalasgdsyhgrd/sign-in-error-android/android_man_mobile_.png";
//            videoUrl = "";

            releasePlayer();

            if(!videoUrl.isEmpty()) {
                initializePlayer();
            }
            else if(!thumbnailUrl.isEmpty()){
                loadThumbnail();
            }
            else if(simpleExoPlayerView != null) {
                 simpleExoPlayerView.setVisibility(View.INVISIBLE);
            }

            createDescriptionTextView();

        } else {
            if(toast != null){
                toast.cancel();
            }
            toast.makeText(getActivity(), getString(R.string.last_step), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPrevious() {

        if(position > 0) {

            position--;

            tvDescription.setText(steps.get(position).getDescription());
            videoUrl = steps.get(position).getVideoURL();
            thumbnailUrl = steps.get(position).getThumbnailURL();

            //This line is for testing the thumbnail.
//            thumbnailUrl = "https://sites.google.com/site/intwalasgdsyhgrd/sign-in-error-android/android_man_mobile_.png";
//            videoUrl = "";

            releasePlayer();

            if(!videoUrl.isEmpty()) {
                initializePlayer();
            }
            else if(!thumbnailUrl.isEmpty()){
                loadThumbnail();
            }
            else if(simpleExoPlayerView != null) {
                simpleExoPlayerView.setVisibility(View.INVISIBLE);
            }

            createDescriptionTextView();

        } else {

            if(toast != null){
                toast.cancel();
            }

            toast.makeText(getActivity(), getString(R.string.first_step), Toast.LENGTH_SHORT).show();
        }
    }

    public void setmTwoPane(boolean mTwoPane) {
        this.mTwoPane = mTwoPane;
    }
}
