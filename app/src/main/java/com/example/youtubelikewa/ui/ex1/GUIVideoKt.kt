package com.example.youtubelikewa.ui.ex1//package com.example.youtubelikewa.ui
//
//import android.animation.Animator
//import android.animation.AnimatorListenerAdapter
//import android.animation.ValueAnimator
//import android.annotation.SuppressLint
//import android.graphics.Point
//import android.graphics.drawable.BitmapDrawable
//import android.os.Build
//import android.os.Handler
//import android.os.Looper
//import android.util.Log
//import android.util.TypedValue
//import android.view.*
//import android.view.View.OnTouchListener
//import android.view.animation.AccelerateInterpolator
//import android.view.animation.AlphaAnimation
//import android.view.animation.Animation
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import com.example.youtubelikewa.R
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
//import java.util.concurrent.atomic.AtomicInteger
//
//class GUIVideoKt(private val activity: AppCompatActivity) {
//    private val coffeeVideo: GUIVideoKt
//    fun setVideoStartSecond(videoStartSecond: Float): GUIVideoKt {
//        this.videoStartSecond = videoStartSecond
//        youTubePlayer?.seekTo(videoStartSecond)
//        return coffeeVideo
//    }
//
//    fun close() {
//        if (popupWindow != null) popupWindow!!.dismiss()
//    }
//
//    private var videoStartSecond = 0f
//    private var screenHeight = 0
//    var screenWidht = 0
//    private var popupWindow: PopupWindow? = null
//    private var popupView: View? = null
//    private val TAG = "Basicodemine"
//    private var playerView: YouTubePlayerView? = null
//    private var popupWidht = 0
//        private set(popupWidht) {
//            field = popupWidht
//            if (!isSetupNeeded) {
//                popupWindow!!.width = popupWidht
//            }
//        }
//    private var popupHeight = 0
//        private set(popupHeight) {
//            field = popupHeight
//            if (!isSetupNeeded) {
//                popupWindow!!.height = popupHeight
//            }
//        }
//    private var draggablePanel: FrameLayout? = null
//    private var playPauseButton: ImageView? = null
//    private var progressBar: ProgressBar? = null
//    private val youTubePlayer: YouTubePlayer? = null
//    private var seekBar: SeekBar? = null
//    private var isSetupNeeded = true
//    private var ytbPnlExpand: ImageView? = null
//    private var ytbPnlClose: ImageView? = null
//    private var positionX = 0
//    private var positionY = 100
//    private var visibleUIHandler: Handler? = null
//    private var visibleUIRunnable: Runnable? = null
//    var flyGravity: FLY_GRAVITY? = null
//        private set
//
//    enum class FLOAT_MOVE {
//        STICKY, FREE
//    }
//
//    enum class FLY_GRAVITY {
//        TOP, BOTTOM
//    }
//
//    fun setFlyGravity(flyGravity: FLY_GRAVITY?): GUIVideoKt {
//        this.flyGravity = flyGravity
//        return coffeeVideo
//    }
//
//    fun setFloatMode(floatMode: FLOAT_MOVE): GUIVideoKt {
//        Companion.floatMode = floatMode
//        return coffeeVideo
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    fun videoSetup(youtubeVideoId: String): GUIVideoKt {
//        if (isSetupNeeded) {
//            val display = activity.windowManager.defaultDisplay
//            val size = Point()
//            display.getRealSize(size)
//            screenHeight = size.y
//            screenWidht = size.x
//            popupWidht = screenWidht - 250
//            popupHeight = (screenWidht - 250) / 16 * 9
//            val layoutInflater = LayoutInflater.from(activity)
//            popupView = layoutInflater.inflate(R.layout.gui_layout, null)
//            setDefaultPopupWindow()
//            setUpViews(popupView)
//            draggablePanel!!.setOnTouchListener(object : OnTouchListener {
//                var orgX = 0
//                var orgY = 0
//                var offsetX = 0
//                var offsetY = 0
//                override fun onTouch(v: View, event: MotionEvent): Boolean {
//                    when (event.action) {
//                        MotionEvent.ACTION_DOWN -> {
//                            orgX = event.x.toInt()
//                            orgY = event.y.toInt()
//                        }
//                        MotionEvent.ACTION_MOVE -> {
//                            offsetX = event.rawX.toInt() - orgX
//                            offsetY = event.rawY.toInt() - orgY
//                            if (ytbPnlClose!!.visibility == View.INVISIBLE) triggerVisibleUIEvent() else popupWindow!!.update(
//                                offsetX,
//                                offsetY,
//                                -1,
//                                -1,
//                                true
//                            )
//                        }
//                        MotionEvent.ACTION_UP -> if (floatMode == FLOAT_MOVE.STICKY) if (ytbPnlClose!!.visibility == View.INVISIBLE) triggerVisibleUIEvent() else repositionScript(
//                            popupWindow,
//                            offsetX,
//                            offsetY
//                        )
//                    }
//                    return true
//                }
//            })
//            isSetupNeeded = false
//        }
//        setPlayerView(youtubeVideoId)
//        return coffeeVideo
//    }
//
//    private fun triggerVisibleUIEvent() {
//        visibleAndEnableUI()
//        visibleUIHandler!!.removeCallbacks(null)
//        visibleUIHandler!!.postDelayed(visibleUIRunnable!!, 1000)
//    }
//
//    private fun visibleAndEnableUI() {
//        ytbPnlClose!!.visibility = View.VISIBLE
//        ytbPnlExpand!!.visibility = View.VISIBLE
//        playPauseButton!!.visibility = View.VISIBLE
//        ytbPnlClose!!.isEnabled = true
//        ytbPnlExpand!!.isEnabled = true
//        playPauseButton!!.isEnabled = true
//    }
//
//    private fun repositionScript(popupWindow: PopupWindow?, defX: Int, defY: Int) {
//        val tv = TypedValue()
//        val actionBarHeight: Int
//        actionBarHeight =
//            if (activity.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
//                TypedValue.complexToDimensionPixelSize(tv.data, activity.resources.displayMetrics)
//            } else {
//                150
//            }
//        val animator = ValueAnimator.ofFloat(0.0f, 1.0f)
//        val mDuration = 300
//        animator.duration = mDuration.toLong()
//        val endX = (screenWidht - popupWidht) / 2
//        val endY =
//            if (defY < screenHeight / 2) actionBarHeight else screenHeight - (actionBarHeight + popupHeight)
//        positionX = endX
//        positionY = endY
//        animator.addUpdateListener { animation: ValueAnimator ->
//            popupWindow!!.update(
//                (defX + (endX - defX) * animation.animatedValue as Float).toInt(),
//                (defY + (endY - defY) * animation.animatedValue as Float).toInt(),
//                -1,
//                -1,
//                true
//            )
//        }
//        animator.start()
//    }
//
//    fun show(targetView: View?) {
//        positionX = (screenWidht - popupWidht) / 2
//        if (flyGravity == FLY_GRAVITY.TOP) popupWindow!!.showAtLocation(
//            targetView,
//            Gravity.NO_GRAVITY,
//            (screenWidht - popupWidht) / 2,
//            100
//        ) else popupWindow!!.showAtLocation(
//            targetView,
//            Gravity.NO_GRAVITY,
//            (screenWidht - popupWidht) / 2,
//            screenHeight - popupHeight - 100
//        )
//    }
//
//    private fun expandVideoView(popupWindow: PopupWindow?, type: Int) {
//        triggerVisibleUIEvent()
//        val anim = ValueAnimator.ofFloat(0f, 1f)
//        anim.duration = 200
//        val latestH = AtomicInteger()
//        val latestW = AtomicInteger()
//        anim.addUpdateListener { animation: ValueAnimator ->
//            if (type == 1) {
//                latestH.set(((popupWidht - 200 * animation.animatedValue as Float) / 16 * 9).toInt())
//                latestW.set((popupWidht - 200 * animation.animatedValue as Float).toInt())
//                popupWindow!!.update(
//                    (screenWidht - latestW.toInt()) / 2,
//                    positionY,
//                    latestW.toInt(),
//                    latestH.toInt()
//                )
//            } else if (type == 0) {
//                latestH.set(((popupWidht + 200 * animation.animatedValue as Float) / 16 * 9).toInt())
//                latestW.set((popupWidht + 200 * animation.animatedValue as Float).toInt())
//                popupWindow!!.update(
//                    (screenWidht - latestW.toInt()) / 2,
//                    positionY,
//                    latestW.toInt(),
//                    latestH.toInt()
//                )
//            }
//        }
//        anim.addListener(object : AnimatorListenerAdapter() {
//            override fun onAnimationEnd(animation: Animator) {
//                super.onAnimationEnd(animation)
//                popupHeight = latestH.toInt()
//                popupWidht = latestW.toInt()
//            }
//        })
//        anim.start()
//    }
//
//    private fun setPlayerView(youtubeVideoId: String) {
//        activity.lifecycle.addObserver(playerView!!)
//        playerView!!.getPlayerUiController().showUi(false)
//        playerView!!.initialize(object : YouTubePlayerListener {
//            override fun onReady(youTubePlayer: YouTubePlayer) {
//                youTubePlayer.loadVideo(youtubeVideoId, videoStartSecond)
//            }
//
//            override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerState) {
//                playerView!!.tag = state.name
//                when (state) {
//                    PlayerState.PLAYING -> {
//                        Log.d(TAG, "video state " + state.name)
//                        progressBar!!.visibility = View.GONE
//                        playPauseButton!!.visibility = View.VISIBLE
//                    }
//                    PlayerState.UNKNOWN, PlayerState.VIDEO_CUED -> Log.d(
//                        TAG,
//                        "video state " + state.name
//                    )
//                    PlayerState.BUFFERING -> {
//                        progressBar!!.visibility = View.VISIBLE
//                        playPauseButton!!.visibility = View.GONE
//                        Log.d(TAG, "video state " + state.name)
//                    }
//                    PlayerState.ENDED -> Log.d(TAG, "video state " + state.name)
//                    PlayerState.PAUSED -> Log.d(TAG, "video state " + state.name)
//                }
//            }
//
//            override fun onPlaybackQualityChange(
//                youTubePlayer: YouTubePlayer,
//                playbackQuality: PlaybackQuality
//            ) {
//            }
//
//            override fun onPlaybackRateChange(
//                youTubePlayer: YouTubePlayer,
//                playbackRate: PlaybackRate
//            ) {
//            }
//
//            override fun onError(youTubePlayer: YouTubePlayer, playerError: PlayerError) {}
//            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, v: Float) {
//                seekBar!!.progress = v.toInt()
//            }
//
//            override fun onVideoDuration(youTubePlayer: YouTubePlayer, v: Float) {
//                seekBar!!.progress = 0
//                seekBar!!.max = v.toInt()
//            }
//
//            override fun onVideoLoadedFraction(youTubePlayer: YouTubePlayer, v: Float) {}
//            override fun onVideoId(youTubePlayer: YouTubePlayer, s: String) {}
//            override fun onApiChange(youTubePlayer: YouTubePlayer) {}
//        }, true)
//    }
//
//    private fun setDefaultPopupWindow() {
//        popupWindow = PopupWindow(activity.baseContext)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            popupWindow!!.elevation = 20f
//        }
//        popupWindow!!.contentView = popupView
//        popupWindow!!.width = popupWidht
//        popupWindow!!.height = popupHeight
//        popupWindow!!.isOutsideTouchable = false
//        popupWindow!!.setBackgroundDrawable(BitmapDrawable())
//        popupWindow!!.isClippingEnabled = false
//        popupWindow!!.animationStyle = R.style.Animation
//    }
//
//    private fun setUpViews(v: View?) {
//        ytbPnlClose = v!!.findViewById(R.id.ytb_pnl_close)
//        ytbPnlExpand = v.findViewById(R.id.ytb_pnl_expand)
//        playerView = v.findViewById(R.id.youtube_player)
//        playerView.enableAutomaticInitialization = false
//        draggablePanel = v.findViewById(R.id.draggablePanel)
//        playPauseButton = v.findViewById(R.id.ytb_play_pause_button)
//        progressBar = v.findViewById(R.id.ytb_progressbar)
//        seekBar = v.findViewById(R.id.ytb_seek_bar)
//        setupUIListeners()
//    }
//
//    private fun setupUIListeners() {
//        visibleUIHandler = Handler(Looper.myLooper()!!)
//        visibleUIRunnable = Runnable {
//            if (ytbPnlExpand!!.visibility == View.VISIBLE) {
//                val fadeOut: Animation = AlphaAnimation(1, 0)
//                fadeOut.interpolator = AccelerateInterpolator()
//                fadeOut.startOffset = 1000
//                fadeOut.duration = 200
//                ytbPnlExpand!!.startAnimation(fadeOut)
//                ytbPnlClose!!.startAnimation(fadeOut)
//                playPauseButton!!.startAnimation(fadeOut)
//                fadeOut.setAnimationListener(object : Animation.AnimationListener {
//                    override fun onAnimationStart(animation: Animation) {}
//                    override fun onAnimationEnd(animation: Animation) {
//                        ytbPnlClose!!.visibility = View.INVISIBLE
//                        ytbPnlExpand!!.visibility = View.INVISIBLE
//                        playPauseButton!!.visibility = View.INVISIBLE
//                        ytbPnlClose!!.isEnabled = false
//                        ytbPnlExpand!!.isEnabled = false
//                        playPauseButton!!.isEnabled = false
//                    }
//
//                    override fun onAnimationRepeat(animation: Animation) {}
//                })
//            }
//        }
//        playPauseButton!!.setOnClickListener { v: View? ->
//            triggerVisibleUIEvent()
//            if (playerView!!.tag != null && playerView!!.tag == PlayerState.PLAYING.name) {
//                if (youTubePlayer != null) {
//                    youTubePlayer.pause()
//                    playerView!!.tag = PlayerState.PAUSED.name
//                    playPauseButton!!.setImageDrawable(activity.resources.getDrawable(R.drawable.mediaplay))
//                }
//            } else if (playerView!!.tag != null && playerView!!.tag == PlayerState.PAUSED.name) {
//                if (youTubePlayer != null) {
//                    youTubePlayer.play()
//                    playerView!!.tag = PlayerState.PLAYING.name
//                    playPauseButton!!.setImageDrawable(activity.resources.getDrawable(R.drawable.pausemedia))
//                }
//            }
//        }
//        popupWindow!!.setOnDismissListener {
//            if (visibleUIHandler != null) visibleUIHandler!!.removeCallbacks(null)
//            youTubePlayer?.pause()
//            isSetupNeeded = true
//        }
//        ytbPnlClose!!.setOnClickListener { v: View? -> popupWindow!!.dismiss() }
//        ytbPnlExpand!!.setOnClickListener { v: View ->
//            if (ytbPnlExpand!!.tag == "normal") {
//                ytbPnlExpand!!.setImageDrawable(activity.resources.getDrawable(R.drawable.collapse))
//                expandVideoView(popupWindow, 0)
//                ytbPnlExpand!!.tag = "max"
//            } else if (v.tag == "max") {
//                ytbPnlExpand!!.setImageDrawable(activity.resources.getDrawable(R.drawable.maximize))
//                expandVideoView(popupWindow, 1)
//                ytbPnlExpand!!.tag = "normal"
//            }
//        }
//    }
//
//    companion object {
//        private var instance: GUIVideoKt? = null
//        fun getInstance(activity: AppCompatActivity): GUIVideoKt? {
//            if (instance == null) {
//                instance = GUIVideoKt(activity)
//            }
//            return instance
//        }
//
//        var apiKey = ""
//        var floatMode = FLOAT_MOVE.STICKY
//            get() = Companion.field
//    }
//
//    init {
//        coffeeVideo = this
//    }
//}