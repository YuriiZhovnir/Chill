package jdroidcoder.ua.chill.fragment

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import butterknife.OnClick
import jdroidcoder.ua.chill.ChillApp
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.activity.MainActivity
import jdroidcoder.ua.chill.network.RetrofitSubscriber
import jdroidcoder.ua.chill.response.Token
import jdroidcoder.ua.chill.util.Utils
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by jdroidcoder on 21.07.2018.
 */
class ForgotPasswordFragment : BaseFragment() {
    companion object {
        fun newInstance(token: String? = "") = ForgotPasswordFragment().apply {
            arguments = Bundle(1).apply {
                putString(UPDATE_PASSWORD_TOKEN, token)
            }
        }
    }

    private var token: String? = ""

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appName.typeface = ChillApp.billabongFont
        preTerms.typeface = ChillApp.demiFont
        terms.typeface = ChillApp.demiFont
        alreadyAccount.typeface = ChillApp.demiFont
        login.typeface = ChillApp.demiFont
        continueButton.typeface = ChillApp.demiFont
        token = arguments?.getString(UPDATE_PASSWORD_TOKEN, "")
        if (!token.isNullOrEmpty()) {
            password?.visibility = View.VISIBLE
            confirmPassword?.visibility = View.VISIBLE
            email?.visibility = View.GONE
        }
    }

    @OnClick(R.id.continueButton)
    fun continueButton() {
        if (token.isNullOrEmpty()) {
            apiService?.forgotPassword(email?.text?.toString())
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.unsubscribeOn(Schedulers.io())
                    ?.doOnSubscribe(this::startLoading)
                    ?.doOnCompleted(this::stopLoading)
                    ?.subscribe(object : RetrofitSubscriber<Object>() {
                        override fun onNext(response: Object) {
                            Toast.makeText(context, resources?.getString(R.string.check_your_email), Toast.LENGTH_SHORT).show()
                            stopLoading()
                            activity?.supportFragmentManager?.popBackStack()
                        }

                        override fun onError(e: Throwable) {
                            stopLoading()
                            e.printStackTrace()
                        }
                    })
        } else {
            apiService?.updatePassword(token, Utils.getSignature(Utils.md5(password.text.toString())),
                    Utils.getSignature(Utils.md5(confirmPassword.text.toString())),
                    Settings.Secure.getString(activity?.contentResolver, Settings.Secure.ANDROID_ID))
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.unsubscribeOn(Schedulers.io())
                    ?.doOnSubscribe(this::startLoading)
                    ?.doOnCompleted(this::stopLoading)
                    ?.subscribe(object : RetrofitSubscriber<Token>() {
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

    @OnClick(R.id.login)
    fun login() {
        activity?.supportFragmentManager?.popBackStack()
    }
}