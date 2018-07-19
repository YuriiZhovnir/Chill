package jdroidcoder.ua.chill.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import butterknife.OnClick
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import jdroidcoder.ua.chill.ChillApp
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.event.UpdateFavorite
import jdroidcoder.ua.chill.network.RetrofitSubscriber
import jdroidcoder.ua.chill.response.Category
import jdroidcoder.ua.chill.response.CollectionData
import jdroidcoder.ua.chill.response.CollectionItem
import jdroidcoder.ua.chill.response.Statistic
import jdroidcoder.ua.chill.util.Utils
import kotlinx.android.synthetic.main.fragment_meditation_completed.*
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File

/**
 * Created by jdroidcoder on 16.07.2018.
 */
class MeditationCompletedFragment : BaseFragment() {
    companion object {
        fun newInstance(collection: CollectionItem) = MeditationCompletedFragment().apply {
            arguments = Bundle(1).apply {
                putSerializable(COLLECTION_KEY, collection)
            }
        }
    }

    private var collection: CollectionItem? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_meditation_completed, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        youDidIt?.typeface = ChillApp.demiFont
        collection = arguments?.getSerializable(COLLECTION_KEY) as CollectionItem
        if (Utils.isNetworkConnected(context)) {
            Picasso.with(context)
                    .load(collection?.previewPhotoUrl)
                    .resizeDimen(R.dimen.size_60_dp, R.dimen.size_60_dp)
                    .into(image)
        } else {
            Picasso.with(context)
                    .load(collection?.previewPhotoUrl)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .resizeDimen(R.dimen.size_60_dp, R.dimen.size_60_dp)
                    .into(image)
        }
        title?.text = collection?.title
        apiService.getStatistics()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(object : RetrofitSubscriber<Statistic>() {
                    override fun onNext(response: Statistic) {
                        mindful?.text = response?.mindfulDays?.toString()
                        current?.text = response?.currentStreak?.toString()
                        longest?.text = response?.longestStreak?.toString()
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
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
                            Toast.makeText(context, "Unfavorite", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Favorited", Toast.LENGTH_SHORT).show()
                            collection?.isFavorite = 1
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

    @OnClick(R.id.close)
    fun close() {
        HomeFragment?.continueItems.clear()
        HomeFragment?.defaultItems.clear()
        HomeFragment?.recommendedItems.clear()
        collection?.id?.let {
            rating?.rating?.let { it1 ->
                apiService.feedback(it, it1)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(object : RetrofitSubscriber<Object>() {
                            override fun onNext(response: Object) {

                            }

                            override fun onError(e: Throwable) {
                                e.printStackTrace()
                            }
                        })
            }
        }
        activity?.supportFragmentManager?.popBackStack()
    }
}