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

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
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
    public static final String STEP_VIDEO_POSITION =  "step_video_position";
    public static final String STEP_PLAY_WHEN_READY =  "step_play_when_ready";
    public static final String STEP_PLAY_WINDOW_INDEX =  "step_play_window_index";
    public static final String STEP_SINGLE =  "step_single";

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
    Step mStep;
    Uri mVideoUri;
    String mVideoThumbnail, mVideoDescription;
    Bitmap mVideoThumbnailImage;
    int mVideoNumber;
    boolean mShouldPlayWhenReady = true;
    long mPlayerPosition;
    int mWindowIndex;


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
            mStep = savedInstanceState.getParcelable(STEP_SINGLE);
            mShouldPlayWhenReady = savedInstanceState.getBoolean(STEP_PLAY_WHEN_READY);
            mPlayerPosition = savedInstanceState.getLong(STEP_VIDEO_POSITION);
            mWindowIndex = savedInstanceState.getInt(STEP_PLAY_WINDOW_INDEX);
        }



        // If there is no saved state getArguments from CookingActivity
        else{
           if(getArguments() != null){

               mImageViewPlaceholder.setVisibility(View.GONE);
               mPlayerView.setVisibility(View.VISIBLE);

               // Get arguments
               mStep = getArguments().getParcelable(ConstantsUtil.STEP_SINGLE);


               if(mStep.getVideoURL().equals("")){
                   if(mStep.getThumbnailURL().equals("")){
                       // If no video or thumbnail, use placeholder image
                       mPlayerView.setUseArtwork(true);
                       mImageViewPlaceholder.setVisibility(View.VISIBLE);
                       mPlayerView.setUseController(false);
                   }
                   else{
                       mImageViewPlaceholder.setVisibility(View.GONE);
                       mPlayerView.setVisibility(View.VISIBLE);
                       mVideoThumbnail = mStep.getThumbnailURL();
                       mVideoThumbnailImage = ThumbnailUtils.createVideoThumbnail(mVideoThumbnail, MediaStore.Video.Thumbnails.MICRO_KIND);
                       mPlayerView.setUseArtwork(true);
                       mPlayerView.setDefaultArtwork(mVideoThumbnailImage);
                   }
               }
               else{
                   mVideoUri = Uri.parse(mStep.getVideoURL());
               }
           }
       }
        return root;
    }


    public void initializeVideoPlayer(Uri videoUri){

       // mStepDescription.setText(mStepArrayList.get(mVideoNumber).getDescription());
       // mStepTitle.setText(mStepArrayList.get(mVideoNumber).getShortDescription());

        mStepDescription.setText(mStep.getDescription());
        mStepTitle.setText(mStep.getShortDescription());


        if(mSimpleExoPlayer == null){

            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mSimpleExoPlayer =
                    ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);

            // Bind the player to the view.
            mPlayerView.setPlayer(mSimpleExoPlayer);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getActivity().getBaseContext(), getString(R.string.app_name));
            MediaSource mediaSource = new ExtractorMediaSource(videoUri,
                    new DefaultDataSourceFactory(getActivity(), userAgent),
                    new DefaultExtractorsFactory(),
                    null,
                    null);

            // Prepare the player with the source.
            mSimpleExoPlayer.prepare(mediaSource);

            if (mPlayerPosition != C.TIME_UNSET) {
                mSimpleExoPlayer.seekTo(mPlayerPosition);
            }
            mSimpleExoPlayer.setPlayWhenReady(mShouldPlayWhenReady);

        }
    }

    // Release player
    private void releasePlayer() {
        if (mSimpleExoPlayer != null) {
            updateStartPosition();
            mSimpleExoPlayer.stop();
            mSimpleExoPlayer.release();
            mSimpleExoPlayer = null;
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
        if(mSimpleExoPlayer != null){
            mSimpleExoPlayer.seekTo(mPlayerPosition);
            mSimpleExoPlayer.setPlayWhenReady(mShouldPlayWhenReady);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mSimpleExoPlayer != null){
            mPlayerPosition = mSimpleExoPlayer.getCurrentPosition();
            mShouldPlayWhenReady = mSimpleExoPlayer.getPlayWhenReady();
            if (Util.SDK_INT <= 23) {
                releasePlayer();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mSimpleExoPlayer != null){
            if (Util.SDK_INT > 23) {
                releasePlayer();
            }
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
        updateStartPosition();
        outState.putParcelable(STEP_SINGLE, mStep);
        outState.putLong(STEP_VIDEO_POSITION, mPlayerPosition);
        outState.putBoolean(STEP_PLAY_WHEN_READY, mShouldPlayWhenReady);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null){
            mStep = savedInstanceState.getParcelable(STEP_SINGLE);
            mPlayerPosition = savedInstanceState.getLong(STEP_VIDEO_POSITION);
            mShouldPlayWhenReady = savedInstanceState.getBoolean(STEP_PLAY_WHEN_READY);
        }
    }

    private void updateStartPosition() {
        if (mSimpleExoPlayer != null) {
            mShouldPlayWhenReady = mSimpleExoPlayer.getPlayWhenReady();
            mWindowIndex = mSimpleExoPlayer.getCurrentWindowIndex();
            mPlayerPosition = Math.max(0, mSimpleExoPlayer.getCurrentPosition());
        }
    }

    private void clearStartPosition() {
        mShouldPlayWhenReady = true;
        mWindowIndex = C.INDEX_UNSET;
        mPlayerPosition = C.TIME_UNSET;
    }

}
