package jdroidcoder.ua.chill.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.response.CollectionItem

/**
 * Created by jdroidcoder on 11.07.2018.
 */
class GridAdapter(var context: Context, private var collections: ArrayList<CollectionItem>) : BaseAdapter() {

    fun addItems(items:ArrayList<CollectionItem>){
        this.collections.addAll(items)
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.grid_item_style, parent, false)
        val collection = collections?.get(position)
        Picasso.with(context).load(collection?.previewPhotoUrl)
                .resizeDimen(R.dimen.size_150_dp, R.dimen.size_150_dp)
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
        return view
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