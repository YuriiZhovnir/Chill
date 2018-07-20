package jdroidcoder.ua.chill.network

import android.text.TextUtils
import jdroidcoder.ua.chill.ChillApp
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

/**
 * Created by jdroidcoder on 16.02.2018.
 */
class RetrofitConfig {
    companion object {
        public const val BASE_URL = "http://18.219.200.99/api/"
    }

    val adapter: ApiService
        get() {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(AuthInterceptor())
                    .build()

            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build()

            return retrofit.create(ApiService::class.java)
        }

    private class AuthInterceptor : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val token = ChillApp.token?.accessToken
            val original = chain.request()
            return if (!TextUtils.isEmpty(token)) {
                val requestBuilder = original.newBuilder()
                        .header("Authorization", "Bearer ${ChillApp.token?.accessToken}")
                        .method(original.method(), original.body())

                chain.proceed(requestBuilder.build())
            } else {
                chain.proceed(original)
            }
        }
    }
}