package io.github.akndmr.yummio.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import io.github.akndmr.yummio.model.Step;
import io.github.akndmr.yummio.ui.VideoPlayerFragment;

/**
 * Created by Akın DEMİR on 26.06.2018.
 */
public class StepPagerAdapter extends FragmentPagerAdapter {

    ArrayList<Step> mStepList;
    Bundle stepsBundle = new Bundle();

    public StepPagerAdapter(FragmentManager fm,  ArrayList<Step> stepList) {
        super(fm);
        this.mStepList = stepList;
    }

    @Override
    public Fragment getItem(int position) {
        VideoPlayerFragment videoPlayerFragment = new VideoPlayerFragment();
        stepsBundle.putParcelableArrayList("steps", mStepList);
        stepsBundle.putInt("page",position+1);
        stepsBundle.putBoolean("isLastPage",position == getCount() - 1);
        videoPlayerFragment.setArguments(stepsBundle);

        return videoPlayerFragment;
    }

    @Override
    public int getCount() {
        return mStepList.size();
    }
}
