<a href="https://medium.com/@akndmr/nightmare-project-baking-app-c37eafee8013">Here</a>, I wrote a step by step guide on how to create Baking App easily.

<b>Project Overview</b></br>
You will productionize an app, taking it from a functional state to a production-ready state. This will involve finding and handling error cases, adding accessibility features, allowing for localization, adding a widget, and adding a library.

<b>Why this Project?</b></br>
As a working Android developer, you often have to create and implement apps where you are responsible for designing and planning the steps you need to take to create a production-ready app. Unlike Popular Movies where we gave you an implementation guide, it will be up to you to figure things out for the Baking App.

<b>What Will I Learn?</b></br>
In this project you will:

-Use MediaPlayer/Exoplayer to display videos.</br>
-Handle error cases in Android.</br>
-Add a widget to your app experience.</br>
-Leverage a third-party library in your app.</br>
-Use Fragments to create a responsive design that works on phones and tablets.</br>

<b>Libraries</b></br>
-<a href="https://github.com/google/ExoPlayer">ExoPlayer </a>  
-<a href="https://github.com/square/retrofit">Retrofit </a>  
-<a href="https://github.com/square/picasso">Picasso </a>  
-<a href="https://developer.android.com/training/testing/espresso/">Espresso </a>  
-<a href="https://github.com/JakeWharton/butterknife">ButterKnife </a> 

<b>Espresso Testing</b></br>
scrollTo, click, intended tests. Also there is another test for tablet view.

```
@Test
public void intentTest(){
    // Let the UI load completely first
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

    //Recyclerview scroll to position
    onView(ViewMatchers.withId(R.id.rv_recipes)).perform(RecyclerViewActions.scrollToPosition(4));

    //Perform Recyclerview click on item at position
    onView(withId(R.id.rv_recipes)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

    //Check if intent (RecipeActivity to RecipeDetailsActivity) has RECIPE_INTENT_EXTRA
    intended(hasExtraWithKey(ConstantsUtil.RECIPE_INTENT_EXTRA));

    //Perform click action on start cooking button
    onView(withId(R.id.btn_start_cooking)).perform(ViewActions.click());

    //Check if intent (RecipeDetailsActivity to CookingActivity) has RECIPE_INTENT_EXTRA
    intended(hasComponent(CookingActivity.class.getName()));
    }
```    
    
 <b>Screenshots</b></br>   
 Phone:</br>
 <img src="https://raw.githubusercontent.com/akndmr/Yummio/master/yummio_phone_ui0.PNG" alt="Yummio Phone Screenshot">
 <img src="https://raw.githubusercontent.com/akndmr/Yummio/master/yummio_phone_ui1.PNG" alt="Yummio Phone Screenshot">
 <img src="https://raw.githubusercontent.com/akndmr/Yummio/master/yummio_phone_ui2.PNG" alt="Yummio Phone Screenshot"></br>
 
 Tablet:</br>
 <img src="https://raw.githubusercontent.com/akndmr/Yummio/master/yummio_tablet_ui2.PNG" alt="Yummio Tablet Screenshot">
 </br>
 
Developed by Akın DEMİR for Udacity Android Nanodegree
