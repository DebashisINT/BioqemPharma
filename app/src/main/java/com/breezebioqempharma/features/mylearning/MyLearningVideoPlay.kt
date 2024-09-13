package com.breezebioqempharma.features.mylearning

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.breezebioqempharma.R
import com.breezebioqempharma.app.NetworkConstant
import com.breezebioqempharma.app.Pref
import com.breezebioqempharma.app.utils.AppUtils
import com.breezebioqempharma.base.BaseResponse
import com.breezebioqempharma.base.presentation.BaseActivity
import com.breezebioqempharma.base.presentation.BaseFragment
import com.breezebioqempharma.features.dashboard.presentation.DashboardActivity
import com.breezebioqempharma.features.location.LocationWizard
import com.breezebioqempharma.features.mylearning.apiCall.LMSRepoProvider
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.apache.commons.lang3.time.DurationFormatUtils
import timber.log.Timber
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class MyLearningVideoPlay : BaseFragment(), View.OnClickListener {
    private lateinit var mContext: Context
    private lateinit var playerView: PlayerView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var simpleExoPlayer: SimpleExoPlayer
    private lateinit var mediaDataSourceFactory: DataSource.Factory
    private lateinit var ll_vdo_ply_like: LinearLayout
    private lateinit var iv_vdo_ply_like: ImageView
    private lateinit var ll_vdo_ply_cmmnt: LinearLayout
    private lateinit var ll_vdo_ply_share: LinearLayout
    private lateinit var ll_frag_video_play_comments: LinearLayout
    private lateinit var iv_frag_video_comment_hide: ImageView
    private lateinit var et_frag_video_comment: EditText
    private lateinit var iv_frag_video_comment_save: ImageView
    var video_watch_completed = false
    private lateinit var cmtAdapter: AdapterComment
    private lateinit var rvComments: RecyclerView
    var commentL: ArrayList<CommentL> = ArrayList()
    var like_flag = false
    var seek_dragging = false


    companion object {

        var obj_my_learning: LarningList = LarningList()
        var topic_name: String = ""
        var previousFrag: String = ""
        fun getInstance(objects: Any): MyLearningVideoPlay {
            val myLearningVideoPlay = MyLearningVideoPlay()

                obj_my_learning = objects as LarningList

            println("tag_objects" + objects)
            return myLearningVideoPlay
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater!!.inflate(R.layout.fragment_my_learning_video_play, container, false)
        initView(view)
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        return view
    }

    private fun initView(view: View) {
        playerView = view.findViewById(R.id.playerView_my_learning)
        progress_wheel =view. findViewById(R.id.progress_wheel_my_learning)
        progress_wheel.stopSpinning()
        ll_vdo_ply_like = view.findViewById(R.id.ll_vdo_ply_like)
        iv_vdo_ply_like = view.findViewById(R.id.iv_vdo_ply_like)
        ll_vdo_ply_cmmnt = view.findViewById(R.id.ll_vdo_ply_cmmnt)
        ll_vdo_ply_share = view.findViewById(R.id.ll_vdo_ply_share)
        ll_frag_video_play_comments = view.findViewById(R.id.ll_frag_video_play_comments)
        iv_frag_video_comment_hide = view.findViewById(R.id.iv_frag_video_comment_hide)
        rvComments = view.findViewById(R.id.rv_frag_video_play_comment)
        et_frag_video_comment = view.findViewById(R.id.et_frag_video_comment)
        iv_frag_video_comment_save = view.findViewById(R.id.iv_frag_video_comment_save)

        ll_frag_video_play_comments.visibility = View.GONE

        ll_vdo_ply_cmmnt.setOnClickListener(this)
        iv_frag_video_comment_hide.setOnClickListener(this)
        ll_vdo_ply_like.setOnClickListener(this)
    }

    public override fun onStart() {
        super.onStart()

        if (Util.SDK_INT > 23)
            initializePlayer()
    }

    private fun initializePlayer() {
        //simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this)
        simpleExoPlayer =  SimpleExoPlayer.Builder(mContext).build()

        mediaDataSourceFactory = DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, "mediaPlayerSample"))
        val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory).createMediaSource(
            MediaItem.fromUri(Uri.parse(obj_my_learning?.content_url)))

        //simpleExoPlayer.playWhenReady = mMicroLearning?.play_when_ready!!
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val time = LocalTime.parse(obj_my_learning.content_watch_length, formatter)
        val milliseconds = time.toSecondOfDay() * 1000L
        println("Milliseconds: $milliseconds")
        simpleExoPlayer.seekTo(milliseconds)
        simpleExoPlayer.prepare(mediaSource, false, false)

        playerView.setShutterBackgroundColor(Color.TRANSPARENT)
        playerView.player = simpleExoPlayer
        playerView.requestFocus()

        simpleExoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(@Player.State state: Int) {

                when(state){
                    Player.STATE_BUFFERING -> {
                        progress_wheel.spin()
                        println("STATE_BUFFERINGMyLearningVideo")

                    }
                    Player.STATE_READY -> {
                        progress_wheel.stopSpinning()
                        println("STATE_READYMyLearningVideo")

                    }
                    Player.STATE_IDLE -> {
                        println("STATE_IDLEMyLearningVideo")

                    }
                    Player.STATE_ENDED -> {
                        println("STATE_ENDEDMyLearningVideo")

                        //saveContentWiseInfo(data_store_LMS_CONTENT_INFO)
                    }
                }
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                super.onPositionDiscontinuity(oldPosition, newPosition, reason)
                if(((newPosition.positionMs / 1000).toInt() - (oldPosition.positionMs / 1000).toInt()) > 10 ){
                    seek_dragging = true
                    println("tag_seek seek ${oldPosition.positionMs} ${newPosition.positionMs} $reason")
                }else{
                    seek_dragging = false
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                error?.printStackTrace()
            }
        })
    }

    private fun saveContentWiseInfo(obj: LMS_CONTENT_INFO) {
        try {
            progress_wheel.visibility = View.VISIBLE
            Timber.d("saveContentWiseInfo call" + obj)
            val repository = LMSRepoProvider.getTopicList()
            BaseActivity.compositeDisposable.add(
                repository.saveContentInfoApi(obj)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val response = result as BaseResponse
                        if (response.status == NetworkConstant.SUCCESS) {
                            progress_wheel.visibility = View.GONE
                            /*try {
                                Toast.makeText(
                                    mContext,
                                    "" + response.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                            }*/
                        } else {
                            progress_wheel.visibility = View.GONE
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_found))

                        }
                    }, { error ->
                        progress_wheel.visibility = View.GONE
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                    })
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
            progress_wheel.visibility = View.GONE
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
        }
    }
    fun convertDate(dateInMilliseconds: String, dateFormat: String?): String {
        return DateFormat.format(dateFormat, dateInMilliseconds.toLong()).toString()
    }

    private fun convertTo24HourFormat(time12Hour: String): String {
        val inputFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(time12Hour)
        return outputFormat.format(date)
    }

    override fun onResume() {
        super.onResume()
        println("onResumeMyLearningVideo")
        if (Util.SDK_INT <= 23)
            initializePlayer()
    }

    fun onAPICalling() {
        //super.onDetach()
        println("onDetachMyLearningVideo")
        simpleExoPlayer?.apply {
            var position_ = simpleExoPlayer.currentPosition //Returns the playback position in the current content
            var duration = simpleExoPlayer.duration //Returns the duration of the current content
            var percentageWatched = (100 * position_ / duration)

         //   println("tag_content_lengthMyLarning"+DurationFormatUtils.formatDuration(duration, "HH:mm:ss"))
         //   println("tag_content_watch_lengthMyLarning"+DurationFormatUtils.formatDuration(position_, "HH:mm:ss"))
         //    println("tag_WatchPercentageMyLarning"+percentageWatched)
            release()
        }

        val comment_list: ArrayList<CommentL> = ArrayList()

        var position_ = simpleExoPlayer.currentPosition //Returns the playback position in the current content
        var duration = simpleExoPlayer.duration //Returns the duration of the current content
        var percentageWatched = (100 * position_ / duration)

        if (percentageWatched.toInt() == 100) {
            video_watch_completed = true
        } else {
            video_watch_completed = false
        }

        val endTime = System.currentTimeMillis()
        convertDate(endTime.toString(),"hh:mm:ss a")
        convertTo24HourFormat(convertDate(endTime.toString(),"hh:mm:ss a"))

        val data_store_LMS_CONTENT_INFO = LMS_CONTENT_INFO(
            Pref.user_id!!,
            obj_my_learning.topic_id.toInt(),
            obj_my_learning.topic_name,
            obj_my_learning.content_id,
            like_flag,
            0,
            DurationFormatUtils.formatDuration(duration, "HH:mm:ss"),
            DurationFormatUtils.formatDuration(position_, "HH:mm:ss"),
            video_watch_completed,
            AppUtils.getCurrentDateTimeNew(),
            "",
            convertTo24HourFormat(convertDate(endTime.toString(),"hh:mm:ss a")),
            DurationFormatUtils.formatDuration(position_, "HH:mm:ss"),
            DurationFormatUtils.formatDuration(position_, "HH:mm:ss"),
            "Mobile",
            "Android",
            LocationWizard.getNewLocationName(mContext, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble()),
            simpleExoPlayer.playbackParameters.speed.toString(),
            percentageWatched.toString(),
            0,
            0,
            false,
            comment_list
        )

        saveContentWiseInfo(data_store_LMS_CONTENT_INFO)


    }

    override fun onDestroy() {
        super.onDestroy()
        println("onDestroyMyLearningVideo")
        simpleExoPlayer?.apply {
            release()
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            ll_vdo_ply_cmmnt.id -> {
                ll_frag_video_play_comments.visibility = View.VISIBLE

                loadCommentData(commentL/*.filter { it.content_id.equals(obj_my_learning.content_id) } as ArrayList<CommentL>*/)

                iv_frag_video_comment_save.setOnClickListener {
                    AppUtils.hideSoftKeyboard(mContext as DashboardActivity)
                    var obj: CommentL = CommentL()
                    obj.topic_id = VideoPlayLMS.topic_id
                    obj.content_id = obj_my_learning.content_id.toString()
                    obj.comment_id = Pref.user_id+"_"+AppUtils.getCurrentDateTime()
                    obj.comment_description = et_frag_video_comment.text.toString()
                    obj.comment_date_time = AppUtils.getCurrentDateTime()
                    commentL.add(obj)

                    et_frag_video_comment.setText("")
                    loadCommentData(commentL/*.filter { it.content_id.equals(obj_my_learning.content_id) } as ArrayList<CommentL>*/)
                }
            }

            iv_frag_video_comment_hide.id -> {
                ll_frag_video_play_comments.visibility = View.GONE
            }

            ll_vdo_ply_like.id -> {
                if (like_flag) {
                    iv_vdo_ply_like.setImageResource(R.drawable.like_white)
                    like_flag = false
                  //  videos.get(position).isLiked = false
                } else {
                    like_flag = true
                    iv_vdo_ply_like.setImageResource(R.drawable.heart_red)
                  //  videos.get(position).isLiked = true
                }
            }

        }
    }

    private fun loadCommentData(comL: ArrayList<CommentL>) {
        rvComments.visibility = View.VISIBLE
        cmtAdapter = AdapterComment(mContext, comL)
        rvComments.adapter = cmtAdapter
    }

}