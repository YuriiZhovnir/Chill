package jdroidcoder.ua.chill.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.OnClick
import jdroidcoder.ua.chill.ChillApp
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.activity.MainActivity
import jdroidcoder.ua.chill.adapter.PreferenceAdapter
import jdroidcoder.ua.chill.model.PreferenceModel
import jdroidcoder.ua.chill.network.RetrofitSubscriber
import kotlinx.android.synthetic.main.fragment_preference.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by jdroidcoder on 09.07.2018.
 */
class PreferenceFragment : BaseFragment() {
    companion object {
        private val preference = ArrayList<PreferenceModel>()

        fun newInstance() = PreferenceFragment().apply {
            preference.add(PreferenceModel(1, "Learn to Meditate", R.drawable.ic_meditation))
            preference.add(PreferenceModel(2, "Build Self-Esteem", R.drawable.ic_esteem))
            preference.add(PreferenceModel(3, "Reduce Anxiety", R.drawable.ic_anxiety))
            preference.add(PreferenceModel(4, "Improve Focus", R.drawable.ic_focuse))
            preference.add(PreferenceModel(5, "Increase Happiness", R.drawable.ic_happiness))
            preference.add(PreferenceModel(6, "Sleep Better", R.drawable.ic_sleep))
            preference.add(PreferenceModel(7, "Develop Gratitude", R.drawable.ic_gratitude))
            preference.add(PreferenceModel(8, "Reduce stress", R.drawable.ic_stress))
        }
    }

    val selectedIds = ArrayList<Int>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_preference, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        continueButton?.typeface = ChillApp.demiFont
        preferences.adapter = PreferenceAdapter(context, preference, this)
    }

    @OnClick(R.id.continueButton)
    fun sendToServer() {
        apiService.setPreferences(selectedIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(object : RetrofitSubscriber<Object>() {
                    override fun onNext(response: Object) {
                        activity?.supportFragmentManager?.beginTransaction()?.remove(this@PreferenceFragment)?.commit()
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
    }
}