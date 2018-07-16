package io.github.akndmr.yummio.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.badoualy.stepperindicator.StepperIndicator;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.akndmr.yummio.R;
import io.github.akndmr.yummio.adapter.StepNumberAdapter;
import io.github.akndmr.yummio.model.Step;
import io.github.akndmr.yummio.utils.ConstantsUtil;
import io.github.akndmr.yummio.widget.YummioWidgetService;

public class CookingActivity extends AppCompatActivity implements StepperIndicator.OnStepClickListener, View.OnClickListener
        , StepNumberAdapter.OnStepClick {

    public static final String STEP_NUMBER_STATE = "step_number_state";

    @BindView(R.id.fl_player_container)
    FrameLayout mFragmentContainer;

    @BindView(R.id.si_step_view)
    StepperIndicator mStepperIndicator;

    @BindView(R.id.btn_next_step)
    Button mButtonNextStep;

    @BindView(R.id.btn_previous_step)
    Button mButtonPreviousStep;

    @Nullable
    @BindView(R.id.rv_recipe_steps)
    RecyclerView mRecyclerViewSteps;

    ArrayList<Step> mStepArrayList = new ArrayList<>();
    String mJsonResult;
    Bundle stepsBundle = new Bundle();
    String mVideoUri, mVideoDescription, mVideoShortDescription,mVideoThumbnail;
    int mVideoNumber = 0;
    StepNumberAdapter mStepNumberAdapter;
    LinearLayoutManager mLinearLayoutManager;
    boolean isFromWidget;

    private boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooking);
        ButterKnife.bind(this);

        mStepArrayList = getIntent().getParcelableArrayListExtra(ConstantsUtil.STEP_INTENT_EXTRA);
        mJsonResult = getIntent().getStringExtra(ConstantsUtil.JSON_RESULT_EXTRA);

        if(getIntent().getStringExtra(ConstantsUtil.WIDGET_EXTRA) != null){
            isFromWidget = true;
        }
        else{
            isFromWidget = false;
        }

        if(findViewById(R.id.cooking_tablet) != null){
            isTablet = true;
        }
        else{
            isTablet = false;
        }

        if(!isTablet){
            // Set step size
            mStepperIndicator.setStepCount(mStepArrayList.size()-1);
            // Set Stepper click listener
            mStepperIndicator.addOnStepClickListener(this);
            // Set button listeners
            mButtonNextStep.setOnClickListener(this);
            mButtonPreviousStep.setOnClickListener(this);
        }
        else{
            mStepNumberAdapter = new StepNumberAdapter(this,mStepArrayList, this);
            mLinearLayoutManager = new LinearLayoutManager(CookingActivity.this);
            mRecyclerViewSteps.setLayoutManager(mLinearLayoutManager);
            mRecyclerViewSteps.setAdapter(mStepNumberAdapter);
        }

        if(savedInstanceState != null){
            int stepNo = savedInstanceState.getInt(STEP_NUMBER_STATE,0);
            if(!isTablet){
                mStepperIndicator.setCurrentStep(stepNo);
            }
            playVideo(stepNo);
        }
        else{
            playVideo(mVideoNumber);
        }


        SharedPreferences.Editor editor = getSharedPreferences(ConstantsUtil.YUMMIO_SHARED_PREF, MODE_PRIVATE).edit();
        editor.putString(ConstantsUtil.JSON_RESULT_EXTRA, mJsonResult);
        editor.apply();

        //Start the widget service to update the widget
        YummioWidgetService.startActionOpenRecipe(this);
    }

    @Override
    public void onStepClicked(int step) {
        if(mVideoNumber == mStepArrayList.size()-1){
            //mVideoNumber = step;
            mStepperIndicator.setCurrentStep(step);
            Toast.makeText(this, "Cooking is over!", Toast.LENGTH_LONG).show();
        }
        else{
            mVideoNumber = step;
            mStepperIndicator.setCurrentStep(step);
            playVideo(mVideoNumber);
        }
    }

    @Override
    public void onClick(View v) {
        if(mVideoNumber == mStepArrayList.size()-1){
            mStepperIndicator.setCurrentStep(mVideoNumber);
            Toast.makeText(this, "Cooking is over!", Toast.LENGTH_LONG).show();
        }
       /* else if(mVideoNumber == 0){
            mStepperIndicator.setCurrentStep(mVideoNumber);
        }*/
        else{
            if(v.getId() == mButtonPreviousStep.getId()){
                mVideoNumber--;
                mStepperIndicator.setCurrentStep(mVideoNumber);
                playVideo(mVideoNumber);
            }
            else if(v.getId() == mButtonNextStep.getId()){
                mVideoNumber++;
                mStepperIndicator.setCurrentStep(mVideoNumber);
                playVideo(mVideoNumber);
            }
        }
    }

    public void playVideo(int videoNumber){
        mVideoUri = mStepArrayList.get(videoNumber).getVideoURL();
        mVideoDescription = mStepArrayList.get(videoNumber).getDescription();
        mVideoThumbnail = mStepArrayList.get(videoNumber).getThumbnailURL();
        mVideoShortDescription = mStepArrayList.get(videoNumber).getShortDescription();


        VideoPlayerFragment videoPlayerFragment = new VideoPlayerFragment();
        stepsBundle.putString(ConstantsUtil.STEP_VIDEO_URI, mVideoUri);
        stepsBundle.putString(ConstantsUtil.STEP_DESCRIPTION,mVideoDescription);
        stepsBundle.putString(ConstantsUtil.STEP_THUMBNAIL_URI, mVideoThumbnail);
        stepsBundle.putString(ConstantsUtil.STEP_SHORT_DESCRIPTION,mVideoShortDescription);
        videoPlayerFragment.setArguments(stepsBundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fl_player_container, videoPlayerFragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STEP_NUMBER_STATE, mVideoNumber);
    }

    @Override
    public void onStepClick(int position) {
        playVideo(position);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isFromWidget){
            Intent intent = new Intent(this, RecipeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
