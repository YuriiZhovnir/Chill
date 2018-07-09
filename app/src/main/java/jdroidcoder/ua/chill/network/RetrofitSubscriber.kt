package jdroidcoder.ua.chill.network

import android.content.Context
import rx.Subscriber
import rx.exceptions.OnErrorFailedException

/**
 * Created by jdroidcoder on 16.02.2018.
 */
open class RetrofitSubscriber<T> : Subscriber<T>() {
    private var context: Context? = null

    override fun onNext(result: T) {}

    override fun onError(e: Throwable) {
        if (e is OnErrorFailedException) {
            e.printStackTrace()
        } else {
            onCompleted()
            e.printStackTrace()
        }
    }

    override fun onCompleted() {

    }
}