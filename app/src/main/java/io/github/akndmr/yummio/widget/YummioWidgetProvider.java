package io.github.akndmr.yummio.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import io.github.akndmr.yummio.R;
import io.github.akndmr.yummio.ui.RecipeDetailsActivity;
import io.github.akndmr.yummio.utils.ConstantsUtil;

/**
 * Implementation of App Widget functionality.
 */
public class YummioWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, String jsonRecipeIngredients, int imgResId, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.yummio_widget_provider);

        Intent intent = new Intent(context, RecipeDetailsActivity.class);
        intent.putExtra(ConstantsUtil.WIDGET_EXTRA,"CAME_FROM_WIDGET");
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);

        if(jsonRecipeIngredients.equals("")){
            jsonRecipeIngredients = "No ingredients yet!";
        }

        views.setTextViewText(R.id.widget_ingredients, jsonRecipeIngredients);
        views.setImageViewResource(R.id.ivWidgetRecipeIcon, imgResId);

        // OnClick intent for textview
        views.setOnClickPendingIntent(R.id.widget_ingredients, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    // Gets called once created and on every update period
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        YummioWidgetService.startActionOpenRecipe(context);
    }

    public static void updateWidgetRecipe(Context context, String jsonRecipe , int imgResId, AppWidgetManager appWidgetManager,
                                          int[] appWidgetIds){
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, jsonRecipe, imgResId, appWidgetManager, appWidgetId);
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

