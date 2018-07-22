package io.github.akndmr.yummio.widget;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.google.gson.Gson;

import java.util.List;

import io.github.akndmr.yummio.model.Ingredient;
import io.github.akndmr.yummio.model.Recipe;
import io.github.akndmr.yummio.utils.ConstantsUtil;

/**
 * Created by Akın DEMİR on 12.07.2018.
 */
public class YummioWidgetService extends IntentService {

    public static final String ACTION_OPEN_RECIPE =
            "io.github.akndmr.yummio.widget.yummio_widget_service";


    public YummioWidgetService(String name) {
        super(name);
    }

    public YummioWidgetService() {
        super("YummioWidgetService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Yummio service",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

           startForeground(1, notification);
        }
    }

    @Override
    protected void onHandleIntent(@NonNull Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_OPEN_RECIPE.equals(action)) {
                handleActionOpenRecipe();
            }
        }
    }

    private void handleActionOpenRecipe() {
        SharedPreferences sharedpreferences =
                getSharedPreferences(ConstantsUtil.YUMMIO_SHARED_PREF,MODE_PRIVATE);
        String jsonRecipe = sharedpreferences.getString(ConstantsUtil.JSON_RESULT_EXTRA, "");
        StringBuilder stringBuilder = new StringBuilder();
        Gson gson = new Gson();
        Recipe recipe = gson.fromJson(jsonRecipe, Recipe.class);
        int id= recipe.getId();
        int imgResId = ConstantsUtil.recipeIcons[id-1];
        List<Ingredient> ingredientList = recipe.getIngredients();
        for(Ingredient ingredient : ingredientList){
            String quantity = String.valueOf(ingredient.getQuantity());
            String measure = ingredient.getMeasure();
            String ingredientName = ingredient.getIngredient();
            String line = quantity + " " + measure + " " + ingredientName;
            stringBuilder.append( line + "\n");
        }
        String ingredientsString = stringBuilder.toString();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, YummioWidgetProvider.class));
        YummioWidgetProvider.updateWidgetRecipe(this, ingredientsString, imgResId, appWidgetManager, appWidgetIds);
    }


    // Trigger the service to perform the action
    public static void startActionOpenRecipe(Context context) {
        Intent intent = new Intent(context, YummioWidgetService.class);
        intent.setAction(ACTION_OPEN_RECIPE);
        context.startService(intent);
    }

    // For Android O and above
    public static void startActionOpenRecipeO(Context context){
        Intent intent = new Intent(context, YummioWidgetService.class);
        intent.setAction(ACTION_OPEN_RECIPE);
        ContextCompat.startForegroundService(context,intent);
    }
}
