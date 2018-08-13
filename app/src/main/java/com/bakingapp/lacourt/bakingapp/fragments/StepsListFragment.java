package com.bakingapp.lacourt.bakingapp.fragments;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.bakingapp.lacourt.bakingapp.AppWidget;
import com.bakingapp.lacourt.bakingapp.R;
import com.bakingapp.lacourt.bakingapp.StepsActivity;
import com.bakingapp.lacourt.bakingapp.adapters.StepsAdapter;
import com.bakingapp.lacourt.bakingapp.model.RecipesResponse;

public class StepsListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private StepsAdapter stepsAdapter;

    private final String LIST_STATE_KEY = "listState";

    private String RECIPE = "recipe";

    private RecipesResponse recipe;

    private StepsAdapter.StepOnClickHandler stepOnClickHandler;
    private String ingredients = "";

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof StepsActivity) {
            stepOnClickHandler = ((StepsAdapter.StepOnClickHandler)context);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_steps_list, container, false);

        TextView tvIngredients = (TextView) rootView.findViewById(R.id.tv_frag_ingredients);

        if(savedInstanceState != null) {
            recipe = savedInstanceState.getParcelable(RECIPE);
        }

        Log.d("RECIPE", recipe.getName());

        for(int i = 0; i < recipe.getIngredients().size(); i++){
            Log.d("RECIPE", "MainActivity: " + recipe.getIngredients().get(i).getIngredient() + "\n");

            ingredients += "- " + recipe.getIngredients().get(i).getIngredient() + " "
                    + "(" + recipe.getIngredients().get(i).getQuantity()
                    + " " + recipe.getIngredients().get(i).getMeasure() + ");" + "\n\n";

        }

        tvIngredients.setText(ingredients);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.frag_steps_recycler_view);

        if (mRecyclerView != null) {
            mRecyclerView.setHasFixedSize(true);
        }

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);

        stepsAdapter = new StepsAdapter(stepOnClickHandler, recipe.getSteps());

        mRecyclerView.setAdapter(stepsAdapter);
        stepsAdapter.notifyDataSetChanged();

        if(listState != null){
            mRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }

        return rootView;
    }

//    public void setStepOnClickHandler(StepsAdapter.StepOnClickHandler stepOnClickHandler) {
//        this.stepOnClickHandler = stepOnClickHandler;
//    }

    public void setRecipe(RecipesResponse recipe) {
        this.recipe = recipe;
    }

    private Parcelable listState;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        listState = mRecyclerView.getLayoutManager().onSaveInstanceState();

        outState.putParcelable(LIST_STATE_KEY, listState);
        outState.putParcelable(RECIPE, recipe);
    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//
//        if(savedInstanceState != null){
//            listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
//        }
//    }

    @Override
    public void onPause() {
        super.onPause();
//        SharedPreference.saveIngredients(getContext(), ingredients);
        updateWidget();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        stepsAdapter.clear();
    }

    private void updateWidget() {
        Activity context = getActivity();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        ComponentName thisWidget = new ComponentName(context, AppWidget.class);

        remoteViews.setTextViewText(R.id.appwidget_text,  recipe.getName().toUpperCase()+ "  -  " + recipe.getIngredients().size() + " ingredients:" + "\n\n" + ingredients);

        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

}
