package jdroidcoder.ua.chill.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.squareup.picasso.Picasso
import jdroidcoder.ua.chill.ChillApp
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.response.CollectionItem
import kotlinx.android.synthetic.main.fragment_preview_meditation.*
import java.util.concurrent.TimeUnit

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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_preview_meditation, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val collection = arguments?.getSerializable(COLLECTION_KEY) as CollectionItem
        currentDay?.typeface = ChillApp.demiFont
        Picasso.with(context).load(collection?.backgroundPhotoUrl).into(background)
        title.text = collection?.title
        previewText.text = collection?.coverText
        if (collection?.isFavorite()) {
            favoriteIcon.setImageResource(R.drawable.ic_favorite_black_24dp)
        } else {
            favoriteIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp)
        }
        for (day in collection?.collectionItems) {
            val tempView = LayoutInflater.from(context).inflate(R.layout.day_item_view, days, false)
            val textView = tempView.findViewById<TextView>(R.id.day)
            val ll = tempView.layoutParams as LinearLayout.LayoutParams
            ll.setMargins(10, 10, 10, 10)
            tempView.layoutParams = ll
            if (day.number <= collection?.endedCount ?: 1) {
                textView?.setBackgroundResource(R.drawable.ic_current_day_border)
                textView?.setTextColor(Color.parseColor("#000000"))
            }
            textView?.text = day?.number?.toString()
            days.addView(tempView)
        }
        val currentData = collection?.collectionItems?.first { p -> p.number == collection?.endedCount ?: 1 }
        currentDay?.text = resources?.getString(R.string.day_label)?.let { String.format(it, currentData?.number) }
        duration?.text = resources?.getString(R.string.min_label)?.let {
            String.format(it, currentData?.audioDuration?.toLong()?.let { it1 ->
                TimeUnit.SECONDS.toMinutes(it1)
            })
        }
    }
}