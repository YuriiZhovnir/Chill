package jdroidcoder.ua.chill.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.OnClick
import com.squareup.picasso.Picasso
import jdroidcoder.ua.chill.ChillApp
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.event.UpdateFavorite
import jdroidcoder.ua.chill.network.RetrofitSubscriber
import jdroidcoder.ua.chill.response.Category
import jdroidcoder.ua.chill.response.CollectionItem
import kotlinx.android.synthetic.main.fragment_preview_meditation.*
import org.greenrobot.eventbus.EventBus
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import android.view.Gravity
import android.widget.FrameLayout
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.squareup.picasso.NetworkPolicy
import jdroidcoder.ua.chill.activity.MainActivity
import jdroidcoder.ua.chill.response.CollectionData
import jdroidcoder.ua.chill.util.Utils
import okhttp3.ResponseBody
import java.io.File

/**
 * Created by jdroidcoder on 14.07.2018.
 */
class MeditationPreviewFragment : BaseFragment() {
    companion object {
        fun newInstance(collectionItem: CollectionItem) = MeditationPreviewFragment().apply {
            arguments = Bundle(1).apply {
                putSerializable(COLLECTION_KEY, collectionItem)
            }
        }
    }

    private var collection: CollectionItem? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_preview_meditation, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collection = arguments?.getSerializable(COLLECTION_KEY) as CollectionItem
        currentDay?.typeface = ChillApp.demiFont
        if (Utils.isNetworkConnected(context)) {
            Picasso.with(context).load(collection?.backgroundPhotoUrl)
                    .resize(MainActivity.width, MainActivity.height)
                    .into(background)
        } else {
            Picasso.with(context).load(collection?.backgroundPhotoUrl)
                    .resize(MainActivity.width, MainActivity.height)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(background)
        }
        title.text = collection?.title
        previewText.text = collection?.coverText
        if (collection?.isFavorite() == true) {
            favorite.setImageResource(R.drawable.ic_favorite_black_24dp)
        } else {
            favorite.setImageResource(R.drawable.ic_favorite_border_black_24dp)
        }
        collection?.collectionItems?.let {
            for (day in it) {
                val tempView = LayoutInflater.from(context).inflate(R.layout.day_item_view, days, false)
                val textView = tempView.findViewById<TextView>(R.id.day)
                val ll = tempView.layoutParams as LinearLayout.LayoutParams
                ll.setMargins(10, 10, 10, 10)
                tempView.layoutParams = ll
                if (day.isEnded()) {
                    textView?.setBackgroundResource(R.drawable.ic_current_day_border)
                    textView?.setTextColor(Color.parseColor("#000000"))
                }
                textView?.text = day?.number?.toString() ?: "1"
                tempView?.setOnClickListener {
                    if (day?.isFree()) {
                        currentDay?.text = resources?.getString(R.string.day_label)?.let {
                            String.format(it, day?.number ?: 1)
                        }
                        duration?.text = resources?.getString(R.string.min_label)?.let {
                            String.format(it, day?.audioDuration?.toLong()?.let { it1 ->
                                TimeUnit.SECONDS.toMinutes(it1)
                            })
                        }
                        collection?.selectedDay = day
                    } else {
                        val fragment = SubscribeFragment.newInstance()
                        (context as AppCompatActivity)?.supportFragmentManager?.beginTransaction()
                                ?.add(android.R.id.content, fragment)
                                ?.addToBackStack(fragment?.tag)
                                ?.commit()
                    }
                }
                days.addView(tempView)
            }
        }
        daysContainer?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                daysContainer?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                val isScrolled = daysContainer?.measuredWidth?.minus(daysContainer?.getChildAt(0)?.width!!) ?: 0 < 0
                if (!isScrolled) {
                    val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                    params.gravity = Gravity.CENTER
                    days?.layoutParams = params
                }
            }
        })
        val currentData = collection?.collectionItems?.first { p -> !p.isEnded() }
        currentData?.isEnded = 1
        collection?.selectedDay = currentData
        currentDay?.text = resources?.getString(R.string.day_label)?.let {
            String.format(it, currentData?.number ?: 1)
        }
        duration?.text = resources?.getString(R.string.min_label)?.let {
            String.format(it, currentData?.audioDuration?.toLong()?.let { it1 ->
                TimeUnit.SECONDS.toMinutes(it1)
            })
        }
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
                            ?.doOnSubscribe(this::startLoading)
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
                                                collection?.collectionItems?.sortBy { p -> p.id }
                                                collection?.let {
                                                    if (!ChillApp?.offlineCollections?.contains(it))
                                                        ChillApp?.offlineCollections?.add(it)
                                                    Utils.saveDownloadedCollection(context, it)
                                                }
                                                stopLoading()
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

    @OnClick(R.id.button)
    fun button() {
        if (collection?.selectedDay?.isFree() == true) {
            MainActivity.player?.pause()
            MainActivity.player?.seekTo(0)
            collection?.let {
                if (!HomeFragment?.continueItems?.contains(it))
                    HomeFragment?.continueItems?.add(it)
            }
            collection?.id?.let {
                apiService?.startDay(it)
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
            close()
            val fragment = collection?.let { PlayerFragment.newInstance(it) }
            activity?.supportFragmentManager?.beginTransaction()
                    ?.add(android.R.id.content, fragment)
                    ?.addToBackStack(fragment?.tag)
                    ?.commit()
        } else {
            val fragment = SubscribeFragment.newInstance()
            (context as AppCompatActivity)?.supportFragmentManager?.beginTransaction()
                    ?.add(android.R.id.content, fragment)
                    ?.addToBackStack(fragment?.tag)
                    ?.commit()
        }
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
                            favorite.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                        } else {
                            collection?.isFavorite = 1
                            favorite.setImageResource(R.drawable.ic_favorite_black_24dp)
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
    }
}