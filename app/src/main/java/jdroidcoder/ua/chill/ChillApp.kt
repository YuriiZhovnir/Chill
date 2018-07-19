package jdroidcoder.ua.chill

import android.app.Application
import android.graphics.Typeface
import com.facebook.FacebookSdk
import com.google.firebase.FirebaseApp
import jdroidcoder.ua.chill.response.Category
import jdroidcoder.ua.chill.response.CollectionItem
import jdroidcoder.ua.chill.response.Token

/**
 * Created by jdroidcoder on 09.07.2018.
 */
class ChillApp : Application() {
    companion object {
        var billabongFont: Typeface? = null
        var demiFont: Typeface? = null
        var token: Token? = null
        var category: ArrayList<Category>? = null
        var offlineCollections = ArrayList<CollectionItem>()
    }

    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(this)
        billabongFont = Typeface.createFromAsset(assets, "Billabong.ttf")
        demiFont = Typeface.createFromAsset(assets, "AvenirNextLTPro-Demi.otf")
        FirebaseApp.initializeApp(this)
    }
}