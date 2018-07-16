package io.github.akndmr.yummio.rest;

import java.util.ArrayList;

import io.github.akndmr.yummio.model.Recipe;
import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipeService {

    //Get list of recipes
    @GET("baking.json")
    Call<ArrayList<Recipe>> getRecipes();
}
