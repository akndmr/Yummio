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
import io.github.akndmr.yummio.utils.DialogUtil;
import io.github.akndmr.yummio.utils.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeActivity extends AppCompatActivity{

    private final String TAG = RecipeActivity.class.getSimpleName();
    public static final String RECIPE_JSON_STATE = "recipe_json_state";
    public static final String RECIPE_ARRAYLIST_STATE = "recipe_arraylist_state";

    RecipeService mRecipeService;
    RecipeAdapter recipeAdapter;
    String mJsonResult;
    ArrayList<Recipe> mRecipeArrayList = new ArrayList<>();

    @BindView(R.id.rv_recipes) RecyclerView mRecyclerViewRecipes;

    private boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe);
        ButterKnife.bind(this);

        if(findViewById(R.id.recipe_tablet) != null){
            isTablet = true;
        }
        else{
            isTablet = false;
        }

        if(savedInstanceState != null){
            mJsonResult = savedInstanceState.getString(RECIPE_JSON_STATE);
            mRecipeArrayList = savedInstanceState.getParcelableArrayList(RECIPE_ARRAYLIST_STATE);
            recipeAdapter = new RecipeAdapter(RecipeActivity.this, mRecipeArrayList, mJsonResult);
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
        else{
            if(NetworkUtil.isConnected(this)){
                mRecipeService = new RecipeClient().mRecipeService;
                new FetchRecipesAsync().execute();
            }
            else{
                DialogUtil.showDialogWithButtons(this,
                        R.drawable.yellow_cake,
                        getResources().getString(R.string.no_internet_connection));
            }

        }

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

                mRecipeArrayList = response.body();

                mJsonResult = new Gson().toJson(response.body());

                recipeAdapter = new RecipeAdapter(RecipeActivity.this, mRecipeArrayList, mJsonResult);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(RECIPE_JSON_STATE, mJsonResult);
        outState.putParcelableArrayList(RECIPE_ARRAYLIST_STATE, mRecipeArrayList);
    }
}
