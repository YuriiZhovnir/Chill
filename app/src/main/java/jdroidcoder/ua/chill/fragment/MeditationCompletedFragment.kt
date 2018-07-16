package jdroidcoder.ua.chill.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.OnClick
import com.squareup.picasso.Picasso
import jdroidcoder.ua.chill.ChillApp
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.event.UpdateFavorite
import jdroidcoder.ua.chill.network.RetrofitSubscriber
import jdroidcoder.ua.chill.response.Category
import jdroidcoder.ua.chill.response.CollectionItem
import jdroidcoder.ua.chill.response.Statistic
import kotlinx.android.synthetic.main.fragment_meditation_completed.*
import org.greenrobot.eventbus.EventBus
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

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
        Picasso.with(context)
                .load(collection?.previewPhotoUrl)
                .resizeDimen(R.dimen.size_60_dp, R.dimen.size_60_dp)
                .into(image)
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
                        } else {
                            collection?.isFavorite = 1
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
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