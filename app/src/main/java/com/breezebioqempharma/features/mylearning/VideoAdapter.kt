package com.breezebioqempharma.features.mylearning

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.breezebioqempharma.R
import com.breezebioqempharma.app.Pref
import com.breezebioqempharma.app.types.FragType
import com.breezebioqempharma.app.utils.AppUtils
import com.breezebioqempharma.features.dashboard.presentation.DashboardActivity
import com.breezebioqempharma.features.location.LocationWizard
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import kotlinx.android.synthetic.main.list_video.view.iv_list_video
import kotlinx.android.synthetic.main.list_video.view.progress_wheel
import kotlinx.android.synthetic.main.list_video.view.stylplayerView
import kotlinx.android.synthetic.main.list_video.view.tvDescrip
import kotlinx.android.synthetic.main.list_video.view.tvTitle
import org.apache.commons.lang3.time.DurationFormatUtils
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


class VideoAdapter(var context: Context,
                   var videos: ArrayList<ContentL>,
                   var topic_id:String,
                   var topic_name:String,
                   var ll_vdo_ply_like: LinearLayout,
                   var ll_vdo_ply_cmmnt: LinearLayout,
                   var ll_vdo_ply_share: LinearLayout,
                   var iv_vdo_ply_like: ImageView,
                   var videoPreparedListener: VideoAdapter.OnVideoPreparedListener,
                   var lastVideoCompleteListener: OnLastVideoCompleteListener) :
    RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    lateinit var exoPlayer: ExoPlayer

    val questionSubmit = LmsQuestionAnswerSet.question_submit
    val question_submit_content_id = LmsQuestionAnswerSet.question_submit_content_id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bindItems(context,videos,videoPreparedListener)
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        lateinit var mediaSource: MediaSource
        var video_watch_completed = false
        var like_flag = false
        var seek_dragging = false
        private val savedContentIds = mutableSetOf<Int>()


        fun bindItems(context: Context, videos_:ArrayList<ContentL>, listner : OnVideoPreparedListener){


            if (videos_.get(adapterPosition).content_url.contains(".mp4", ignoreCase = true)) {
                setVideoPath(/*"http://3.7.30.86:8073"+*/videos_.get(adapterPosition).content_url, position ,listner)
                //getVideoDuration(model.content_url)
                if (videos_.get(adapterPosition).content_url.contains("http")) {
                    setVideoPath(/*"http://3.7.30.86:8073"+*/videos_.get(adapterPosition).content_url, position ,listner)
                } else {
                    setVideoPath("http://3.7.30.86:8073" + videos_.get(adapterPosition).content_url, position ,listner)
                }
            } else {
                setGIF()
            }

            val model = videos[position]
            itemView.tvTitle.text = model.content_title
            itemView.tvDescrip.text = model.content_description

            if (model.isAllowLike) {
                ll_vdo_ply_like.visibility = View.VISIBLE
            } else {
                ll_vdo_ply_like.visibility = View.VISIBLE
            }
            if (model.isAllowComment) {
                ll_vdo_ply_cmmnt.visibility = View.VISIBLE
            } else {
                ll_vdo_ply_cmmnt.visibility = View.GONE
            }
            if (model.isAllowShare) {
                ll_vdo_ply_share.visibility = View.INVISIBLE
            } else {
                ll_vdo_ply_share.visibility = View.INVISIBLE
            }

            ll_vdo_ply_like.setOnClickListener {
                if (like_flag) {
                    iv_vdo_ply_like.setImageResource(R.drawable.like_white)
                    like_flag = false
                    videos.get(position).isLiked = false
                    videoPreparedListener.onLikeClick(false)
                } else {
                    like_flag = true
                    iv_vdo_ply_like.setImageResource(R.drawable.heart_red)
                    videos.get(position).isLiked = true
                    videoPreparedListener.onLikeClick(true)
                }


            }

            if(videos.get(position).isLiked){
                iv_vdo_ply_like.setImageResource(R.drawable.heart_red)
            }else{
                iv_vdo_ply_like.setImageResource(R.drawable.like_white)
            }

            ll_vdo_ply_share.setOnClickListener {
                openShareIntents(videos[position])
            }

            //suman 17-07-2024
            ll_vdo_ply_cmmnt.setOnClickListener {
                listner.onCommentCLick(videos[absoluteAdapterPosition])
            }
        }

        fun setGIF() {
            itemView.progress_wheel.stopSpinning()
            itemView.iv_list_video.visibility = View.GONE
            itemView.stylplayerView.visibility = View.GONE

            videoPreparedListener.onNonVideo()
            itemView.iv_list_video.visibility = View.VISIBLE
            itemView.stylplayerView.visibility = View.GONE

        }

        private fun setVideoPath(
            contentUrl: String,
            position: Int,
            listner: OnVideoPreparedListener
        ) {

            try {
               // exoPlayer.clearMediaItems()
                exoPlayer.release()

            } catch (e: Exception) {
                e.printStackTrace()
            }


            itemView.progress_wheel.stopSpinning()
                println("tag_vodeo_position_state pos : $adapterPosition")
                exoPlayer = ExoPlayer.Builder(context)
                    .setRenderersFactory(
                        DefaultRenderersFactory(context).setEnableDecoderFallback(
                            true
                        )
                    ).build()

                exoPlayer.addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        super.onPlayerError(error)
                        Toast.makeText(context, "Can't play this video", Toast.LENGTH_SHORT).show()
                    }

                    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

                        var position_ = exoPlayer.currentPosition //Returns the playback position in the current content
                        var duration = exoPlayer.duration //Returns the duration of the current content
                        var percentageWatched = (100 * position_ / duration)
                        if (percentageWatched.toInt() == 100) {
                            video_watch_completed = true
                        } else {
                            video_watch_completed = false
                        }
                        println("tag_percentageWatched_after  $video_watch_completed")
                        if (position_ > 0) {
                            println("tag_topic_id  $topic_id")
                            println("tag_topic_name  $topic_name")
                            println("tag_content_id "+videos.get(position).content_id)
                            println("tag_like_flag"+like_flag)
                            println("tag_share_count"+0)
                            println("tag_content_length"+DurationFormatUtils.formatDuration(duration, "HH:mm:ss"))
                            println("tag_content_watch_length"+DurationFormatUtils.formatDuration(position_, "HH:mm:ss"))
                            println("tag_content_watch_completed"+video_watch_completed)
                            println("tag_content_last_view_date_time"+AppUtils.getCurrentDateTimeNew())
                            println("tag_video_position  $position_ $duration")
                            println("tag_video_pos  $position_")
                            println("tag_VideoDuration "+ DurationFormatUtils.formatDuration(duration, "HH:mm:ss"))
                            println("tag_WatchedDuration "+ DurationFormatUtils.formatDuration(position_, "HH:mm:ss"))
                            println("tag_CompletionStatus")
                            println("tag_DeviceType"+"Android")
                            println("tag_OperatingSystem"+"Android")
                            println("tag_Location"+Pref.current_address)
                            println("tag_LastWatchedPosition"+position_)
                            println("tag_PlaybackSpeed"+exoPlayer.playbackParameters.speed)
                            println("tag_WatchPercentage"+percentageWatched)
                            println("tag_NoOfQuizAttempts"+"0")   // Need to be done
                            println("tag_QuizScores"+"")   // Need to be done

                            val endTime = System.currentTimeMillis()
                            convertDate(endTime.toString(),"hh:mm:ss a")
                            convertTo24HourFormat(convertDate(endTime.toString(),"hh:mm:ss a"))
                            println("Playback ended at swip: "+convertTo24HourFormat(convertDate(endTime.toString(),"hh:mm:ss a")))
                            val comment_list: ArrayList<CommentL> = ArrayList()

                            val data_swipe_LMS_CONTENT_INFO = LMS_CONTENT_INFO(
                                Pref.user_id!!,
                                topic_id.toInt(),
                                topic_name,
                                videos.get(position).content_id.toInt(),
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
                                LocationWizard.getNewLocationName(context, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble()),
                                exoPlayer.playbackParameters.speed.toString(),
                                percentageWatched.toString(),
                                0,
                                0,
                                false,
                                comment_list
                            )
                            listner.onContentInfoAPICalling(data_swipe_LMS_CONTENT_INFO)
                        }

                        when (playbackState) {
                            Player.STATE_ENDED -> {
                                println("tag_video_position_state  state ended ")

                                val endTime = System.currentTimeMillis()
                                convertDate(endTime.toString(),"hh:mm:ss a")
                                convertTo24HourFormat(convertDate(endTime.toString(),"hh:mm:ss a"))
                                println("Playback ended at: "+convertTo24HourFormat(convertDate(endTime.toString(),"hh:mm:ss a")))
                                val comment_list: ArrayList<CommentL> = ArrayList()

                                val data_end_LMS_CONTENT_INFO = LMS_CONTENT_INFO(
                                    Pref.user_id!!,
                                    topic_id.toInt(),
                                    topic_name,
                                    videos.get(position).content_id.toInt(),
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
                                    LocationWizard.getNewLocationName(context, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble()),
                                    exoPlayer.playbackParameters.speed.toString(),
                                    percentageWatched.toString(),
                                    0,
                                    0,
                                    false,
                                    comment_list
                                )

                                listner.onContentInfoAPICalling(data_end_LMS_CONTENT_INFO)

                            }

                            Player.STATE_READY -> {
                                println("tag_vodeo_position_state  state ready ")
                                val starttime = System.currentTimeMillis()
                                convertDate(starttime.toString(),"yyyy-MM-dd hh:mm:ss");
                                println("Playback start at: "+convertDate(starttime.toString(),"yyyy-MM-dd hh:mm:ss"))
                            }
                            Player.STATE_BUFFERING -> {
                                println("tag_vodeo_position_state  state buffering ")
                            }
                            Player.STATE_IDLE -> {
                                println("tag_vodeo_position_state  state idle ")
                            }
                        }

                        if (playbackState == Player.STATE_BUFFERING) {
                            println("tag_vodeo_position_state  state STATE_BUFFERING ")
                            itemView.progress_wheel.spin()
                        }
                        else if (playbackState == Player.STATE_READY) {
                            itemView.progress_wheel.stopSpinning()
                        } else if (playbackState == Player.STATE_ENDED) {
                            try {
                                val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                                val contentIdsString = sharedPreferences.getString("saved_content_ids", "")
                                val savedContentIds = SavedContentIds()
                                savedContentIds.content_id = contentIdsString!!.split(",").filter { it.isNotEmpty() }.map { it.toInt() }.let { it.toCollection(LinkedHashSet(it)) }
                                println("seek_dragging"+seek_dragging)
                                println("question_submit"+LmsQuestionAnswerSet.question_submit)
                                if (videos.get(position).question_list!=null) {
                                if (exoPlayer.playbackParameters.speed != 2.0.toFloat() && !seek_dragging /*&& !LmsQuestionAnswerSet.question_submit*/) {
                                    println("qqqqqqq"+savedContentIds.content_id)
                                    println("zzzzzzz"+videos.get(absoluteAdapterPosition).content_id)
                                    //if (!savedContentIds.content_id.contains(videos.get(absoluteAdapterPosition).content_id.toInt())) {
                                    //if (LmsQuestionAnswerSet.question_submit_content_id != videos.get(absoluteAdapterPosition).content_id.toInt()) {


                                            listner.onQuestionAnswerSetPageLoad(videos.get(position).question_list.clone() as ArrayList<QuestionL>,absoluteAdapterPosition)
                                           // var Obj_LMS_CONTENT_INFO = LmsQuestionAnswerSet.Obj_LMS_CONTENT_INFO
                                            /*(context as DashboardActivity).loadFragment(
                                                FragType.LmsQuestionAnswerSet,
                                                true,
                                                videos.get(position).question_list.clone()
                                            )*/
                                     //   }
                                    }
                                }
                                else{
                                    if (position == videos.size - 1)
                                        lastVideoCompleteListener.onLastVideoComplete()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
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
                })


                itemView.iv_list_video.visibility = View.GONE
                itemView.stylplayerView.visibility = View.VISIBLE

                //playerView.player = exoPlayer

                exoPlayer.seekTo(0)
                exoPlayer.repeatMode = Player.REPEAT_MODE_OFF

                val dataSourceFactory = DefaultDataSource.Factory(context)

                mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(Uri.parse(contentUrl)))
                exoPlayer.setMediaSource(mediaSource)
                exoPlayer.prepare()

               itemView.progress_wheel.visibility = View.GONE
                if (absoluteAdapterPosition == 0) {
                    exoPlayer.playWhenReady = true
                    //exoPlayer.seekTo(20000)
                    exoPlayer.play()
                    itemView.progress_wheel.stopSpinning()
                }

                println("tag_posss absoluteAdapterPosition:$absoluteAdapterPosition")

                videoPreparedListener.onVideoPrepared(ExoPlayerItem(exoPlayer, absoluteAdapterPosition))
                itemView.stylplayerView.player = exoPlayer

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

        fun openShareIntents(contentL: ContentL) {
            try {
                videoPreparedListener.onShareClick(contentL)
                return

                /*lateinit var manager : DownloadManager
                manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                var uuri : Uri = Uri.parse(contentL.content_url)
                val request = DownloadManager.Request(uuri)
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                val reference = manager.enqueue(request)*/

                /*lateinit var videoFile: InputStream
                var videoFileUrl = "https://file-examples-com.github.io/uploads/2017/04/file_example_MP4_480_1_5MG.mp4"
                // download the video to videoFile
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "video/mp4"
                sharingIntent.putExtra(Intent.EXTRA_STREAM, uuri)
                context.startActivity(Intent.createChooser(sharingIntent,"Share Video"))*/

               /* val password = "123"
                val salt = ByteArray(16) // 128 bits
                SecureRandom().nextBytes(salt)

                val keyGenerator = KeyGenerator.getInstance("PBKDF2WithHmacSHA1")
                val spec = PBEKeySpec(password.toCharArray(), salt, 1000, 256) // 256 bits
                val secretKeyBytes = keyGenerator.generateKey()
                val secretKey = SecretKeySpec(secretKeyBytes.encoded, "AES")

                val encryptedUrl = encryptUrl(contentL.content_url, secretKey)
                val originalUrl = decryptUrl(encryptedUrl, secretKey)*/

                println("originalUrl+++"+contentL.content_url)
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, contentL.content_url)
                ContextCompat.startActivity(
                    context,
                    Intent.createChooser(
                        intent,
                        context.getString(R.string.app_name)
                    ), null
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun encryptUrl(url: String, secretKey: SecretKeySpec): String {
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            val urlBytes = url.toByteArray(Charsets.UTF_8)
            val encryptedBytes = cipher.doFinal(urlBytes)

            return Base64.getEncoder().encodeToString(encryptedBytes)
        }

        fun decryptUrl(encryptedUrl: String, secretKey: SecretKeySpec): String {
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)

            val encryptedBytes = Base64.getDecoder().decode(encryptedUrl)
            val decryptedBytes = cipher.doFinal(encryptedBytes)

            return String(decryptedBytes, Charsets.UTF_8)
        }


    }

    interface OnVideoPreparedListener {
        fun onVideoPrepared(exoPlayerItem: ExoPlayerItem)
        fun onNonVideo()
        fun onContentInfoAPICalling(obj: LMS_CONTENT_INFO)
        fun onCommentCLick(obj: ContentL)
        fun onShareClick(obj: ContentL)
        fun onQuestionAnswerSetPageLoad(obj: ArrayList<QuestionL>,position:Int)
        fun onLikeClick(isLike:Boolean)
    }
    interface OnLastVideoCompleteListener {
        fun onLastVideoComplete()
    }
}