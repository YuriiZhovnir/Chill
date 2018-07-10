package jdroidcoder.ua.chill.network

import jdroidcoder.ua.chill.response.Token
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import rx.Observable

/**
 * Created by jdroidcoder on 16.02.2018.
 */
interface ApiService {
    @POST("api/auth/facebook-id/sign-in")
    @FormUrlEncoded
    fun facebookSignUp(@Field("device_id") deviceId: String,
                       @Field("first_name") firstName: String,
                       @Field("password") password: String,
                       @Field("email") email: String,
                       @Field("facebook_id") facebookId: String): Observable<Token>

    @POST("api/auth/email/sign-up")
    @FormUrlEncoded
    fun signUp(@Field("device_id") deviceId: String,
               @Field("first_name") firstName: String,
               @Field("password") password: String,
               @Field("email") email: String): Observable<Token>

    @POST("api/auth/email/sign-in")
    @FormUrlEncoded
    fun signIn(@Field("device_id") deviceId: String,
               @Field("password") password: String,
               @Field("email") email: String): Observable<Token>

    @POST("api/user/preferences/update")
    @FormUrlEncoded
    fun setPreferences(@Field("ids[]") ids: ArrayList<Int>): Observable<Object>
}