package jdroidcoder.ua.chill.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.OnClick
import jdroidcoder.ua.chill.ChillApp
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.activity.MainActivity
import jdroidcoder.ua.chill.network.RetrofitSubscriber
import jdroidcoder.ua.chill.response.Token
import jdroidcoder.ua.chill.util.Utils
import kotlinx.android.synthetic.main.fragment_registration_login.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.dynamiclinks.ShortDynamicLink

/**
 * Created by jdroidcoder on 09.07.2018.
 */
class RegistrationLoginFragment : BaseFragment() {
    companion object {
        fun newInstance(isLogin: Boolean) = RegistrationLoginFragment().apply {
            arguments = Bundle(1).apply {
                putBoolean(IS_LOGIN_KEY, isLogin)
            }
        }
    }

    private var isLogin = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_registration_login, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appName.typeface = ChillApp.billabongFont
        preTerms.typeface = ChillApp.demiFont
        terms.typeface = ChillApp.demiFont
        alreadyAccount.typeface = ChillApp.demiFont
        login.typeface = ChillApp.demiFont
        continueButton.typeface = ChillApp.demiFont
        isLogin = arguments?.getBoolean(IS_LOGIN_KEY, false) ?: false
        if (isLogin) {
            welcome.text = resources?.getString(R.string.login)
            continueButton.text = resources?.getString(R.string.login)
            login.text = resources?.getString(R.string.forgot_password)
            alreadyAccount.visibility = View.GONE
            name.visibility = View.GONE
        }
//        FirebaseApp.initializeApp(activity)
//        FirebaseDynamicLinks.getInstance()
//                .getDynamicLink(activity.intent)
//                .addOnSuccessListener(activity, OnSuccessListener<PendingDynamicLinkData> { pendingDynamicLinkData ->
//                    var deepLink: Uri? = null
//                    if (pendingDynamicLinkData != null) {
//                        deepLink = pendingDynamicLinkData.link
//                    }else{
//                        println("dsadsa")
//                    }
//                })
//                .addOnFailureListener(activity, OnFailureListener { e -> e.printStackTrace() })
    }

    @OnClick(R.id.login)
    fun login() {
        if (!isLogin) {
            val fragment = RegistrationLoginFragment.newInstance(true)
            activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.container, fragment)
                    ?.addToBackStack(fragment.tag)
                    ?.commit()
        } else {
//            "https://chillapp.page.link"
//            val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
//                    .setLink(Uri.parse("http://chill.com"))
//                    .setDynamicLinkDomain("chillapp.page.link")
//                    .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())
//                    .setIosParameters(DynamicLink.IosParameters.Builder("chillapp.page.link").build())
//                    .buildDynamicLink()
//
//            val dynamicLinkUri = dynamicLink.uri
//            val shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
//                    .setLongLink(dynamicLinkUri)
//                    .buildShortDynamicLink()
//                    .addOnCompleteListener(activity, object : OnCompleteListener<ShortDynamicLink> {
//                        override fun onComplete(task: Task<ShortDynamicLink>) {
//                            if (task.isSuccessful) {
//                                // Short link created
//                                val shortLink = task.result.shortLink
//                                val flowchartLink = task.result.previewLink
//                                println("dsa")
//                            } else {
//                               task?.exception?.printStackTrace()
//                            }
//                        }
//                    })
        }
    }

    @OnClick(R.id.continueButton)
    fun continueButton() {
        val request = if (!isLogin) {
            apiService.signUp(Settings.Secure.getString(activity?.contentResolver, Settings.Secure.ANDROID_ID), name.text.toString(),
                    Utils.getSignature(Utils.md5(password.text.toString())), email.text.toString())
        } else {
            apiService.signIn(Settings.Secure.getString(activity?.contentResolver, Settings.Secure.ANDROID_ID),
                    Utils.getSignature(Utils.md5(password.text.toString())), email.text.toString())
        }
        request.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(this::startLoading)
                .doOnCompleted(this::stopLoading)
                .subscribe(object : RetrofitSubscriber<Token>() {
                    override fun onNext(response: Token) {
                        stopLoading()
                        Utils.saveToken(context, response.accessToken)
                        ChillApp.token = response
                        startActivity(Intent(context, MainActivity::class.java))
                        activity.finish()
                    }

                    override fun onError(e: Throwable) {
                        stopLoading()
                        e.printStackTrace()
                    }
                })
    }
}