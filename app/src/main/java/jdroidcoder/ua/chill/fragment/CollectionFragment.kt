package jdroidcoder.ua.chill.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.adapter.GridAdapter
import jdroidcoder.ua.chill.network.RetrofitSubscriber
import jdroidcoder.ua.chill.response.CollectionItem
import jdroidcoder.ua.chill.util.EndlessScrollListener
import kotlinx.android.synthetic.main.fragment_collection.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by jdroidcoder on 11.07.2018.
 */
class CollectionFragment : BaseFragment() {
    companion object {
        fun newInstance(subcategoryId: Int, resourcesKey: String) = CollectionFragment().apply {
            arguments = Bundle(2).apply {
                putInt(SUBCATEGORY_ID_KEY, subcategoryId)
                putString(RESOURCE_KEY, resourcesKey)
            }
        }
    }

    private var resource: String? = null
    private var id: Int? = null
    private var adapder: GridAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_collection, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resource = arguments?.getString(RESOURCE_KEY)
        id = arguments?.getInt(SUBCATEGORY_ID_KEY, 0)
        adapder = GridAdapter(context, ArrayList())
        gridView?.adapter = adapder
        load(1)
        gridView?.setOnScrollListener(object : EndlessScrollListener() {
            override fun onLoadMore(page: Int, totalItemsCount: Int): Boolean {
                load(page)
                return true
            }
        })
    }

    private fun load(page: Int) {
        val request = when (resource) {
            RESOURCE_CATEGORY -> id?.let { apiService.getCollectionFromCategory(it, page) }
            RESOURCE_FAVORITE -> id?.let { apiService.getCollectionFromCategory(it, page) }
            else -> id?.let { apiService.getCollectionFromSubategory(it, page) }
        }

        request?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.unsubscribeOn(Schedulers.io())
                ?.subscribe(object : RetrofitSubscriber<ArrayList<CollectionItem>>() {
                    override fun onNext(response: ArrayList<CollectionItem>) {
                        adapder?.addItems(response)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
    }
}