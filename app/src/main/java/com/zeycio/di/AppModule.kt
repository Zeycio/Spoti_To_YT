package com.zeycio.di

// com/musictransfer/di/AppModule.kt


import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.zeycio.data.api.SpotifyApi
import com.zeycio.data.api.YouTubeMusicApi
import com.zeycio.data.auth.AuthManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit.Builder {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    @Provides
    @Singleton
    fun provideSpotifyApi(retrofitBuilder: Retrofit.Builder): SpotifyApi {
        return retrofitBuilder
            .baseUrl("https://api.spotify.com/v1/")
            .build()
            .create(SpotifyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideYouTubeMusicApi(retrofitBuilder: Retrofit.Builder): YouTubeMusicApi {
        return retrofitBuilder
            .baseUrl("https://music.youtube.com/api/")
            .build()
            .create(YouTubeMusicApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    }


    @Provides
    @Singleton
    fun provideAuthManager(
        @ApplicationContext context: Context,
        sharedPreferences: SharedPreferences,
        okHttpClient: OkHttpClient
    ): AuthManager = AuthManager(context, sharedPreferences,okHttpClient)

    @Provides
    @Singleton
    fun provideAuthInterceptor(authManager: AuthManager): AuthInterceptor =
        AuthInterceptor(authManager)
}

class AuthInterceptor(private val authManager: AuthManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${authManager.getActiveToken()}")
            .build()
        return chain.proceed(request)
    }
}