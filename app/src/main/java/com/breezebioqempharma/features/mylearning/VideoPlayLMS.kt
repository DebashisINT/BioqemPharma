package com.breezebioqempharma.features.mylearning

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.breezebioqempharma.R
import com.breezebioqempharma.app.FileUtils
import com.breezebioqempharma.app.NetworkConstant
import com.breezebioqempharma.app.Pref
import com.breezebioqempharma.app.types.FragType
import com.breezebioqempharma.app.utils.AppUtils
import com.breezebioqempharma.base.BaseResponse
import com.breezebioqempharma.base.presentation.BaseActivity
import com.breezebioqempharma.base.presentation.BaseFragment
import com.breezebioqempharma.features.dashboard.presentation.DashboardActivity
import com.breezebioqempharma.features.mylearning.apiCall.LMSRepoProvider
import com.breezebioqempharma.widgets.AppCustomTextView
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import timber.log.Timber
import java.io.File
import java.text.FieldPosition

class VideoPlayLMS : BaseFragment() {
    private lateinit var mContext: Context
    private lateinit var adapter: VideoAdapter
    private lateinit var adapter1: VideoAdapter1

    //private val videos = ArrayList<VideoLMS>()
    private val videos = ArrayList<VideoLMS>()
    private val exoPlayerItems = ArrayList<ExoPlayerItem>()
    private val exoPlayerItems_ = ArrayList<ExoPlayerItem1>()
    lateinit var viewPager2: ViewPager2

    private lateinit var progressWheel: ProgressBar
    private lateinit var ll_vdo_ply_like: LinearLayout
    private lateinit var iv_vdo_ply_like: ImageView
    private lateinit var ll_vdo_ply_cmmnt: LinearLayout
    private lateinit var ll_vdo_ply_share: LinearLayout
    private lateinit var ll_video_not_found: LinearLayout
    private lateinit var ll_frag_video_play_comments: LinearLayout
    private lateinit var iv_frag_video_comment_hide: ImageView
    private lateinit var et_frag_video_comment: EditText
    private lateinit var iv_frag_video_comment_save: ImageView
    //var lastvideo:Boolean = false
    private lateinit var cmtAdapter: AdapterComment


    private lateinit var rvComments: RecyclerView

    var contentL: ArrayList<ContentL> = ArrayList()

    var commentL: ArrayList<CommentL> = ArrayList()

    var Obj_LMS_CONTENT_INFO: LMS_CONTENT_INFO = LMS_CONTENT_INFO()

    lateinit var currentVideoObj : ContentL

    companion object {
        var topic_id: String = ""
        var topic_name: String = ""
        var previousFrag: String = ""
        var lastvideo: Boolean = false
        fun getInstance(objects: Any): VideoPlayLMS {
            val videoPlayLMS = VideoPlayLMS()

            if (!TextUtils.isEmpty(objects.toString())) {
                val parts = objects.toString().split("~")
                topic_id = parts[0]
                topic_name = parts[1]
            } else {
                topic_id = ""
                topic_name = ""
            }
            println("tag_topic_id" + topic_id)


            val bundle = Bundle()
            bundle.putBoolean("LAST_VIDEO", lastvideo)
            videoPlayLMS.arguments = bundle
            return videoPlayLMS
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater!!.inflate(R.layout.fragment_video_play_l_m_s, container, false)
        initView(view)
        requireActivity().getWindow()
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return view
    }

    private fun initView(view: View) {
        progressWheel = view.findViewById(R.id.pb_frag_video_player)
        ll_vdo_ply_like = view.findViewById(R.id.ll_vdo_ply_like)
        iv_vdo_ply_like = view.findViewById(R.id.iv_vdo_ply_like)
        ll_vdo_ply_cmmnt = view.findViewById(R.id.ll_vdo_ply_cmmnt)
        ll_vdo_ply_share = view.findViewById(R.id.ll_vdo_ply_share)
        ll_video_not_found = view.findViewById(R.id.ll_video_not_found)
        ll_frag_video_play_comments = view.findViewById(R.id.ll_frag_video_play_comments)
        iv_frag_video_comment_hide = view.findViewById(R.id.iv_frag_video_comment_hide)
        rvComments = view.findViewById(R.id.rv_frag_video_play_comment)
        et_frag_video_comment = view.findViewById(R.id.et_frag_video_comment)
        iv_frag_video_comment_save = view.findViewById(R.id.iv_frag_video_comment_save)
        progressWheel.visibility = View.GONE
        ll_video_not_found.visibility = View.GONE
        ll_frag_video_play_comments.visibility = View.GONE

        viewPager2 = view.findViewById(R.id.viewPager2)

        contentL = ArrayList()

        iv_frag_video_comment_hide.setOnClickListener {
            ll_frag_video_play_comments.visibility = View.GONE
        }

        if (topic_id != "") {
            getVideoTopicWise()
            return
        }
        adapter1 = VideoAdapter1(
            mContext,
            videos,
            ll_vdo_ply_like,
            ll_vdo_ply_cmmnt,
            ll_vdo_ply_share,
            object : VideoAdapter1.OnVideoPreparedListener {
                override fun onVideoPrepared(exoPlayerItem: ExoPlayerItem1) {
                    exoPlayerItems_.add(exoPlayerItem)
                }

                override fun onNonVideo() {
                    //Toaster.msgShort(mContext,"Not video")
                }
            })

        viewPager2.adapter = adapter1

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                val previousIndex = exoPlayerItems.indexOfFirst { it.exoPlayer.isPlaying }
                if (previousIndex != -1) {
                    val player = exoPlayerItems[previousIndex].exoPlayer
                    player.pause()
                    player.playWhenReady = false
                }
                val newIndex = exoPlayerItems.indexOfFirst { it.position == position }
                if (newIndex != -1) {
                    val player = exoPlayerItems[newIndex].exoPlayer
                    player.playWhenReady = true
                    player.play()
                    //progressWheel.visibility = View.GONE
                }
            }
        })

        /*  adapter = VideoAdapter(mContext, videos, object : VideoAdapter.OnVideoPreparedListener {
              override fun onVideoPrepared(exoPlayerItem: ExoPlayerItem) {
                  exoPlayerItems.add(exoPlayerItem)
              }

              override fun onNonVideo() {
                  //Toaster.msgShort(mContext,"Not video")
              }
          })

          viewPager2.adapter = adapter */

        ll_vdo_ply_like.setOnClickListener {
            Toast.makeText(mContext, "hi", Toast.LENGTH_SHORT).show()
            //  iv_vdo_ply_like.setImageResource(R.drawable.heart_red)
            /* if(iv_vdo_ply_like.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.heart_red).getConstantState()){
                 //do work here
                 iv_vdo_ply_like.setImageResource(R.drawable.heart_)
             }else{
                 iv_vdo_ply_like.setImageResource(R.drawable.heart_red)
             }*/
        }


        ll_vdo_ply_share.setOnClickListener {
            //share
        }

    }

    fun getVideoTopicWise() {
        try {
            progressWheel.visibility = View.VISIBLE
            Timber.d("deleteImei call" + AppUtils.getCurrentDateTime())
            val repository = LMSRepoProvider.getTopicList()
            BaseActivity.compositeDisposable.add(
                repository.getTopicsWiseVideo(topic_id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val response = result as VideoTopicWiseResponse
                        if (response.status == NetworkConstant.SUCCESS) {
                            progressWheel.visibility = View.GONE
                            try {
                                if (response.content_list != null && response.content_list.size > 0) {
                                    ll_video_not_found.visibility = View.GONE
                                    contentL = response.content_list
                                    // Sort the content list by content_play_sequence
                                    val sortedList = contentL.sortedBy { it.content_play_sequence.toInt() }.toCollection(ArrayList())
                                    Log.d("sortedList", "" + sortedList)

                                    setVideoAdapter(
                                        sortedList, topic_id, topic_name,
                                        ll_vdo_ply_like,
                                        ll_vdo_ply_cmmnt,
                                        ll_vdo_ply_share,
                                        iv_vdo_ply_like
                                    )
                                } else {
                                    Toast.makeText(mContext, "No video found", Toast.LENGTH_SHORT)
                                        .show()
                                    ll_video_not_found.visibility = View.VISIBLE
                                }
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                            }
                        } else {
                            progressWheel.visibility = View.GONE
                            ll_video_not_found.visibility = View.VISIBLE
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_found))

                        }
                    }, { error ->
                        progressWheel.visibility = View.GONE
                        ll_video_not_found.visibility = View.GONE
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                    })
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
            progressWheel.visibility = View.GONE
            ll_video_not_found.visibility = View.GONE
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
        }
    }

    fun setVideoAdapter(
        contentL: ArrayList<ContentL>,
        topic_id: String,
        topic_name: String,
        ll_vdo_ply_like: LinearLayout,
        ll_vdo_ply_cmmnt: LinearLayout,
        ll_vdo_ply_share: LinearLayout,
        iv_vdo_ply_like: ImageView,
    ) {
        adapter = VideoAdapter(
            mContext,
            contentL,
            topic_id,
            topic_name,
            ll_vdo_ply_like,
            ll_vdo_ply_cmmnt,
            ll_vdo_ply_share,
            iv_vdo_ply_like,
            object : VideoAdapter.OnVideoPreparedListener {

                override fun onLikeClick(isLike:Boolean) {
                    contentL.filter { it.content_id.equals(currentVideoObj.content_id) }.first().isLiked = isLike
                    adapter.notifyDataSetChanged()
                    println("tag_like_check $isLike for ${currentVideoObj.content_title}")
                    for(i in 0..contentL.size-1){
                        println("tag_like_check loop name : ${contentL.get(i).content_title} isLike :  ${contentL.get(i).isLiked}")
                    }
                }

                override fun onVideoPrepared(exoPlayerItem: ExoPlayerItem) {
                    exoPlayerItems.add(exoPlayerItem)
                }

                override fun onNonVideo() {
                    //Toaster.msgShort(mContext,"Not video")
                }

                override fun onContentInfoAPICalling(obj: LMS_CONTENT_INFO) {
                    obj.CompletionStatus = false
                    obj.QuizScores = 0
                    obj.comment_list = commentL.filter { it.content_id.equals(obj.content_id) } as ArrayList<CommentL>
                    Obj_LMS_CONTENT_INFO = obj
                    println("onContentInfoAPICallingComment" + commentL.filter { it.content_id.equals(obj.content_id) } as ArrayList<CommentL>)
                    println("onContentInfoAPICalling" + obj)

                    saveContentWiseInfo(obj)
                }

                override fun onCommentCLick(obj: ContentL) {

                    onCommentClick(currentVideoObj)
                }

                override fun onShareClick(obj: ContentL) {
                    try {


                        val Download_Uri = Uri.parse(obj.content_url)
                        val request = DownloadManager.Request(Download_Uri)
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                        request.setAllowedOverRoaming(false)
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        request.setTitle("Downloading")
                        request.setDescription("Downloading File")
                        var dir = "${AppUtils.getCurrentDateTime().replace(" ", "").replace("-", "").replace(":", "")}.mp4"
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, dir)

                        val downloadManager = context!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
                        var downloadID = downloadManager!!.enqueue(request) // enqueue puts the download request in the queue.

                        Handler().postDelayed(Runnable {
                            if (downloadID != null) {
                                try {
                                val fileDownloadedUri: Uri =
                                    downloadManager.getUriForDownloadedFile(downloadID)
                                val fileDownloadedPath =
                                    FileUtils.getPath(context, fileDownloadedUri)

                                    val shareIntent = Intent(Intent.ACTION_SEND)
                                    val fileUrl = Uri.parse(fileDownloadedPath)
                                    val file = File(fileUrl.path)
                                    //val uri = Uri.fromFile(file)
                                    val uri: Uri = FileProvider.getUriForFile(
                                        mContext,
                                        context!!.applicationContext.packageName.toString() + ".provider",
                                        file
                                    )
                                    shareIntent.type = "image/png"
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                                    startActivity(Intent.createChooser(shareIntent, "Share using"));
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }, 3100)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                override fun onQuestionAnswerSetPageLoad(obj: ArrayList<QuestionL>,position: Int) {

                    println("tag_value_contentLSize"+contentL.size)
                    println("tag_value_position"+position)

                    if (contentL.size -1 == position)
                        lastvideo = true
                    else
                        lastvideo = false

                    LmsQuestionAnswerSet.lastVideo = /*true*/ lastvideo
                    println("tag_value_set setting value"+lastvideo)

                    (context as DashboardActivity).loadFragment(
                        FragType.LmsQuestionAnswerSet,
                        true,
                        obj/*+"~"+lastvideo*/
                    )
                }



            },
            object : VideoAdapter.OnLastVideoCompleteListener {
                override fun onLastVideoComplete() {
                    (mContext as DashboardActivity).onBackPressed()
                }

            }
        )

        viewPager2.adapter = adapter

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentVideoObj = contentL.get(position)
                println("onPageSelected1 +++++" + position)
                println("video_start_time"+AppUtils.getCurrentDateTimeNew())
                ll_frag_video_play_comments.visibility = View.GONE
                AppUtils.hideSoftKeyboard(mContext as DashboardActivity)
                //progressWheel.visibility = View.VISIBLE
                val previousIndex = exoPlayerItems.indexOfFirst { it.exoPlayer.isPlaying }
                if (previousIndex != -1) {
                    val player = exoPlayerItems[previousIndex].exoPlayer
                    player.pause()
                    player.playWhenReady = false
                }
                val newIndex = exoPlayerItems.indexOfFirst { it.position == position }
                if (newIndex != -1) {
                    val player = exoPlayerItems[newIndex].exoPlayer
                    player.playWhenReady = true
                    player.play()
                    //progressWheel.visibility = View.GONE
                }
            }
        })


    }

    private fun saveContentWiseInfo(obj: LMS_CONTENT_INFO) {
        try {
            progressWheel.visibility = View.VISIBLE
            Timber.d("saveContentWiseInfo call" + obj)
            val repository = LMSRepoProvider.getTopicList()
            BaseActivity.compositeDisposable.add(
                repository.saveContentInfoApi(obj)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val response = result as BaseResponse
                        if (response.status == NetworkConstant.SUCCESS) {
                            progressWheel.visibility = View.GONE
                           /* try {
                                Toast.makeText(
                                    mContext,
                                    "" + response.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                            }*/
                        } else {
                            progressWheel.visibility = View.GONE
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_found))

                        }
                    }, { error ->
                        progressWheel.visibility = View.GONE
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                    })
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
            progressWheel.visibility = View.GONE
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
        }
    }

    override fun onPause() {
        super.onPause()

        val index = exoPlayerItems.indexOfFirst { it.position == viewPager2.currentItem }
        if (index != -1) {
            val player = exoPlayerItems[index].exoPlayer
            player.pause()
            player.playWhenReady = false
        }
    }

    override fun onResume() {
        super.onResume()

        val index = exoPlayerItems.indexOfFirst { it.position == viewPager2.currentItem }
        if (index != -1) {
            val player = exoPlayerItems[index].exoPlayer
            player.playWhenReady = true
            player.play()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (exoPlayerItems.isNotEmpty()) {
            for (item in exoPlayerItems) {
                val player = item.exoPlayer
                player.stop()
                player.clearMediaItems()
            }
        }
    }


    fun onCommentClick(videoObj: ContentL) {
        println("onCommentCLick++++" + videoObj.content_id)
        ll_frag_video_play_comments.visibility = View.VISIBLE

        loadCommentData(commentL.filter { it.content_id.equals(videoObj.content_id) } as ArrayList<CommentL>)

        iv_frag_video_comment_save.setOnClickListener {
            AppUtils.hideSoftKeyboard(mContext as DashboardActivity)
            var obj: CommentL = CommentL()
            obj.topic_id = topic_id
            obj.content_id = videoObj.content_id
            obj.comment_id = Pref.user_id+"_"+AppUtils.getCurrentDateTime()
            obj.comment_description = et_frag_video_comment.text.toString()
            obj.comment_date_time = AppUtils.getCurrentDateTime()
            commentL.add(obj)

            et_frag_video_comment.setText("")
            loadCommentData(commentL.filter { it.content_id.equals(videoObj.content_id) } as ArrayList<CommentL>)
        }
    }

    fun loadCommentData(comL: ArrayList<CommentL>) {
        rvComments.visibility = View.VISIBLE
        cmtAdapter = AdapterComment(mContext, comL)
        rvComments.adapter = cmtAdapter
    }
}