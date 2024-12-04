package com.zeycio.data.auth


// AuthManager.kt


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.edit
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.zeycio.MainActivity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
@Singleton
class AuthManager @Inject constructor(
    private val context: Context,
    private val prefs: SharedPreferences,
    private val okHttpClient: OkHttpClient
) {
    companion object {
        private const val SPOTIFY_CLIENT_ID = "your_spotify_client_id"
        private const val SPOTIFY_CLIENT_SECRET = "your_spotify_client_secret"
        private const val YOUTUBE_CLIENT_ID = "your_youtube_client_id"
        private const val YOUTUBE_CLIENT_SECRET = "your_youtube_client_secret"
        internal const val REDIRECT_URI = "com.zeycio://callback"

        private const val PREF_SPOTIFY_TOKEN = "spotify_token"
        private const val PREF_YOUTUBE_TOKEN = "youtube_token"
    }

    fun startSpotifyAuth() {
        val authUrl = Uri.parse("https://accounts.spotify.com/authorize").buildUpon().apply {
            appendQueryParameter("response_type", "code")
            appendQueryParameter("client_id", SPOTIFY_CLIENT_ID)
            appendQueryParameter("scope", "playlist-read-private playlist-modify-public")
            appendQueryParameter("redirect_uri", REDIRECT_URI)
            appendQueryParameter("state", "spotify")
        }.build()

        launchAuthFlow(authUrl)
    }

    private val googleSignInClient by lazy {
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        )
    }

    fun signIn(activity: MainActivity) {
        activity.launchSignIn(googleSignInClient.signInIntent)
    }

    fun getLastSignedInAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    private fun launchAuthFlow(authUrl: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, authUrl).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addCategory(Intent.CATEGORY_BROWSABLE)
        }
        context.startActivity(intent)
    }

    suspend fun handleAuthResponse(uri: Uri): Boolean {
        val code = uri.getQueryParameter("code") ?: return false
        val state = uri.getQueryParameter("state") ?: return false

        return when (state) {
            "spotify" -> handleSpotifyToken(code)
            "youtube" -> handleYouTubeToken(code)
            else -> false
        }
    }

    private suspend fun handleSpotifyToken(code: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val requestBody = FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("redirect_uri", REDIRECT_URI)
                .add("client_id", SPOTIFY_CLIENT_ID)
                .add("client_secret", SPOTIFY_CLIENT_SECRET)
                .build()

            val request = Request.Builder()
                .url("https://accounts.spotify.com/api/token")
                .post(requestBody)
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body?.string()?.let {
                        val json = JSONObject(it)
                        val token = json.optString("access_token")
                        prefs.edit().putString(PREF_SPOTIFY_TOKEN, token).apply()
                        return@use true
                    }
                }
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun handleYouTubeToken(code: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val requestBody = FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("redirect_uri", REDIRECT_URI)
                .add("client_id", YOUTUBE_CLIENT_ID)
                .add("client_secret", YOUTUBE_CLIENT_SECRET)
                .build()

            val request = Request.Builder()
                .url("https://oauth2.googleapis.com/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(requestBody)
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body?.string()?.let {
                        val json = JSONObject(it)
                        val token = json.optString("access_token")
                        prefs.edit().putString(PREF_YOUTUBE_TOKEN, token).apply()
                        return@use true
                    }
                }
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getSpotifyToken(): String? = prefs.getString(PREF_SPOTIFY_TOKEN, null)
    fun getYouTubeToken(): String? = prefs.getString(PREF_YOUTUBE_TOKEN, null)
    fun isSpotifyConnected() = getSpotifyToken() != null
    fun isYouTubeConnected() = getYouTubeToken() != null

    fun getActiveToken(): String? = getSpotifyToken() ?: getYouTubeToken()
}
