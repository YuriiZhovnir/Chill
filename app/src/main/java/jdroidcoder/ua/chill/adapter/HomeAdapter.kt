package jdroidcoder.ua.chill.adapter

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import jdroidcoder.ua.chill.ChillApp
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.event.PlayAudio
import jdroidcoder.ua.chill.fragment.MeditationPreviewFragment
import jdroidcoder.ua.chill.network.RetrofitConfig
import jdroidcoder.ua.chill.network.RetrofitSubscriber
import jdroidcoder.ua.chill.response.CollectionItem
import kotlinx.android.synthetic.main.home_item_style.view.*
import org.greenrobot.eventbus.EventBus
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by jdroidcoder on 10.07.2018.
 */
class HomeAdapter(var items: ArrayList<CollectionItem>) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        context = parent?.context
        return ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.home_item_style, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items?.get(position)
        Picasso.with(context).load(item.previewPhotoUrl).resizeDimen(R.dimen.size_160_dp, R.dimen.size_120_dp).into(holder.image)
        holder.title.typeface = ChillApp.demiFont
        holder.title.text = item.title
        holder.description.text = item.coverText
        holder.view.setOnClickListener {
            item.id?.let { it1 ->
                RetrofitConfig().adapter.getCollection(it1)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(object : RetrofitSubscriber<CollectionItem>() {
                            override fun onNext(response: CollectionItem) {
                                if (item?.isMeditation()) {
                                    val fragment = MeditationPreviewFragment.newInstance(response)
                                    (context as AppCompatActivity)?.supportFragmentManager?.beginTransaction()
                                            ?.add(android.R.id.content, fragment)
                                            ?.addToBackStack(fragment?.tag)
                                            ?.commit()
                                } else {
                                    EventBus.getDefault().post(PlayAudio(response))
                                }
                            }

                            override fun onError(e: Throwable) {
                                e.printStackTrace()
                            }
                        })
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var view = itemView.view
        var image = itemView.image
        var title = itemView.title
        var description = itemView.description
    }
}