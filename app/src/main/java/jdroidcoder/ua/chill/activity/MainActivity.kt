package jdroidcoder.ua.chill.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.fragment.PreferenceFragment
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.drawable.BitmapDrawable
import android.renderscript.ScriptIntrinsicBlur
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.graphics.Bitmap
import android.os.Build
import android.annotation.TargetApi
import android.graphics.Canvas
import android.view.View
import android.view.ViewTreeObserver
import jdroidcoder.ua.chill.ChillApp
import jdroidcoder.ua.chill.fragment.HomeFragment

/**
 * Created by jdroidcoder on 09.07.2018.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (ChillApp.token?.getIsRegistration() == true) {
            applyBlur()
            val fragment = PreferenceFragment.newInstance()
            supportFragmentManager?.beginTransaction()
                    ?.replace(android.R.id.content, fragment)
                    ?.addToBackStack(fragment.tag)
                    ?.commit()

        }
        val fragment = HomeFragment.newInstance()
        supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container, fragment)
                ?.commit()
    }

    private fun applyBlur() {
        image.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                image.viewTreeObserver.removeOnPreDrawListener(this)
                image.buildDrawingCache()
                val bmp = image.drawingCache
                blur(bmp, container)
                return true
            }
        })
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun blur(bkg: Bitmap, view: View) {
        val radius = 20f
        val overlay = Bitmap.createBitmap(view.measuredWidth as Int,
                view.measuredHeight as Int, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(overlay)
        canvas.translate((-view.left).toFloat(), (-view.top).toFloat())
        canvas.drawBitmap(bkg, 0f, 0f, null)
        val rs = RenderScript.create(this@MainActivity)
        val overlayAlloc = Allocation.createFromBitmap(rs, overlay)
        val blur = ScriptIntrinsicBlur.create(rs, overlayAlloc.element)
        blur.setInput(overlayAlloc)
        blur.setRadius(radius)
        blur.forEach(overlayAlloc)
        overlayAlloc.copyTo(overlay)
        view.background = BitmapDrawable(resources, overlay)
        rs.destroy()
    }

    fun removeBlur() {
        container?.background = null
    }
}