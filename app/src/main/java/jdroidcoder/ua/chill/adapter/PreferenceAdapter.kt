package jdroidcoder.ua.chill.adapter

import android.content.Context
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.fragment.PreferenceFragment
import jdroidcoder.ua.chill.model.PreferenceModel

/**
 * Created by jdroidcoder on 09.07.2018.
 */
class PreferenceAdapter(var context: Context, var preferences: ArrayList<PreferenceModel>,
                        var fragment: PreferenceFragment) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.preference_item_style, parent, false)
        view.findViewById<ImageView>(R.id.image).setImageResource(preferences?.get(position).imageId)
        view.findViewById<TextView>(R.id.name).text = preferences?.get(position)?.name
        val selector = view.findViewById<ImageView>(R.id.selectedIcon)
        view.findViewById<CardView>(R.id.view).setOnClickListener {
            if (selector.visibility == View.GONE) {
                fragment?.selectedIds.add(preferences?.get(position)?.id)
                selector.visibility = View.VISIBLE
            } else {
                fragment?.selectedIds.remove(preferences?.get(position)?.id)
                selector.visibility = View.GONE
            }
        }
        return view
    }

    override fun getItem(position: Int): Any {
        return preferences?.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return preferences?.size ?: 0
    }
}