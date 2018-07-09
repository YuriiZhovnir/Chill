package jdroidcoder.ua.chill.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.fragment.PreferenceFragment
import jdroidcoder.ua.chill.network.RetrofitConfig
import jdroidcoder.ua.chill.network.RetrofitSubscriber
import jdroidcoder.ua.chill.response.Preference
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by jdroidcoder on 09.07.2018.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RetrofitConfig().adapter.getPreferences()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(object : RetrofitSubscriber<ArrayList<Preference>>() {
                    override fun onNext(response: ArrayList<Preference>) {
                        supportFragmentManager?.beginTransaction()
                                ?.replace(R.id.container, PreferenceFragment.newInstance(response))
                                ?.commit()
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
    }
}