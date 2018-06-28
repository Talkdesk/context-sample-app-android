package com.talkdesk.contextsample

import android.app.IntentService
import android.content.Intent
import android.util.Log
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class CallbackHttpIntentService : IntentService("CallbackHttpIntentService") {
    override fun onHandleIntent(intent: Intent?) {
        val httpClient = OkHttpClient()

        val body = intent!!.getStringExtra(EXTRA_HTTP_BODY)
        val endpoint = intent.getStringExtra(EXTRA_HTTP_ENDPOINT)

        val responseIntent = Intent(ACTION_API_RESPONSE)
        try {
            val request = Request.Builder()
                .url("${BuildConfig.API_BASE_URL}$endpoint")
                .post(RequestBody.create(MediaType.parse("application/json"), body.toByteArray()))
                .addHeader("Authorization", "Bearer ${BuildConfig.AUTH_TOKEN}")
                .build()

            val response = httpClient.newCall(request).execute()
            val responseBody = response.body()?.string()

            Log.d("SampleApp", "Server response: $responseBody")

            if (response.code() in 200..299) {
                responseIntent.putExtra(EXTRA_HTTP_RESPONSE, ApiResponse(true, responseBody!!))
            } else {
                handleError(responseIntent)
            }
        } catch (ex: Exception) {
            handleError(responseIntent)
        }

        sendBroadcast(responseIntent)
    }

    private fun handleError(responseIntent: Intent) {
        responseIntent.putExtra(EXTRA_HTTP_RESPONSE, ApiResponse(false))
    }

    companion object {
        const val ACTION_API_RESPONSE = "${BuildConfig.APPLICATION_ID}.action.API_RESPONSE"
        const val EXTRA_HTTP_BODY = "${BuildConfig.APPLICATION_ID}.extra.HTTP_BODY"
        const val EXTRA_HTTP_ENDPOINT = "${BuildConfig.APPLICATION_ID}.extra.HTTP_ENDPOINT"
        const val EXTRA_HTTP_RESPONSE = "${BuildConfig.APPLICATION_ID}.extra.HTTP_RESPONSE"
    }
}
