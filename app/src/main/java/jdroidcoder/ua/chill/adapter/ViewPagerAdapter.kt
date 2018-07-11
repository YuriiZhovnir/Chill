package jdroidcoder.ua.chill.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.ViewGroup
import android.support.v4.app.FragmentStatePagerAdapter

/**
 * Created by jdroidcoder on 21.02.2018.
 */
class ViewPagerAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {
    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return mFragmentTitleList.get(position)
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any) {
        if (position >= count) {
            val manager = (`object` as Fragment).fragmentManager
            val trans = manager.beginTransaction()
            trans.remove(`object`)
            trans.commit()
        }
    }
}