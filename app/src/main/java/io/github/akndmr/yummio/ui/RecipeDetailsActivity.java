package io.github.akndmr.yummio.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.akndmr.yummio.R;
import io.github.akndmr.yummio.adapter.RecipeDetailsAdapter;
import io.github.akndmr.yummio.model.Ingredient;
import io.github.akndmr.yummio.model.Recipe;
import io.github.akndmr.yummio.model.Step;
import io.github.akndmr.yummio.utils.ConstantsUtil;
import io.github.akndmr.yummio.widget.YummioWidgetService;

public class RecipeDetailsActivity extends AppCompatActivity {

    public static final String RECIPE_LIST_STATE = "recipe_details_list";
    public static final String RECIPE_JSON_STATE = "recipe_json_list";

    @BindView(R.id.rv_recipe_ingredients)
    RecyclerView mRecyclerView;

    @BindView(R.id.btn_start_cooking)
    Button mButtonStartCooking;

    RecipeDetailsAdapter mRecipeDetailsAdapter;
    ArrayList<Recipe> mRecipeArrayList;
    ArrayList<Step> mStepArrayList;
    String mJsonResult;
    List<Ingredient> mIngredientList;

    private boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        ButterKnife.bind(this);

        // Up navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(findViewById(R.id.recipe_details_tablet) != null){
            isTablet = true;
        }
        else{
            isTablet = false;
        }


        if(getIntent().getStringExtra(ConstantsUtil.WIDGET_EXTRA) != null){
            SharedPreferences sharedpreferences =
                   getSharedPreferences(ConstantsUtil.YUMMIO_SHARED_PREF,MODE_PRIVATE);
            String jsonRecipe = sharedpreferences.getString(ConstantsUtil.JSON_RESULT_EXTRA, "");
            mJsonResult = jsonRecipe;

            Gson gson = new Gson();
            Recipe recipe = gson.fromJson(jsonRecipe, Recipe.class);

            mStepArrayList = (ArrayList<Step>) recipe.getSteps();
            mIngredientList = recipe.getIngredients();
        }
        else{

            // Check if state saved
            if(savedInstanceState != null)
            {
                mRecipeArrayList = savedInstanceState.getParcelableArrayList(RECIPE_LIST_STATE);
                mJsonResult = savedInstanceState.getString(RECIPE_JSON_STATE);
                mStepArrayList = (ArrayList<Step>) mRecipeArrayList.get(0).getSteps();
                mIngredientList = mRecipeArrayList.get(0).getIngredients();
            }
            else{
                // Get recipe from intent extra
                Intent recipeIntent = getIntent();
                mRecipeArrayList = recipeIntent.getParcelableArrayListExtra(ConstantsUtil.RECIPE_INTENT_EXTRA);
                mJsonResult = recipeIntent.getStringExtra(ConstantsUtil.JSON_RESULT_EXTRA);
                mStepArrayList = (ArrayList<Step>) mRecipeArrayList.get(0).getSteps();
                mIngredientList = mRecipeArrayList.get(0).getIngredients();
            }
        }

        mRecipeDetailsAdapter = new RecipeDetailsAdapter(this, mIngredientList);

        RecyclerView.LayoutManager mLayoutManager;
        if(isTablet){
            mLayoutManager = new GridLayoutManager(this, 2);
        }
        else{
            mLayoutManager = new LinearLayoutManager(this);
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mRecipeDetailsAdapter);


        mButtonStartCooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipeDetailsActivity.this, CookingActivity.class);
                intent.putParcelableArrayListExtra(ConstantsUtil.STEP_INTENT_EXTRA, mStepArrayList);
                intent.putExtra(ConstantsUtil.JSON_RESULT_EXTRA, mJsonResult);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RECIPE_LIST_STATE, mRecipeArrayList);
        outState.putString(RECIPE_JSON_STATE, mJsonResult);
    }
}
