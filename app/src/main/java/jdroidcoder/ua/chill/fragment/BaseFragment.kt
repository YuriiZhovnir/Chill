package jdroidcoder.ua.chill.fragment

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.network.RetrofitConfig

/**
 * Created by jdroidcoder on 10.05.2018.
 */
abstract class BaseFragment : Fragment() {

    companion object {
        const val IS_LOGIN_KEY = "is_login"
        const val CATEGORY_KEY = "sleep_category"
        const val SUBCATEGORY_ID_KEY = "subcategory_id"
        const val RESOURCE_KEY = "resource"
        const val COLLECTION_KEY = "collection"
        const val IS_MEDITATION_KEY = "is_meditation"

        const val RESOURCE_CATEGORY = "category"
        const val RESOURCE_SUBCATEGORY = "subcategory"
        const val RESOURCE_FAVORITE = "favorite"
    }

    private var unbinder: Unbinder = Unbinder.EMPTY
    protected val apiService = RetrofitConfig().adapter
    private var progressDialog: ProgressDialog? = null
    private var isShowProgressDialog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        unbinder = ButterKnife.bind(this, view!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        progressDialog = ProgressDialog(context)
        progressDialog?.setTitle("")
        progressDialog?.setMessage("")
        progressDialog?.setCancelable(false)
        progressDialog?.isIndeterminate = false
        progressDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun startLoading() {
        if (!isShowProgressDialog && isAdded) {
            isShowProgressDialog = true
            progressDialog?.show()
            progressDialog?.setContentView(R.layout.custom_progress_layout)
        }
    }

    fun stopLoading() {
        isShowProgressDialog = false
        try {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog?.dismiss()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    @OnClick(R.id.empty)
    fun empty() {
    }
}