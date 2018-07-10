package jdroidcoder.ua.chill.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jdroidcoder.ua.chill.ChillApp
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.activity.MainActivity
import jdroidcoder.ua.chill.adapter.HomeAdapter
import jdroidcoder.ua.chill.network.RetrofitSubscriber
import jdroidcoder.ua.chill.response.Home
import kotlinx.android.synthetic.main.fragment_home.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

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
        apiService.getHomeScreen()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(object : RetrofitSubscriber<Home>() {
                    override fun onNext(response: Home) {
                        recommendedList?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        recommendedList?.adapter = HomeAdapter(response.recommendedArray
                                ?: ArrayList(), activity as MainActivity)
//                        if(response?.continueArray?.isEmpty()==true){
//                            continueLabel?.text = resources?.getString(R.string.basic_label)
//                            continueList?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//                            continueList?.adapter = HomeAdapter(response.defaultArray
//                                    ?: ArrayList())
//                        }else{
//                            continueLabel?.text = resources?.getString(R.string.continue_meditating_label)
//                            continueList?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//                            continueList?.adapter = HomeAdapter(response.continueArray
//                                    ?: ArrayList())
//                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
    }
}