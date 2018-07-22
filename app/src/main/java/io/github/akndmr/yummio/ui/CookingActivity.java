package io.github.akndmr.yummio.ui;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.akndmr.yummio.R;
import io.github.akndmr.yummio.adapter.StepNumberAdapter;
import io.github.akndmr.yummio.model.Step;
import io.github.akndmr.yummio.utils.ConstantsUtil;

public class CookingActivity extends AppCompatActivity  implements View.OnClickListener
        , StepNumberAdapter.OnStepClick{

    public static final String STEP_LIST_STATE = "step_list_state";
    public static final String STEP_NUMBER_STATE = "step_number_state";
    public static final String STEP_LIST_JSON_STATE = "step_list_json_state";
    private boolean isTablet;
    private int mVideoNumber = 0;

    @BindView(R.id.fl_player_container)
    FrameLayout mFragmentContainer;

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
    boolean isFromWidget;
    StepNumberAdapter mStepNumberAdapter;
    LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooking);
        ButterKnife.bind(this);

        // Up navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Check if device is a tablet
        if(findViewById(R.id.cooking_tablet) != null){
            isTablet = true;
        }
        else{
            isTablet = false;
        }

        if(savedInstanceState != null){
            mStepArrayList = savedInstanceState.getParcelableArrayList(STEP_LIST_STATE);
            mJsonResult = savedInstanceState.getString(STEP_LIST_JSON_STATE);
            mVideoNumber = savedInstanceState.getInt(STEP_NUMBER_STATE);
        }
        else{
            mStepArrayList = getIntent().getParcelableArrayListExtra(ConstantsUtil.STEP_INTENT_EXTRA);
            mJsonResult = getIntent().getStringExtra(ConstantsUtil.JSON_RESULT_EXTRA);
        }

        if(getIntent().getStringExtra(ConstantsUtil.WIDGET_EXTRA) != null){
            isFromWidget = true;
        }
        else{
            isFromWidget = false;
        }

        handleUiForDevice();
        playVideo(mStepArrayList.get(mVideoNumber));
    }

    // Initialize fragment
    public void playVideo(Step step){
        Step videoStep = mStepArrayList.get(mVideoNumber);
        VideoPlayerFragment videoPlayerFragment = new VideoPlayerFragment();
        stepsBundle.putParcelable(ConstantsUtil.STEP_SINGLE, videoStep);
        videoPlayerFragment.setArguments(stepsBundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fl_player_container, videoPlayerFragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STEP_LIST_STATE, mStepArrayList);
        outState.putString(STEP_LIST_JSON_STATE, mJsonResult);
        outState.putInt(STEP_NUMBER_STATE, mVideoNumber);
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
    public void onClick(View v) {
        //If it's last step show cooking is over
        if(mVideoNumber == mStepArrayList.size()-1){
            Toast.makeText(this, R.string.cooking_is_over, Toast.LENGTH_SHORT).show();
        }
        else{
            if(v.getId() == mButtonPreviousStep.getId()){
                mVideoNumber--;
                if(mVideoNumber < 0){
                    Toast.makeText(this, R.string.you_better_see_next_step, Toast.LENGTH_SHORT).show();
                }
                else
                    playVideo(mStepArrayList.get(mVideoNumber));
            }
            else if(v.getId() == mButtonNextStep.getId()){
                mVideoNumber++;
                playVideo(mStepArrayList.get(mVideoNumber));
            }
        }
    }


    @Override
    public void onStepClick(int position) {
        mVideoNumber = position;
        playVideo(mStepArrayList.get(position));
    }

    public void handleUiForDevice(){
        if(!isTablet){
            // Set button listeners
            mButtonNextStep.setOnClickListener(this);
            mButtonPreviousStep.setOnClickListener(this);
        }
        else{//Tablet view
            mStepNumberAdapter = new StepNumberAdapter(this,mStepArrayList, this, mVideoNumber);
            mLinearLayoutManager = new LinearLayoutManager(this);
            mRecyclerViewSteps.setLayoutManager(mLinearLayoutManager);
            mRecyclerViewSteps.setAdapter(mStepNumberAdapter);
        }
    }

}
