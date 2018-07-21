package jdroidcoder.ua.chill.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.OnClick
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import jdroidcoder.ua.chill.ChillApp
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_subscribe.*

/**
 * Created by jdroidcoder on 21.07.2018.
 */
class SubscribeFragment : BaseFragment() {
    companion object {
        fun newInstance() = SubscribeFragment()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_subscribe, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribe?.typeface = ChillApp.demiFont
        join?.typeface = ChillApp.demiFont
    }

    @OnClick(R.id.subscribe)
    fun weekSubscribe() {
        val flowParams = BillingFlowParams.newBuilder()
                .setSku("test_1")
                .setType(BillingClient.SkuType.SUBS)
                .build()
        (activity as MainActivity)?.billingClient?.launchBillingFlow(activity, flowParams)
    }
}