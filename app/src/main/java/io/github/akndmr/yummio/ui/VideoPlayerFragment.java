package io.github.akndmr.yummio.ui;


import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
public class VideoPlayerFragment extends Fragment {

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

    String url = "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd974_-intro-creampie/-intro-creampie.mp4";
    Uri mp4VideoUri = Uri.parse(url);

    ArrayList<Step> mStepArrayList = new ArrayList<>();
    int stepNo = 0;
    Uri mVideoUri;
    String mVideoThumbnail, mVideoDescription;
    Bitmap mVideoThumbnailImage;

    public VideoPlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_video_player, container, false);
        ButterKnife.bind(this, root);
        if(getArguments() != null){

            mImageViewPlaceholder.setVisibility(View.GONE);
            mPlayerView.setVisibility(View.VISIBLE);

            if(getArguments().getString(ConstantsUtil.STEP_VIDEO_URI).equals("")){
                if(getArguments().getString(ConstantsUtil.STEP_THUMBNAIL_URI).equals(""))
                {
                    // If no video or thumbnail, use placeholder image
                    mPlayerView.setUseArtwork(true);
                    mImageViewPlaceholder.setVisibility(View.VISIBLE);
                    mPlayerView.setUseController(false);

                    /*
                    --Not working--
                    Bitmap placeHolder = BitmapFactory.decodeResource(getResources(),R.drawable.placeholder);
                    mPlayerView.setDefaultArtwork(placeHolder);*/
                }
                else{
                    mImageViewPlaceholder.setVisibility(View.GONE);
                    mPlayerView.setVisibility(View.VISIBLE);
                    mVideoThumbnail = getArguments().getString(ConstantsUtil.STEP_THUMBNAIL_URI);
                    mVideoThumbnailImage = ThumbnailUtils.createVideoThumbnail(mVideoThumbnail, MediaStore.Video.Thumbnails.MICRO_KIND);
                    mPlayerView.setUseArtwork(true);
                    mPlayerView.setDefaultArtwork(mVideoThumbnailImage);
                }
            }
            else{
                mVideoUri = Uri.parse(getArguments().getString(ConstantsUtil.STEP_VIDEO_URI));
            }
            mStepDescription.setText(getArguments().getString(ConstantsUtil.STEP_DESCRIPTION));
            mStepTitle.setText(getArguments().getString(ConstantsUtil.STEP_SHORT_DESCRIPTION));
            initializeVideoPlayer(mVideoUri);
        }
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }


    public static VideoPlayerFragment newInstance(int page, boolean isLast, ArrayList<Step> stepArrayList) {
        Bundle args = new Bundle();
        args.putParcelableArrayList("steps", stepArrayList);
        args.putInt("page", page);
        if (isLast)
            args.putBoolean("isLast", true);
        final VideoPlayerFragment fragment = new VideoPlayerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void initializeVideoPlayer(Uri videoUri){
        if(mSimpleExoPlayer == null){
            Log.d("KONTROL", "initializeVideoPlayer()");
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
            Log.d("KONTROL", "releasePlayer()");
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
            initializeVideoPlayer(mp4VideoUri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || mSimpleExoPlayer == null) {
            initializeVideoPlayer(mp4VideoUri);
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
}
