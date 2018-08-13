package com.bakingapp.lacourt.bakingapp.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bakingapp.lacourt.bakingapp.model.RecipesResponse;
import com.google.gson.Gson;

public class SharedPreference {

    private static final String INGREDIENTS = "ingredients";
    private static final String RECIPE = "recipe";

    private static SharedPreferences.Editor editor;
    private static SharedPreferences sharedPref;

    private static void buildEditor(Context context) {

        if(sharedPref == null){
            sharedPref =  PreferenceManager.getDefaultSharedPreferences(context);
        }
        if(editor == null) {
            editor = sharedPref.edit();
        }

    }
    public static void saveIngredients(Context context, String ingredients) {

        buildEditor(context);

        editor.putString(INGREDIENTS, ingredients);
        editor.commit();
    }

    public static String getIngredients(Context context) {

        if(sharedPref == null){
            sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        }

        return sharedPref.getString(INGREDIENTS, "null");

    }

    public static void saveRecipe(Context context, RecipesResponse recipe) {

        buildEditor(context);
        Gson gson = new Gson();
        String json = gson.toJson(recipe);
        editor.putString(RECIPE, json);
        editor.apply();
    }

    public static RecipesResponse getRecipe(Context context) {

        if(sharedPref == null){
            sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        }

        Gson gson = new Gson();
        String json = sharedPref.getString(RECIPE, null);
        return gson.fromJson(json, RecipesResponse.class);

    }

}
