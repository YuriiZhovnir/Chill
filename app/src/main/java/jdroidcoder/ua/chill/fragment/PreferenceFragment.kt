package jdroidcoder.ua.chill.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.activity.MainActivity
import jdroidcoder.ua.chill.adapter.PreferenceAdapter
import jdroidcoder.ua.chill.response.Preference
import kotlinx.android.synthetic.main.fragment_preference.*

/**
 * Created by jdroidcoder on 09.07.2018.
 */
class PreferenceFragment : BaseFragment() {
    companion object {
        fun newInstance(preferences: ArrayList<Preference>) = PreferenceFragment().apply {
            arguments = Bundle(1).apply {
                putParcelableArrayList(PREFERENCES_KEY, preferences)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_preference, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferences.adapter = PreferenceAdapter(context, arguments.getParcelableArrayList(PREFERENCES_KEY))
    }

    override fun onDestroyView() {
        try {
            (activity as MainActivity).removeBlur()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        super.onDestroyView()
    }
}