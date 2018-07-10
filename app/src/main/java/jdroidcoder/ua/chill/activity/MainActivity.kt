package jdroidcoder.ua.chill.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.fragment.PreferenceFragment
import jdroidcoder.ua.chill.network.RetrofitConfig
import jdroidcoder.ua.chill.network.RetrofitSubscriber
import jdroidcoder.ua.chill.response.Preference
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.activity_main.*
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
                        Blurry.with(this@MainActivity)
                                .sampling(8)
                                .animate(500)
                                .async()
                                .capture(image)
                                .into(image)
                        val fragment = PreferenceFragment.newInstance(response)
                        supportFragmentManager?.beginTransaction()
                                ?.replace(R.id.container, fragment)
                                ?.addToBackStack(fragment.tag)
                                ?.commit()
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
    }

    fun removeBlur() {
        image?.setImageResource(R.drawable.login_background)
    }
}