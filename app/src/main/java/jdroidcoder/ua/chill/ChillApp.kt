package jdroidcoder.ua.chill

import android.app.Application
import android.graphics.Typeface
import com.facebook.FacebookSdk
import jdroidcoder.ua.chill.response.Token

/**
 * Created by jdroidcoder on 09.07.2018.
 */
class ChillApp:Application(){
    companion object {
        var billabongFont:Typeface? = null
        var demiFont:Typeface? = null
        var token:Token? = null
    }

    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(this)
        billabongFont = Typeface.createFromAsset(assets,"Billabong.ttf")
        demiFont = Typeface.createFromAsset(assets,"AvenirNextLTPro-Demi.otf")
    }
}