package jdroidcoder.ua.chill.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jdroidcoder.ua.chill.ChillApp
import jdroidcoder.ua.chill.R
import kotlinx.android.synthetic.main.fragment_meditation_completed.*

/**
 * Created by jdroidcoder on 16.07.2018.
 */
class MeditationCompletedFragment : BaseFragment() {
    companion object {
        fun newInstance() = MeditationCompletedFragment()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_meditation_completed, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        youDidIt?.typeface = ChillApp.demiFont
    }
}