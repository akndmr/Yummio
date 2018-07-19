package io.github.akndmr.yummio.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.akndmr.yummio.R;
import io.github.akndmr.yummio.adapter.RecipeAdapter;
import io.github.akndmr.yummio.model.Recipe;
import io.github.akndmr.yummio.rest.RecipeClient;
import io.github.akndmr.yummio.rest.RecipeService;
import io.github.akndmr.yummio.utils.ConstantsUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeActivity extends AppCompatActivity{

    private final String TAG = RecipeActivity.class.getSimpleName();
    RecipeService mRecipeService;
    RecipeAdapter recipeAdapter;
    String mJsonResult;
    @BindView(R.id.rv_recipes) RecyclerView mRecyclerViewRecipes;

    private boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().getStringExtra(ConstantsUtil.WIDGET_EXTRA) != null){
            Intent intent = new Intent(this, RecipeDetailsActivity.class);
            intent.putExtra(ConstantsUtil.WIDGET_EXTRA, "CAME_FROM_WIDGET");
            startActivity(intent);
        }
        setContentView(R.layout.activity_recipe);
        ButterKnife.bind(this);

        if(findViewById(R.id.recipe_tablet) != null){
            isTablet = true;
        }
        else{
            isTablet = false;
        }

        mRecipeService = new RecipeClient().mRecipeService;
        new FetchRecipesAsync().execute();
    }


    private class FetchRecipesAsync extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            fetchRecipes();
            return null;
        }
    }

    // Fetch recipes
    private void fetchRecipes() {
        Call<ArrayList<Recipe>> call = mRecipeService.getRecipes();

        call.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {

                ArrayList<Recipe> recipe = response.body();

                mJsonResult = new Gson().toJson(response.body());

                recipeAdapter = new RecipeAdapter(RecipeActivity.this, recipe,mJsonResult);
                RecyclerView.LayoutManager mLayoutManager;
                if(isTablet){
                    mLayoutManager = new GridLayoutManager(RecipeActivity.this, 2);
                }
                else{
                    mLayoutManager = new LinearLayoutManager(RecipeActivity.this);
                }

                mRecyclerViewRecipes.setLayoutManager(mLayoutManager);
                mRecyclerViewRecipes.setAdapter(recipeAdapter);

            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {
                Log.d(TAG, t.toString());
            }
        });
    }

}
