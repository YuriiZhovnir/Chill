package jdroidcoder.ua.chill.fragment

import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.OnClick
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import jdroidcoder.ua.chill.ChillApp
import jdroidcoder.ua.chill.R
import jdroidcoder.ua.chill.activity.StartActivity
import jdroidcoder.ua.chill.network.RetrofitSubscriber
import jdroidcoder.ua.chill.response.Token
import jdroidcoder.ua.chill.util.Utils
import kotlinx.android.synthetic.main.fragment_start.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

/**
 * Created by jdroidcoder on 09.07.2018.
 */
class StartFragment : BaseFragment(), FacebookCallback<LoginResult> {

    companion object {
        fun newInstance() = StartFragment()
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appName.typeface = ChillApp.billabongFont
        facebookLogin.typeface = ChillApp.demiFont
        emailRegistration.typeface = ChillApp.demiFont
        preTerms.typeface = ChillApp.demiFont
        terms.typeface = ChillApp.demiFont
        alreadyAccount.typeface = ChillApp.demiFont
        login.typeface = ChillApp.demiFont
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday"))
        loginButton.registerCallback(StartActivity.callbackManager, this)
    }

    @OnClick(R.id.login)
    fun login() {
        val fragment = RegistrationLoginFragment.newInstance(true)
        activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.container, fragment)
                ?.addToBackStack(fragment.tag)
                ?.commit()
    }

    @OnClick(R.id.emailRegistration)
    fun emailRegistration() {
        val fragment = RegistrationLoginFragment.newInstance(false)
        activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.container, fragment)
                ?.addToBackStack(fragment.tag)
                ?.commit()
    }

    @OnClick(R.id.facebookLogin)
    fun facebookLogin() {
        loginButton?.performClick()
    }

    override fun onSuccess(result: LoginResult?) {
        try {
            LoginManager.getInstance().logOut()
            val request = GraphRequest.newMeRequest(result?.accessToken, { `object`, resp ->
                if (resp != null && resp.error == null) {
                    val email = try {
                        `object`.getString("email")
                    } catch (ex: Exception) {
                        ""
                    }
                    val name = try {
                        `object`.getString("first_name")
                    } catch (ex: Exception) {
                        ""
                    }
                    val id = try {
                        `object`.getString("id")
                    } catch (ex: Exception) {
                        ""
                    }
                    apiService.facebookSignUp(Settings.Secure.getString(activity?.contentResolver,
                            Settings.Secure.ANDROID_ID), name, Utils.getSignature(Utils.md5(id)), email, id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .unsubscribeOn(Schedulers.io())
                            .subscribe(object : RetrofitSubscriber<Token>() {
                                override fun onNext(response: Token) {
                                    Utils.saveToken(context, response.accessToken)
                                    ChillApp.token = response
                                }

                                override fun onError(e: Throwable) {
                                    e.printStackTrace()
                                }
                            })
                }
            })
            val parameters = Bundle()
            parameters.putString("fields", "id,first_name,last_name,email,birthday")
            request.parameters = parameters
            request.executeAsync()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onCancel() {
    }

    override fun onError(error: FacebookException?) {
    }
}