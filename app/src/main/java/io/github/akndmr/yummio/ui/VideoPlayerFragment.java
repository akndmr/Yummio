package io.github.akndmr.yummio.ui;


import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.akndmr.yummio.R;
import io.github.akndmr.yummio.model.Step;
import io.github.akndmr.yummio.utils.ConstantsUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoPlayerFragment extends Fragment{

    public static final String STEP_LIST =  "step_list_fragment";
    public static final String STEP_NUMBER =  "step_number";
    public static final String STEP_LIST_ARGS =  "step_list_fragment_args";
    public static final String STEP_NUMBER_ARGS =  "step_number_args";

    @BindView(R.id.tv_step_title)
    TextView mStepTitle;

    @BindView(R.id.player_view)
    PlayerView mPlayerView;

    @BindView(R.id.tv_step_description)
    TextView mStepDescription;

    @BindView(R.id.iv_video_placeholder)
    ImageView mImageViewPlaceholder;

    SimpleExoPlayer mSimpleExoPlayer;
    DefaultBandwidthMeter bandwidthMeter;
    TrackSelection.Factory videoTrackSelectionFactory;
    TrackSelector trackSelector;
    DataSource.Factory dataSourceFactory;
    MediaSource videoSource;


    ArrayList<Step> mStepArrayList = new ArrayList<>();
    Uri mVideoUri;
    String mVideoThumbnail, mVideoDescription;
    Bitmap mVideoThumbnailImage;
    int mVideoNumber;


    public VideoPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_video_player, container, false);
        ButterKnife.bind(this, root);


        // Check if there is any state saved
        if(savedInstanceState != null){
            mStepArrayList = savedInstanceState.getParcelableArrayList(STEP_LIST);
            mVideoNumber = savedInstanceState.getInt(STEP_NUMBER);
        }


        // If there is no saved state getArguments from CookingActivity
        else{
           if(getArguments() != null){

               mImageViewPlaceholder.setVisibility(View.GONE);
               mPlayerView.setVisibility(View.VISIBLE);

               // Get arguments
               mStepArrayList = getArguments().getParcelableArrayList(ConstantsUtil.STEP_ARRAYLIST);
               mVideoNumber = getArguments().getInt(ConstantsUtil.STEP_NUMBER);

               if(mStepArrayList.get(mVideoNumber).getVideoURL().equals("")){
                   if(mStepArrayList.get(mVideoNumber).getThumbnailURL().equals(""))
                   {
                       // If no video or thumbnail, use placeholder image
                       mPlayerView.setUseArtwork(true);
                       mImageViewPlaceholder.setVisibility(View.VISIBLE);
                       mPlayerView.setUseController(false);
                   }
                   else{
                       mImageViewPlaceholder.setVisibility(View.GONE);
                       mPlayerView.setVisibility(View.VISIBLE);
                       mVideoThumbnail = mStepArrayList.get(mVideoNumber).getThumbnailURL();
                       mVideoThumbnailImage = ThumbnailUtils.createVideoThumbnail(mVideoThumbnail, MediaStore.Video.Thumbnails.MICRO_KIND);
                       mPlayerView.setUseArtwork(true);
                       mPlayerView.setDefaultArtwork(mVideoThumbnailImage);
                   }
               }
               else{
                   mVideoUri = Uri.parse(mStepArrayList.get(mVideoNumber).getVideoURL());
               }
           }
           // Start player
           initializeVideoPlayer(mVideoUri);
       }
        return root;
    }


    public void initializeVideoPlayer(Uri videoUri){

        mStepDescription.setText(mStepArrayList.get(mVideoNumber).getDescription());
        mStepTitle.setText(mStepArrayList.get(mVideoNumber).getShortDescription());

        if(mSimpleExoPlayer == null){
            // 1. Create a default TrackSelector
            bandwidthMeter = new DefaultBandwidthMeter();
            videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

            // 2. Create the player
            mSimpleExoPlayer =
                    ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);

            // Bind the player to the view.
            mPlayerView.setPlayer(mSimpleExoPlayer);

            // Produces DataSource instances through which media data is loaded.
            dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                    Util.getUserAgent(getContext(), "Yummio"), bandwidthMeter);

            // This is the MediaSource representing the media to be played.
            videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(videoUri);
            // Prepare the player with the source.
            mSimpleExoPlayer.prepare(videoSource);
        }
    }

    // Release player
    private void releasePlayer() {
        if (mSimpleExoPlayer != null) {
            mSimpleExoPlayer.stop();
            mSimpleExoPlayer.release();
            mSimpleExoPlayer = null;
            dataSourceFactory = null;
            videoSource = null;
            trackSelector = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializeVideoPlayer(mVideoUri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || mSimpleExoPlayer == null) {
            initializeVideoPlayer(mVideoUri);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mSimpleExoPlayer!=null) {
           releasePlayer();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STEP_LIST, mStepArrayList);
        outState.putInt(STEP_NUMBER, mVideoNumber);
    }
}
