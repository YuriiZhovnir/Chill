package jdroidcoder.ua.chill.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.adapter.ViewPagerAdapter
import jdroidcoder.ua.chill.response.Category
import kotlinx.android.synthetic.main.fragment_sleep.*

/**
 * Created by jdroidcoder on 11.07.2018.
 */
class SleepFragment : BaseFragment() {
    companion object {
        fun newInstance(category: Category) = SleepFragment().apply {
            arguments = Bundle(1).apply {
                putSerializable(CATEGORY_KEY, category)
            }
        }
    }

    private var category: Category? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_sleep, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        category = arguments?.getSerializable(CATEGORY_KEY) as Category?
        val viewPagerAdapter = ViewPagerAdapter(childFragmentManager)
        category?.id?.let {
            CollectionFragment.newInstance(it, RESOURCE_CATEGORY, true)
        }?.let {
            viewPagerAdapter.addFragment(it, resources.getString(R.string.all_label))
        }
        category?.id?.let {
            CollectionFragment.newInstance(it, RESOURCE_FAVORITE, true)
        }?.let {
            viewPagerAdapter.addFragment(it, "")
        }
        category?.subcategories?.let {
            for (subcategory in it) {
                subcategory?.let { it1 ->
                    it1?.id?.let { it2 ->
                        CollectionFragment.newInstance(it2, RESOURCE_SUBCATEGORY, true)
                    }
                }?.let { it2 ->
                    subcategory?.name?.let { it1 ->
                        viewPagerAdapter.addFragment(it2, it1)
                    }
                }
            }
        }
        viewPager.adapter = viewPagerAdapter
        tabs.setupWithViewPager(viewPager)
        tabs.getTabAt(1)?.setCustomView(R.layout.text_view)
        for (i in 0 until tabs.tabCount) {
            val tab = (tabs.getChildAt(0) as ViewGroup).getChildAt(i)
            val p = tab.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(0, 0, 20, 0)
            tab.requestLayout()
        }
    }
}