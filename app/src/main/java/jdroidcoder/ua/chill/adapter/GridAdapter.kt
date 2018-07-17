package jdroidcoder.ua.chill.adapter

import android.content.Context
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import jdroidcoder.ua.chill.ChillApp
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.event.PlayAudio
import jdroidcoder.ua.chill.fragment.MeditationPreviewFragment
import jdroidcoder.ua.chill.network.RetrofitConfig
import jdroidcoder.ua.chill.network.RetrofitSubscriber
import jdroidcoder.ua.chill.response.CollectionItem
import org.greenrobot.eventbus.EventBus
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by jdroidcoder on 11.07.2018.
 */
class GridAdapter(var context: Context, private var collections: ArrayList<CollectionItem>,
                  private var isMeditation: Boolean = false) : BaseAdapter() {

    fun addItems(items: ArrayList<CollectionItem>) {
        this.collections.addAll(items)
        notifyDataSetChanged()
    }

    fun clear() {
        collections.clear()
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.grid_item_style, parent, false)
        val collection = collections?.get(position)
        Picasso.with(context).load(collection?.previewPhotoUrl)
                .resizeDimen(R.dimen.size_150_dp, R.dimen.size_150_dp)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(android.R.color.white)
                .into(view.findViewById<ImageView>(R.id.image))
        if (collection.isFree()) {
            view.findViewById<ImageView>(R.id.lock).visibility = View.GONE
        } else {
            view.findViewById<ImageView>(R.id.lock).visibility = View.VISIBLE
        }
        if (collection.isNew()) {
            view.findViewById<TextView>(R.id.newLabel).visibility = View.VISIBLE
        } else {
            view.findViewById<TextView>(R.id.newLabel).visibility = View.GONE
        }
        view.setOnClickListener {
            collection.id?.let { it1 ->
                val offline = ChillApp?.offlineCollections?.find { p -> p.id == it1 }
                if (offline == null) {
                    RetrofitConfig().adapter.getCollection(it1)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .unsubscribeOn(Schedulers.io())
                            .subscribe(object : RetrofitSubscriber<CollectionItem>() {
                                override fun onNext(response: CollectionItem) {
                                    show(response)
                                }

                                override fun onError(e: Throwable) {
                                    e.printStackTrace()
                                }
                            })
                } else {
                    show(offline)
                }
            }
        }
        return view
    }

    private fun show(collection: CollectionItem) {
        if (isMeditation) {
            val fragment = MeditationPreviewFragment.newInstance(collection)
            (context as AppCompatActivity)?.supportFragmentManager?.beginTransaction()
                    ?.add(android.R.id.content, fragment)
                    ?.addToBackStack(fragment?.tag)
                    ?.commit()
        } else {
            EventBus.getDefault().post(PlayAudio(collection))
        }
    }

    override fun getItem(position: Int): Any {
        return collections?.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return collections?.size ?: 0
    }
}