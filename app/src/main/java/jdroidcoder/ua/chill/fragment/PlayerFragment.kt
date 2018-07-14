package jdroidcoder.ua.chill.fragment

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.OnClick
import com.squareup.picasso.Picasso
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.activity.MainActivity
import jdroidcoder.ua.chill.event.ContinuePlay
import jdroidcoder.ua.chill.response.CollectionItem
import kotlinx.android.synthetic.main.fragment_palyer.*
import org.greenrobot.eventbus.EventBus

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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_palyer, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val collection = arguments?.getSerializable(COLLECTION_KEY) as CollectionItem
        Picasso.with(context).load(collection.backgroundPhotoUrl).into(background)
        title.text = collection.collectionItems?.get(0).title
        author.text = "${collection.authors?.get(0).position?.name}: ${collection.authors?.get(0).fullName}"
        if (collection.isFavorite()) {
            favoriteIcon.setImageResource(R.drawable.ic_favorite_black_24dp)
        } else {
            favoriteIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp)
        }
        player = MainActivity.player
        if (player == null) {
            player = MediaPlayer()
            player?.setDataSource(activity?.applicationContext, Uri.parse(collection?.collectionItems?.get(0)?.audioUrl))
            player?.prepare()
            player?.setOnPreparedListener(this)
        } else {
            val duration = player?.duration
            progress?.max = duration ?: 100
        }
        player?.setOnCompletionListener {
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

    @OnClick(R.id.close)
    fun close() {
        activity?.supportFragmentManager?.popBackStack()
    }

    override fun onDestroyView() {
        player?.pause()
        if (MainActivity.isNeedPlay)
            EventBus.getDefault().post(ContinuePlay())
        super.onDestroyView()
    }
}