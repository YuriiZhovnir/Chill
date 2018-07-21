package jdroidcoder.ua.chill.network

import jdroidcoder.ua.chill.response.*
import okhttp3.ResponseBody
import retrofit2.http.*
import rx.Observable

/**
 * Created by jdroidcoder on 16.02.2018.
 */
interface ApiService {
    @POST("auth/facebook-id/sign-in")
    @FormUrlEncoded
    fun facebookSignUp(@Field("device_id") deviceId: String,
                       @Field("first_name") firstName: String,
                       @Field("password") password: String,
                       @Field("email") email: String,
                       @Field("facebook_id") facebookId: String): Observable<Token>

    @POST("auth/email/sign-up")
    @FormUrlEncoded
    fun signUp(@Field("device_id") deviceId: String,
               @Field("first_name") firstName: String,
               @Field("password") password: String,
               @Field("email") email: String): Observable<Token>

    @POST("auth/email/sign-in")
    @FormUrlEncoded
    fun signIn(@Field("device_id") deviceId: String,
               @Field("password") password: String,
               @Field("email") email: String): Observable<Token>

    @POST("user/preferences/update")
    @FormUrlEncoded
    fun setPreferences(@Field("ids[]") ids: ArrayList<Int>): Observable<Object>

    @GET("collections/home")
    fun getHomeScreen(): Observable<Home>

    @GET("list/categories")
    fun getCategories(): Observable<ArrayList<Category>>

    @GET("collections/category/{category_id}")
    fun getCollectionFromCategory(@Path("category_id") categoryId: Int,
                                  @Query("page") page: Int): Observable<ArrayList<CollectionItem>>

    @GET("collections/subcategory/{subcategory_id}")
    fun getCollectionFromSubategory(@Path("subcategory_id") subcategoryId: Int,
                                    @Query("page") page: Int): Observable<ArrayList<CollectionItem>>

    @GET("collections/category/{category_id}/favorite")
    fun getFavorite(@Path("category_id") categoryId: Int,
                    @Query("page") page: Int): Observable<ArrayList<CollectionItem>>

    @GET("collection/{collection_id}")
    fun getCollection(@Path("collection_id") collectionId: Int): Observable<CollectionItem>

    @POST("collection/{collection_id}/favorite/add")
    fun addToFavorite(@Path("collection_id") collectionId: Int): Observable<ArrayList<Category>>

    @POST("collection/{collection_id}/favorite/remove")
    fun removeToFavorite(@Path("collection_id") collectionId: Int): Observable<ArrayList<Category>>

    @POST("user/meditation/{collection_id}/started")
    fun startDay(@Path("collection_id") collectionId: Int): Observable<Object>

    @POST("user/meditation/item/{collection_item_id}/ended")
    fun endDay(@Path("collection_item_id") collectionItemId: Int): Observable<Object>

    @GET("user/statistics")
    fun getStatistics(): Observable<Statistic>

    @POST("user/collection/{collection_id}/rating/update")
    @FormUrlEncoded
    fun feedback(@Path("collection_id") collectionId: Int,
                 @Field("rating") rating: Float): Observable<Object>

    @GET
    @Streaming
    fun download(@Url fileUrl: String): Observable<ResponseBody>

    @GET("user/history-of-month")
    fun getHistory(@Query("date_month") dateMonth: String): Observable<ArrayList<Statistic>>

    @POST("auth/email/forgot-password")
    @FormUrlEncoded
    fun forgotPassword(@Field("email") email: String?): Observable<Object>

    @POST("auth/email/reset-password")
    @FormUrlEncoded
    fun updatePassword(@Field("token") token: String?,
                       @Field("password") password: String?,
                       @Field("password_confirm") confirmPassword: String?,
                       @Field("device_id") deviceId: String?): Observable<Token>
}