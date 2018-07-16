package jdroidcoder.ua.chill.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import jdroidcoder.ua.chill.R
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.drawable.BitmapDrawable
import android.renderscript.ScriptIntrinsicBlur
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.graphics.Bitmap
import android.os.Build
import android.annotation.TargetApi
import android.graphics.Canvas
import android.media.MediaPlayer
import android.net.Uri
import android.os.CountDownTimer
import android.view.View
import android.view.ViewTreeObserver
import butterknife.ButterKnife
import butterknife.OnClick
import com.squareup.picasso.Picasso
import jdroidcoder.ua.chill.ChillApp
import jdroidcoder.ua.chill.event.ContinuePlay
import jdroidcoder.ua.chill.event.PlayAudio
import jdroidcoder.ua.chill.fragment.*
import jdroidcoder.ua.chill.network.RetrofitConfig
import jdroidcoder.ua.chill.network.RetrofitSubscriber
import jdroidcoder.ua.chill.response.Category
import jdroidcoder.ua.chill.response.CollectionItem
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by jdroidcoder on 09.07.2018.
 */
class MainActivity : AppCompatActivity(), MediaPlayer.OnPreparedListener {
    companion object {
        var player: MediaPlayer? = null
        var isNeedPlay = false
    }

    private var apiService = RetrofitConfig().adapter
    private var timer: CountDownTimer? = null
    private var collection: CollectionItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.getDefault().register(this)
        ButterKnife.bind(this)
        apiService.getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(object : RetrofitSubscriber<ArrayList<Category>>() {
                    override fun onNext(response: ArrayList<Category>) {
                        ChillApp.category = response
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
        if (ChillApp.token?.getIsRegistration() == true) {
            applyBlur()
            val fragment = PreferenceFragment.newInstance()
            supportFragmentManager?.beginTransaction()
                    ?.replace(android.R.id.content, fragment)
                    ?.addToBackStack(fragment.tag)
                    ?.commit()

        }
        audioName?.typeface = ChillApp?.demiFont
        home()
    }

    override fun onBackPressed() {
        if (supportFragmentManager?.fragments?.isEmpty() == false
                && supportFragmentManager?.fragments?.last() is MeditationCompletedFragment) {
            return
        }
        super.onBackPressed()
    }

    @OnClick(R.id.home)
    fun home() {
        val fragment = HomeFragment.newInstance()
        supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container, fragment)
                ?.commit()
    }

    @OnClick(R.id.sleep)
    fun sleep() {
        val fragment = ChillApp.category?.first { p -> p.name == "Sleep" }?.let { SleepFragment.newInstance(it) }
        supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container, fragment)
                ?.commit()
    }

    @OnClick(R.id.meditate)
    fun meditate() {
        val fragment = ChillApp.category?.first { p -> p.name == "Meditations" }?.let { MeditateFragment.newInstance(it) }
        supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container, fragment)
                ?.commit()
    }

    @OnClick(R.id.music)
    fun music() {
        val fragment = ChillApp.category?.first { p -> p.name == "Music" }?.let { MusicFragment.newInstance(it) }
        supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container, fragment)
                ?.commit()
    }

    @OnClick(R.id.playerView)
    fun playerView() {
        if (player?.isPlaying == true) {
            button()
        }
        val fragment = collection?.let { PlayerFragment.newInstance(it) }
        supportFragmentManager?.beginTransaction()
                ?.add(android.R.id.content, fragment)
                ?.addToBackStack(fragment?.tag)
                ?.commit()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun playAudio(playAudio: PlayAudio) {
        collection = playAudio?.collectionItem
        if (player != null && player?.isPlaying == true) {
            player?.stop()
        }
        playerView?.visibility = View.VISIBLE
        Picasso.with(this)
                .load(playAudio?.collectionItem?.previewPhotoUrl).resizeDimen(R.dimen.size_60_dp, R.dimen.size_60_dp)
                .into(audioImage)
        audioName?.text = playAudio?.collectionItem?.collectionItems?.get(0)?.title
        try {
            authorName?.text = playAudio?.collectionItem?.authors?.get(0).fullName
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        button?.setImageResource(R.drawable.ic_pause_black_24dp)
        player = MediaPlayer()
        player?.setDataSource(applicationContext, if (collection?.isMeditation() == false) {
            Uri.parse(collection?.collectionItems?.get(0)?.audioUrl)
        } else {
            val currentData = collection?.collectionItems?.first { p -> p.number == collection?.endedCount ?: 1 }
            Uri.parse(currentData?.audioUrl)
        })
        player?.prepare()
        player?.start()
        player?.seekTo(playAudio.startFrom)
        player?.setOnPreparedListener(this)
        player?.setOnCompletionListener {
            if (collection?.isMeditation() == true) {
                collection?.selectedDay?.id?.let {
                    apiService?.endDay(it)
                            ?.subscribeOn(Schedulers.io())
                            ?.observeOn(AndroidSchedulers.mainThread())
                            ?.unsubscribeOn(Schedulers.io())
                            ?.subscribe(object : RetrofitSubscriber<Object>() {
                                override fun onNext(response: Object) {
                                }

                                override fun onError(e: Throwable) {
                                    e.printStackTrace()
                                }
                            })
                }
                collection?.collectionItems?.first { p -> p.number == collection?.selectedDay?.number }?.isEnded = 1
                val isLast = collection?.collectionItems?.findLast { p -> !p.isEnded() }
                if (isLast == null) {
                    val fragment = collection?.let { MeditationCompletedFragment.newInstance(it) }
                    supportFragmentManager?.beginTransaction()
                            ?.add(android.R.id.content, fragment)
                            ?.addToBackStack(fragment?.tag)
                            ?.commit()
                }
            }
            button?.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        }
    }

    override fun onPrepared(mMediaPlayer: MediaPlayer?) {
        val duration = mMediaPlayer?.duration
        progress?.max = duration ?: 100
        if (timer != null) {
            timer?.cancel()
        }
        duration?.toLong()?.let { setTimer(it) }
    }

    private fun setTimer(duration: Long) {
        timer = object : CountDownTimer(duration, 1000) {
            override fun onFinish() {
            }

            override fun onTick(millisUntilFinished: Long) {
                val temp = player?.currentPosition?.toLong()
                if (temp != null) {
                    progress?.progress = temp?.toInt()
                    playTime?.text = String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(temp) -
                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(temp)),
                            TimeUnit.MILLISECONDS.toSeconds(temp) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(temp)))
                }
            }

        }
        timer?.start()
    }

    @OnClick(R.id.button)
    fun button() {
        if (timer != null) {
            timer?.cancel()
        }
        if (player != null && player?.isPlaying == true) {
            button?.setImageResource(R.drawable.ic_play_arrow_black_24dp)
            isNeedPlay = false
            player?.pause()
        } else {
            player?.duration?.toLong()?.let { setTimer(it) }
            button?.setImageResource(R.drawable.ic_pause_black_24dp)
            isNeedPlay = true
            player?.start()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun continuePlay(continuePlay: ContinuePlay) {
        button()
    }

    private fun applyBlur() {
        image.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                image.viewTreeObserver.removeOnPreDrawListener(this)
                image.buildDrawingCache()
                val bmp = image.drawingCache
                blur(bmp, container)
                return true
            }
        })
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun blur(bkg: Bitmap, view: View) {
        val radius = 20f
        val overlay = Bitmap.createBitmap(view.measuredWidth as Int,
                view.measuredHeight as Int, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(overlay)
        canvas.translate((-view.left).toFloat(), (-view.top).toFloat())
        canvas.drawBitmap(bkg, 0f, 0f, null)
        val rs = RenderScript.create(this@MainActivity)
        val overlayAlloc = Allocation.createFromBitmap(rs, overlay)
        val blur = ScriptIntrinsicBlur.create(rs, overlayAlloc.element)
        blur.setInput(overlayAlloc)
        blur.setRadius(radius)
        blur.forEach(overlayAlloc)
        overlayAlloc.copyTo(overlay)
        view.background = BitmapDrawable(resources, overlay)
        rs.destroy()
    }

    fun removeBlur() {
        container?.background = null
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}