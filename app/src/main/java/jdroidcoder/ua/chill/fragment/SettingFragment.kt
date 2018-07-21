package jdroidcoder.ua.chill.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.OnClick
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.activity.StartActivity
import jdroidcoder.ua.chill.network.RetrofitSubscriber
import jdroidcoder.ua.chill.util.Utils
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by jdroidcoder on 21.07.2018.
 */
class SettingFragment : BaseFragment() {
    companion object {
        fun newInstance() = SettingFragment()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_setting, container, false)
    }

    @OnClick(R.id.logout)
    fun logout() {
        apiService?.logout()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.unsubscribeOn(Schedulers.io())
                ?.doOnSubscribe(this::startLoading)
                ?.subscribe(object : RetrofitSubscriber<Object>() {
                    override fun onNext(response: Object) {
                        Utils.saveToken(context, null)
                        startActivity(Intent(activity, StartActivity::class.java))
                        activity?.finish()
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        stopLoading()
                    }
                })
    }
}