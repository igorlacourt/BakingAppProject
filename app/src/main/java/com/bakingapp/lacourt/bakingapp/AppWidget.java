package com.bakingapp.lacourt.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.bakingapp.lacourt.bakingapp.preference.SharedPreference;

public class AppWidget extends AppWidgetProvider {

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
//
//        String ingredients = "NUTELLA PIE  -  8 ingredients:\n\n" +
//                "- Graham Cracker crumbs (2 CUP); \n\n" +
//                "- unsalted butter, melted (6 TBLSP); \n\n" +
//                "- granulated sugar (0.5 CUP); \n\n" +
//                "- salt (1.5 TSP); \n\n" +
//                "- vanilla (5 TBLSP); \n\n" +
//                "- Nutella or other chocolate-hazelnut spread (1 K); \n\n" +
//                "- Mascapone Cheese(room temperdature) (500 G); \n\n" +
//                "- heavy cream(cold) (1 CUP); \n\n" +
//                "- cream cheese(softened) (4 OZ); \n\n";

        // Create an Intent to launch StepsActivity when clicked
        Intent stepsActivityIntent = new Intent(context, StepsActivity.class);
        PendingIntent stepsActivityPendingIntent = PendingIntent.getActivity(context, 0, stepsActivityIntent, 0);

        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, 0);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        String ingredients = SharedPreference.getIngredients(context);

        if(ingredients == "null") {
            views.setTextViewText(R.id.appwidget_text,
                    "\n" + "Go to a recipe in the app and the ingredients will be displayed here."
                            );
            views.setOnClickPendingIntent(R.id.app_widget_layout, mainActivityPendingIntent);
        } else {
            views.setTextViewText(R.id.appwidget_text, ingredients);
            views.setOnClickPendingIntent(R.id.app_widget_layout, stepsActivityPendingIntent);
        }


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

