package jdroidcoder.ua.chill.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.ActivityCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import butterknife.OnClick
import com.squareup.picasso.Picasso
import jdroidcoder.ua.chill.ChillApp
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.activity.MainActivity
import jdroidcoder.ua.chill.event.ContinuePlay
import jdroidcoder.ua.chill.event.PlayAudio
import jdroidcoder.ua.chill.event.UpdateFavorite
import jdroidcoder.ua.chill.network.RetrofitSubscriber
import jdroidcoder.ua.chill.response.Category
import jdroidcoder.ua.chill.response.CollectionData
import jdroidcoder.ua.chill.response.CollectionItem
import jdroidcoder.ua.chill.util.Utils
import kotlinx.android.synthetic.main.fragment_palyer.*
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File

/**
 * Created by jdroidcoder on 14.07.2018.
 */
class PlayerFragment : BaseFragment(), MediaPlayer.OnPreparedListener {
    companion object {
        fun newInstance(collection: CollectionItem) = PlayerFragment().apply {
            arguments = Bundle(1).apply {
                putSerializable(COLLECTION_KEY, collection)
            }
        }
    }

    private var player: MediaPlayer? = null
    private var timer: CountDownTimer? = null
    private var collection: CollectionItem? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_palyer, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collection = arguments?.getSerializable(COLLECTION_KEY) as CollectionItem
        Picasso.with(context).load(collection?.backgroundPhotoUrl).into(background)
        if (collection?.isMeditation() == false) {
            author?.text = "${collection?.authors?.get(0)?.position?.name}: ${collection?.authors?.get(0)?.fullName}"
            currentDay?.visibility = View.GONE
            header?.visibility = View.GONE
            music?.visibility = View.GONE
            title.text = collection?.collectionItems?.get(0)?.title
        } else {
            author?.visibility = View.GONE
            val currentData = collection?.selectedDay
            currentDay?.text = resources?.getString(R.string.day_label)?.let { String.format(it, currentData?.number) }
            titleMeditation?.text = collection?.title
            title?.text = collection?.selectedDay?.title
            music?.visibility = View.VISIBLE
            val ll = favorite?.layoutParams as RelativeLayout.LayoutParams
            ll.addRule(RelativeLayout.ALIGN_RIGHT, mainButton.id)
        }
        if (collection?.isFavorite() == true) {
            favoriteIcon.setImageResource(R.drawable.ic_favorite_black_24dp)
        } else {
            favoriteIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp)
        }
        player = MainActivity.player
        if (player == null) {
            player = MediaPlayer()
            player?.setDataSource(activity?.applicationContext, if (collection?.isMeditation() == false) {
                Uri.parse(collection?.collectionItems?.get(0)?.audioUrl)
            } else {
                Uri.parse(collection?.selectedDay?.audioUrl)
            })
            player?.prepare()
            player?.setOnPreparedListener(this)
            MainActivity.player = player
        } else {
            val duration = player?.duration
            progress?.max = duration ?: 100
        }
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
                collection?.collectionItems?.find { p -> p.number == collection?.selectedDay?.number }?.isEnded = 1
                val isLast = collection?.collectionItems?.findLast { p -> !p.isEnded() }
                if (isLast == null) {
                    val fragment = collection?.let { MeditationCompletedFragment.newInstance(it) }
                    activity?.supportFragmentManager?.beginTransaction()
                            ?.add(android.R.id.content, fragment)
                            ?.addToBackStack(fragment?.tag)
                            ?.commit()
                }
            }

            button?.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        }
        button()
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
                val temp = MainActivity.player?.currentPosition?.toLong()
                if (temp != null) {
                    progress?.progress = temp?.toInt()
                }
            }
        }
        timer?.start()
    }

    @OnClick(R.id.forward)
    fun forward() {
        player?.currentPosition?.plus(15000)?.let { player?.seekTo(it) }
    }

    @OnClick(R.id.rewind)
    fun rewind() {
        player?.currentPosition?.minus(15000)?.let { player?.seekTo(it) }
    }

    @OnClick(R.id.button)
    fun button() {
        if (timer != null) {
            timer?.cancel()
        }
        if (player != null && player?.isPlaying == true) {
            button?.setImageResource(R.drawable.ic_play_arrow_black_24dp)
            player?.pause()
        } else {
            player?.duration?.toLong()?.let { setTimer(it) }
            button?.setImageResource(R.drawable.ic_pause_black_24dp)
            player?.start()
        }
    }

    @OnClick(R.id.music)
    fun music() {
        EventBus.getDefault().post(collection?.let {
            player?.currentPosition?.let { it1 ->
                PlayAudio(it, it1)
            }
        })
        close()
    }

    @OnClick(R.id.close)
    fun close() {
        activity?.supportFragmentManager?.popBackStack()
    }

    @OnClick(R.id.favorite)
    fun favorite() {
        val request = if (collection?.isFavorite() == true) {
            collection?.id?.let { apiService?.removeToFavorite(it) }
        } else {
            collection?.id?.let { apiService?.addToFavorite(it) }
        }
        request?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.unsubscribeOn(Schedulers.io())
                ?.subscribe(object : RetrofitSubscriber<ArrayList<Category>>() {
                    override fun onNext(response: ArrayList<Category>) {
                        EventBus.getDefault().post(UpdateFavorite())
                        if (collection?.isFavorite() == true) {
                            collection?.isFavorite = 0
                            favoriteIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                        } else {
                            collection?.isFavorite = 1
                            favoriteIcon.setImageResource(R.drawable.ic_favorite_black_24dp)
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
    }

    @OnClick(R.id.download)
    fun downloadButton() {
        if (context?.let { it1 -> ActivityCompat.checkSelfPermission(it1, Manifest.permission.READ_EXTERNAL_STORAGE) }
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context!!,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            download()
        } else {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    43)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 43 && (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            download()
        }
    }

    @SuppressLint("StaticFieldLeak")
    private fun download() {
        var count = 0
        collection?.collectionItems?.let {
            for (uri in it) {
                uri.audioUrl?.let { it1 ->
                    apiService?.download(it1)
                            ?.subscribeOn(Schedulers.io())
                            ?.observeOn(AndroidSchedulers.mainThread())
                            ?.unsubscribeOn(Schedulers.io())
                            ?.subscribe(object : RetrofitSubscriber<ResponseBody>() {
                                override fun onNext(response: ResponseBody) {
                                    object : AsyncTask<CollectionData, Void, CollectionData>() {
                                        override fun doInBackground(vararg params: CollectionData): CollectionData {
                                            val fileName = uri?.audioUrl?.lastIndexOf('/')
                                                    ?.plus(1)?.let { it2 ->
                                                        uri?.audioUrl?.substring(it2)
                                                    }
                                            fileName?.let { it2 ->
                                                Utils.writeResponseBodyToDisk(response, activity, it2)
                                            }
                                            collection?.collectionItems?.remove(params[0])
                                            params[0]?.audioUrl = context
                                                    ?.getExternalFilesDir(null).toString() +
                                                    File.separator + fileName
                                            collection?.collectionItems?.add(params[0])
                                            count++
                                            return params[0]
                                        }

                                        override fun onPostExecute(result: CollectionData?) {
                                            super.onPostExecute(result)
                                            if (count == collection?.collectionItems?.size) {
                                                collection?.collectionItems?.sortBy { p -> p.number }
                                                collection?.let {
                                                    ChillApp?.offlineCollections?.add(it)
                                                    Utils.saveDownloadedCollection(context, it)
                                                }
                                            }
                                        }
                                    }.execute(uri)
                                }

                                override fun onError(e: Throwable) {
                                    e.printStackTrace()
                                }
                            })
                }
            }
        }
    }

    override fun onDestroyView() {
        player?.pause()
        if (MainActivity.isNeedPlay)
            EventBus.getDefault().post(ContinuePlay())
        super.onDestroyView()
    }
}