package jdroidcoder.ua.chill.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jdroidcoder.ua.chill.ChillApp
import jdroidcoder.ua.chill.R
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * Created by jdroidcoder on 10.07.2018.
 */
class HomeFragment : BaseFragment() {
    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title?.typeface = ChillApp.billabongFont
        continueLabel?.typeface = ChillApp.demiFont
        recommendedLabel?.typeface = ChillApp.demiFont
    }
}