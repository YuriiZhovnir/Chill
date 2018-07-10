package jdroidcoder.ua.chill.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.facebook.CallbackManager
import jdroidcoder.ua.chill.ChillApp
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.fragment.StartFragment
import jdroidcoder.ua.chill.response.Token
import jdroidcoder.ua.chill.util.Utils

class StartActivity : AppCompatActivity() {
    companion object {
        var callbackManager: CallbackManager? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        callbackManager = CallbackManager.Factory.create()
        val token = Utils.loadToken(this)
        if (token == null) {
            supportFragmentManager?.beginTransaction()?.replace(R.id.container, StartFragment.newInstance())?.commit()
        } else {
            ChillApp.token = Token(token)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        try {
            callbackManager?.onActivityResult(requestCode, resultCode, data)
            super.onActivityResult(requestCode, resultCode, data)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}
