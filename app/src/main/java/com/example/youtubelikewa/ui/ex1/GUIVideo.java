package com.example.youtubelikewa.ui.ex1;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.youtubelikewa.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class GUIVideo {
    private AppCompatActivity activity;
    private GUIVideo coffeeVideo;
    private static GUIVideo instance;
    private boolean isFullScreenVid = false;

    public GUIVideo(AppCompatActivity activity) {
        this.activity = activity;
        coffeeVideo = this;
    }


    public static GUIVideo getInstance(AppCompatActivity activity) {
        if (instance == null) {
            instance = new GUIVideo(activity);
        }
        return instance;
    }

    public GUIVideo setVideoStartSecond(float videoStartSecond) {
        this.videoStartSecond = videoStartSecond;
        if (youTubePlayer != null) {
            youTubePlayer.seekTo(videoStartSecond);
        }
        return coffeeVideo;
    }

    public void close() {
        if (popupWindow != null)
            popupWindow.dismiss();
    }

    private float videoStartSecond = 0f;
    private int screenHeight;
    private int screenWidht;
    private PopupWindow popupWindow;
    private View popupView;
    private String TAG = "Basicodemine";
    private YouTubePlayerView playerView;
    private int popupWidht;
    private int popupHeight;
    private FrameLayout draggablePanel;
    private ImageView playPauseButton;
    private ProgressBar progressBar;
    private YouTubePlayer youTubePlayer = null;
    private SeekBar seekBar;
    private boolean isSetupNeeded = true;
    private boolean fullScreenToggleEnabled = false;
    private ImageView ytbPnlExpand;
    private ImageView ytbPnlClose;
    private ImageView ytbPnlFull;
    private int positionX = 0;
    private int positionY = 100;
    private Handler visibleUIHandler;
    private Runnable visibleUIRunnable;
    private FLY_GRAVITY FlyGravity;
    public static String apiKey = "";

    public enum FLOAT_MOVE {
        STICKY, FREE
    }

    public enum FLY_GRAVITY {
        TOP, BOTTOM
    }

    public FLY_GRAVITY getFlyGravity() {
        return FlyGravity;
    }

    public GUIVideo setFlyGravity(FLY_GRAVITY flyGravity) {
        FlyGravity = flyGravity;
        return coffeeVideo;
    }

    public static FLOAT_MOVE FloatMode = FLOAT_MOVE.STICKY;

    public FLOAT_MOVE getFloatMode() {
        return FloatMode;
    }

    public GUIVideo setFloatMode(FLOAT_MOVE floatMode) {
        FloatMode = floatMode;
        return coffeeVideo;
    }


    @SuppressLint("ClickableViewAccessibility")
    public GUIVideo videoSetup(String youtubeVideoId) {
        fullScreenToggleEnabled = true;
        if (isSetupNeeded) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getRealSize(size);
            screenHeight = size.y;
            screenWidht = size.x;
            setPopupWidht(getScreenWidht() - 250);
            setPopupHeight(((getScreenWidht() - 250) / 16) * 9);
            LayoutInflater layoutInflater = LayoutInflater.from(activity);
            popupView = layoutInflater.inflate(R.layout.gui_layout, null);
            setDefaultPopupWindow();
            setUpViews(popupView);
            draggablePanel.setOnTouchListener(new View.OnTouchListener() {
                int orgX, orgY;
                int offsetX, offsetY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            orgX = (int) event.getX();
                            orgY = (int) event.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            offsetX = (int) event.getRawX() - orgX;
                            offsetY = (int) event.getRawY() - orgY;

                            if (ytbPnlClose.getVisibility() == INVISIBLE)
                                triggerVisibleUIEvent();
                            else if (!isFullScreenVid)
                                popupWindow.update(offsetX, offsetY, -1, -1, true);
                            break;
                        case MotionEvent.ACTION_UP:
                            offsetX = (int) event.getRawX() - (int) event.getX();
                            offsetY = (int) event.getRawY() - (int) event.getY();

                            if (getFloatMode() == FLOAT_MOVE.STICKY)
                                if (ytbPnlClose.getVisibility() == INVISIBLE)
                                    triggerVisibleUIEvent();
                                else if (!isFullScreenVid)
                                    repositionScript(popupWindow, offsetX, offsetY);
                            break;
                    }
                    return true;
                }
            });
            isSetupNeeded = false;
        }
        setPlayerView(youtubeVideoId);
        setFullScreenListener(youtubeVideoId);
        return coffeeVideo;
    }

    private void setFullScreenListener(String youtubeVideoId) {
        ytbPnlFull.setOnClickListener(v -> {
            /*Intent i = new Intent(activity, FullScreenActivity.class);
            i.putExtra("videoId", youtubeVideoId);
            i.putExtra("startSecond", videoStartSecond);
            activity.startActivity(i);*/
            if (isFullScreenVid) {
                expandVideoView(popupWindow, 1);
            } else {
                expandVideoView(popupWindow, 2);
            }
            isFullScreenVid = !isFullScreenVid;
        });
    }

    private void triggerVisibleUIEvent() {
        visibleAndEnableUI();
        visibleUIHandler.removeCallbacks(null);
        visibleUIHandler.postDelayed(visibleUIRunnable, 1000);
    }

    private void visibleAndEnableUI() {
        ytbPnlClose.setVisibility(VISIBLE);
        ytbPnlExpand.setVisibility(VISIBLE);

        if (fullScreenToggleEnabled) {
            ytbPnlFull.setVisibility(VISIBLE);
            ytbPnlFull.setEnabled(true);
        }

        playPauseButton.setVisibility(VISIBLE);
        ytbPnlClose.setEnabled(true);
        ytbPnlExpand.setEnabled(true);
        playPauseButton.setEnabled(true);
    }

    private void repositionScript(final PopupWindow popupWindow, final int defX, final int defY) {
        TypedValue tv = new TypedValue();
        int actionBarHeight;
        if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
        } else {
            actionBarHeight = 150;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        int mDuration = 300;
        animator.setDuration(mDuration);
        final int endX = (getScreenWidht() - getPopupWidht()) / 2;
        final int endY = (defY < getScreenHeight() / 2) ? (actionBarHeight) : (getScreenHeight() - (actionBarHeight + getPopupHeight()));
        positionX = endX;
        positionY = endY;
        animator.addUpdateListener(animation -> popupWindow.update((int) (defX + (endX - defX) * (float) animation.getAnimatedValue()), (int) (defY + (endY - defY) * (float) animation.getAnimatedValue()), -1, -1, true));
        animator.start();
    }


    public void show(View targetView) {
        positionX = (getScreenWidht() - getPopupWidht()) / 2;
        if (getFlyGravity() == FLY_GRAVITY.TOP)
            popupWindow.showAtLocation(targetView, Gravity.NO_GRAVITY, (getScreenWidht() - getPopupWidht()) / 2, 100);
        else
            popupWindow.showAtLocation(targetView, Gravity.NO_GRAVITY, (getScreenWidht() - getPopupWidht()) / 2, getScreenHeight() - getPopupHeight() - 100);
    }


    private void expandVideoView(PopupWindow popupWindow, int type) {
        triggerVisibleUIEvent();
        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.setDuration(200);

        AtomicInteger latestH = new AtomicInteger();
        AtomicInteger latestW = new AtomicInteger();

        anim.addUpdateListener(animation -> {
            if (type == 1) {
                latestH.set((int) (((getPopupWidht() - (200 * (float) animation.getAnimatedValue())) / 16) * 9));
                latestW.set((int) (getPopupWidht() - (200 * (float) animation.getAnimatedValue())));
                popupWindow.update((getScreenWidht() - latestW.intValue()) / 2, positionY, latestW.intValue(), latestH.intValue());
            } else if (type == 0) {
                latestH.set((int) (((getPopupWidht() + (200 * (float) animation.getAnimatedValue())) / 16) * 9));
                latestW.set((int) (getPopupWidht() + (200 * (float) animation.getAnimatedValue())));
                popupWindow.update((getScreenWidht() - latestW.intValue()) / 2, positionY, latestW.intValue(), latestH.intValue());
            } else if (type == 2) {
                latestH.set((int) (((getPopupWidht() + (200 * (float) animation.getAnimatedValue())) / 16) * 9));
                latestW.set((int) (getPopupWidht() + (200 * (float) animation.getAnimatedValue())));

                Resources r = activity.getResources();
                int yPx = Math.round(TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 24, r.getDisplayMetrics()));

                //todo jika kamu ingin menjadikan vid screen fullscreen, edit aja code dibawah ini ya
                //todo set aja posisi y = 0, dan size y = getscreenHeight
                //todo rumus dibawah ini hanya keisengan ghozi aja itumah abaikan :p
                popupWindow.update(0, yPx, getScreenWidht(),
                        getScreenHeight() - (getNavBarSize() + yPx));
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setPopupHeight(latestH.intValue());
                setPopupWidht(latestW.intValue());
            }
        });
        anim.start();
    }

    private int getNavBarSize() {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private void setPlayerView(String youtubeVideoId) {
        activity.getLifecycle().addObserver(playerView);
        playerView.getPlayerUiController().showUi(false);
        playerView.initialize(new YouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(youtubeVideoId, videoStartSecond);
            }

            @Override
            public void onStateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerState state) {
                playerView.setTag(state.name());
                switch (state) {
                    case PLAYING:
                        Log.d(TAG, "video state " + state.name());
                        progressBar.setVisibility(GONE);
                        playPauseButton.setVisibility(VISIBLE);
                        break;
                    case UNKNOWN:
                    case VIDEO_CUED:
                        Log.d(TAG, "video state " + state.name());
                        break;
                    case BUFFERING:
                        progressBar.setVisibility(VISIBLE);
                        playPauseButton.setVisibility(GONE);
                        Log.d(TAG, "video state " + state.name());
                        break;
                    case ENDED:
                        Log.d(TAG, "video state " + state.name());
                        break;
                    case PAUSED:
                        Log.d(TAG, "video state " + state.name());
                        break;

                }
            }

            @Override
            public void onPlaybackQualityChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlaybackQuality playbackQuality) {

            }

            @Override
            public void onPlaybackRateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlaybackRate playbackRate) {

            }

            @Override
            public void onError(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerError playerError) {

            }

            @Override
            public void onCurrentSecond(@NotNull YouTubePlayer youTubePlayer, float v) {
                seekBar.setProgress((int) v);
            }

            @Override
            public void onVideoDuration(@NotNull YouTubePlayer youTubePlayer, float v) {
                seekBar.setProgress(0);
                seekBar.setMax((int) v);
            }

            @Override
            public void onVideoLoadedFraction(@NotNull YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoId(@NotNull YouTubePlayer youTubePlayer, @NotNull String s) {

            }

            @Override
            public void onApiChange(@NotNull YouTubePlayer youTubePlayer) {

            }
        }, true);
    }


    private void setDefaultPopupWindow() {
        popupWindow = new PopupWindow(activity.getBaseContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(20);
        }
        popupWindow.setContentView(popupView);
        popupWindow.setWidth(getPopupWidht());
        popupWindow.setHeight(getPopupHeight());
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setClippingEnabled(false);
        popupWindow.setAnimationStyle(R.style.Animation);
    }

    private void setFullScreenSize() {
        popupWindow = new PopupWindow(activity.getBaseContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(20);
        }
        popupWindow.setContentView(popupView);
        popupWindow.setWidth(FrameLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(FrameLayout.LayoutParams.MATCH_PARENT);
    }

    private void setUpViews(View v) {
        ytbPnlClose = v.findViewById(R.id.ytb_pnl_close);
        ytbPnlFull = v.findViewById(R.id.ytb_pnl_full);
        ytbPnlExpand = v.findViewById(R.id.ytb_pnl_expand);
        playerView = v.findViewById(R.id.youtube_player);
        playerView.setEnableAutomaticInitialization(false);
        draggablePanel = v.findViewById(R.id.draggablePanel);
        playPauseButton = v.findViewById(R.id.ytb_play_pause_button);
        progressBar = v.findViewById(R.id.ytb_progressbar);
        seekBar = v.findViewById(R.id.ytb_seek_bar);
        ytbPnlFull.setVisibility(fullScreenToggleEnabled ? VISIBLE : GONE);
        setupUIListeners();
    }

    private void setupUIListeners() {
        visibleUIHandler = new Handler(Looper.myLooper());
        visibleUIRunnable = () -> {

            if (ytbPnlExpand.getVisibility() == VISIBLE) {
                Animation fadeOut = new AlphaAnimation(1, 0);
                fadeOut.setInterpolator(new AccelerateInterpolator());
                fadeOut.setStartOffset(1000);
                fadeOut.setDuration(200);
                ytbPnlExpand.startAnimation(fadeOut);
                ytbPnlClose.startAnimation(fadeOut);
                playPauseButton.startAnimation(fadeOut);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ytbPnlClose.setVisibility(View.INVISIBLE);
                        ytbPnlExpand.setVisibility(View.INVISIBLE);
                        playPauseButton.setVisibility(View.INVISIBLE);

                        if (fullScreenToggleEnabled) {
                            ytbPnlFull.setVisibility(INVISIBLE);
                            ytbPnlFull.setEnabled(false);
                        }

                        ytbPnlClose.setEnabled(false);
                        ytbPnlExpand.setEnabled(false);
                        playPauseButton.setEnabled(false);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }
        };

        playPauseButton.setOnClickListener(v -> {
            triggerVisibleUIEvent();
            if (playerView.getTag() != null && playerView.getTag().equals(PlayerConstants.PlayerState.PLAYING.name())) {
                if (youTubePlayer != null) {
                    youTubePlayer.pause();
                    playerView.setTag(PlayerConstants.PlayerState.PAUSED.name());
                    playPauseButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.mediaplay));
                }
            } else if (playerView.getTag() != null && playerView.getTag().equals(PlayerConstants.PlayerState.PAUSED.name())) {
                if (youTubePlayer != null) {
                    youTubePlayer.play();
                    playerView.setTag(PlayerConstants.PlayerState.PLAYING.name());
                    playPauseButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.pausemedia));
                }
            }
        });

        popupWindow.setOnDismissListener(() -> {
            if (visibleUIHandler != null)
                visibleUIHandler.removeCallbacks(null);
            if (youTubePlayer != null)
                youTubePlayer.pause();
            isSetupNeeded = true;
        });

        ytbPnlClose.setOnClickListener(v -> popupWindow.dismiss());

        ytbPnlExpand.setOnClickListener(v -> {
            if (ytbPnlExpand.getTag().equals("normal")) {
                (ytbPnlExpand).setImageDrawable(activity.getResources().getDrawable(R.drawable.collapse));
                expandVideoView(popupWindow, 0);
                ytbPnlExpand.setTag("max");
            } else if (v.getTag().equals("max")) {
                (ytbPnlExpand).setImageDrawable(activity.getResources().getDrawable(R.drawable.maximize));
                expandVideoView(popupWindow, 1);
                ytbPnlExpand.setTag("normal");
            }
        });
    }


    private int getPopupWidht() {
        return popupWidht;
    }

    private void setPopupWidht(int popupWidht) {
        this.popupWidht = popupWidht;
        if (!isSetupNeeded) {
            popupWindow.setWidth(getPopupWidht());
        }
    }

    private int getPopupHeight() {
        return popupHeight;
    }

    private void setPopupHeight(int popupHeight) {
        this.popupHeight = popupHeight;
        if (!isSetupNeeded) {
            popupWindow.setHeight(getPopupHeight());
        }
    }

    private int getScreenHeight() {
        return screenHeight;
    }

    private void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public int getScreenWidht() {
        return screenWidht;
    }

    public void setScreenWidht(int screenWidht) {
        this.screenWidht = screenWidht;
    }
}
