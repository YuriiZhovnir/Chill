package jdroidcoder.ua.chill.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.OnClick
import jdroidcoder.ua.chill.ChillApp
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.network.RetrofitSubscriber
import jdroidcoder.ua.chill.response.Profile
import jdroidcoder.ua.chill.response.Statistic
import kotlinx.android.synthetic.main.fragment_profile.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import sun.bob.mcalendarview.listeners.OnDateClickListener
import sun.bob.mcalendarview.listeners.OnMonthChangeListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import sun.bob.mcalendarview.vo.DateData

/**
 * Created by jdroidcoder on 20.07.2018.
 */
class ProfileFragment : BaseFragment() {
    companion object {
        fun newInstance() = ProfileFragment()
    }

    private var month = 0
    private var year = 0
    private var loadedData = ArrayList<Statistic>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        current?.typeface = ChillApp.demiFont
        mindful?.typeface = ChillApp.demiFont
        longest?.typeface = ChillApp.demiFont
        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        month = Calendar.getInstance().get(Calendar.MONTH) + 1
        year = Calendar.getInstance().get(Calendar.YEAR)
        monthYear?.text = SimpleDateFormat("MMMM' 'yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
        loadData(currentMonth)
        calender?.setOnMonthChangeListener(object : OnMonthChangeListener() {
            override fun onMonthChange(year: Int, month: Int) {
                this@ProfileFragment.month = month
                this@ProfileFragment.year = year
                val date = Calendar.getInstance(Locale.getDefault())
                date.set(Calendar.YEAR, year)
                date.set(Calendar.MONTH, month - 1)
                monthYear?.text = SimpleDateFormat("MMMM' 'yyyy", Locale.getDefault()).format(date.time)
                loadData(SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(date.time))
            }
        })
        calender?.setOnDateClickListener(object : OnDateClickListener() {
            override fun onDateClick(view: View?, date: DateData?) {
                val day = loadedData.find { p -> p.dateData == date }
                day?.let { setDayInfo(it) }
            }
        })
    }

    private fun setDayInfo(day: Statistic) {
        current?.text = day?.currentStreak?.toString()
        mindful?.text = day?.mindfulDays?.toString()
        longest?.text = day?.longestStreak?.toString()
        sessions?.removeAllViews()
        if (day?.sessionArray?.isNotEmpty() == true) {
            day?.sessionArray?.let {
                for (session in it) {
                    val view = LayoutInflater.from(context).inflate(R.layout.session_item, null)
                    view?.findViewById<TextView>(R.id.label)?.text = session?.title + "\n" +
                            String.format(resources.getString(R.string.day_label, session.number))
                    sessions.addView(view)
                }
            }
            sessions?.visibility = View.VISIBLE
            labelSession?.visibility = View.VISIBLE
        } else {
            sessions?.visibility = View.GONE
            labelSession?.visibility = View.GONE
        }
        stopLoading()
    }

    private fun loadData(date: String) {
        apiService?.getHistory(date)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.unsubscribeOn(Schedulers.io())
                ?.doOnSubscribe(this::startLoading)
                ?.subscribe(object : RetrofitSubscriber<Profile>() {
                    override fun onNext(response: Profile) {
                        response?.history?.add(Statistic(response?.statistics?.currentStreak,
                                response?.statistics?.mindfulDays, response?.statistics?.longestStreak,
                                SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss",
                                        Locale.getDefault()).format(Calendar.getInstance().time)))
                        initDays(response.history)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        stopLoading()
                    }
                })
    }

    private fun initDays(response: ArrayList<Statistic>) {
        val dates = ArrayList<DateData>()
        for (item in response) {
            val date = SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.getDefault()).parse(item.createdAt)
            val cal = Calendar.getInstance()
            cal.time = date
            val dateData = DateData(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
            item.dateData = dateData
            if (!loadedData.contains(item)) {
                loadedData.add(item)
            }
            dates.add(dateData)
        }
        for (day in dates) {
            calender?.markDate(day)
        }
        setDayInfo(response?.get(0))
        calender?.hasTitle(false)
        calender?.requestLayout()
    }

    @OnClick(R.id.previousMonth)
    fun previousMonth() {
        calender?.travelTo(DateData(year, month - 1, 1))
    }

    @OnClick(R.id.nextMonth)
    fun nextMonth() {
        calender?.travelTo(DateData(year, month + 1, 1))
    }
}