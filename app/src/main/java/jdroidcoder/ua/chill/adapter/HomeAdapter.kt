package jdroidcoder.ua.chill.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import jdroidcoder.ua.chill.ChillApp
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.activity.MainActivity
import jdroidcoder.ua.chill.response.CollectionItem
import kotlinx.android.synthetic.main.home_item_style.view.*
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by jdroidcoder on 10.07.2018.
 */
class HomeAdapter(var items: ArrayList<CollectionItem>, var activity: MainActivity) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
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
        Picasso.get().load(item.previewPhotoUrl).into(holder.image)
        holder.title.typeface = ChillApp.demiFont
        holder.title.text = item.title
        holder.description.text = item.coverText
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var view = itemView.view
        var image = itemView.image
        var title = itemView.title
        var description = itemView.description
    }
}