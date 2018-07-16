package jdroidcoder.ua.chill.network

import jdroidcoder.ua.chill.response.*
import retrofit2.http.*
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

    @GET("api/collections/home")
    fun getHomeScreen(): Observable<Home>

    @GET("api/list/categories")
    fun getCategories(): Observable<ArrayList<Category>>

    @GET("api/collections/category/{category_id}")
    fun getCollectionFromCategory(@Path("category_id") categoryId: Int,
                                  @Query("page") page: Int): Observable<ArrayList<CollectionItem>>

    @GET("api/collections/subcategory/{subcategory_id}")
    fun getCollectionFromSubategory(@Path("subcategory_id") subcategoryId: Int,
                                    @Query("page") page: Int): Observable<ArrayList<CollectionItem>>

    @GET("api/collections/category/{category_id}/favorite")
    fun getFavorite(@Path("category_id") categoryId: Int,
                    @Query("page") page: Int): Observable<ArrayList<CollectionItem>>

    @GET("api/collection/{collection_id}")
    fun getCollection(@Path("collection_id") collectionId: Int): Observable<CollectionItem>

    @POST("api/collection/{collection_id}/favorite/add")
    fun addToFavorite(@Path("collection_id") collectionId: Int): Observable<ArrayList<Category>>

    @POST("api/collection/{collection_id}/favorite/remove")
    fun removeToFavorite(@Path("collection_id") collectionId: Int): Observable<ArrayList<Category>>

    @POST("api/user/meditation/{collection_id}/started")
    fun startDay(@Path("collection_id") collectionId: Int): Observable<Object>

    @POST("api/user/meditation/item/{collection_item_id}/ended")
    fun endDay(@Path("collection_item_id") collectionItemId: Int): Observable<Object>

    @GET("api/user/statistics")
    fun getStatistics(): Observable<Statistic>

    @POST("api/user/collection/{collection_id}/rating/update")
    @FormUrlEncoded
    fun feedback(@Path("collection_id") collectionId: Int,
                 @Field("rating") rating: Float): Observable<Object>
}