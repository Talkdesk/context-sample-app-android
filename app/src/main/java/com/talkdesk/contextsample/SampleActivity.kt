package com.talkdesk.contextsample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import org.json.JSONArray
import org.json.JSONObject

class SampleActivity : AppCompatActivity() {
    private lateinit var buttonSubmit: Button
    private lateinit var nameEditText: EditText
    private lateinit var issueEditText: EditText
    private lateinit var reservationNumberEditText: EditText
    private lateinit var phoneNumberEditText: EditText

    private val submitCallback = View.OnClickListener {
        val name = nameEditText.text.toString()
        val issue = issueEditText.text.toString()
        val reservationNumber = reservationNumberEditText.text.toString()
        val phoneNumber = phoneNumberEditText.text.toString()

        val json = generateRequestBody(name, issue, reservationNumber, phoneNumber)

        val httpRequestService = Intent(this, CallbackHttpIntentService::class.java)
        httpRequestService.putExtra(CallbackHttpIntentService.EXTRA_HTTP_BODY, json)
        httpRequestService.putExtra(CallbackHttpIntentService.EXTRA_HTTP_ENDPOINT, SampleActivity.CALLBACK_ENDPOINT)
        startService(httpRequestService)
    }

    private val apiCallResponseReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val response = intent!!.getParcelableExtra(CallbackHttpIntentService.EXTRA_HTTP_RESPONSE) as ApiResponse

            if (response.success) {
                Snackbar.make(
                    findViewById<ConstraintLayout>(R.id.activity_sample_container),
                    R.string.activity_sample_api_request_success_message,
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                Snackbar.make(
                    findViewById<ConstraintLayout>(R.id.activity_sample_container),
                    R.string.activity_sample_generic_error_message,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        buttonSubmit = findViewById(R.id.activity_sample_button_submit)
        nameEditText = findViewById(R.id.activity_sample_name_edit_text)
        issueEditText = findViewById(R.id.activity_sample_issue_edit_text)
        reservationNumberEditText = findViewById(R.id.activity_sample_reservation_number_edit_text)
        phoneNumberEditText = findViewById(R.id.activity_sample_phone_number_edit_text)
    }

    override fun onStart() {
        super.onStart()
        buttonSubmit.setOnClickListener(submitCallback)
        registerReceiver(apiCallResponseReceiver, IntentFilter(CallbackHttpIntentService.ACTION_API_RESPONSE))
    }

    override fun onStop() {
        buttonSubmit.setOnClickListener(null)
        unregisterReceiver(apiCallResponseReceiver)
        super.onStop()
    }

    private fun generateRequestBody(name: String, issue: String, reservationNumber: String, phoneNumber: String): String {
        val contextNameData = JSONObject()
        contextNameData.put("name", "name")
        contextNameData.put("display_name", "Name")
        contextNameData.put("tooltip_text", "Name")
        contextNameData.put("data_type", "text")
        contextNameData.put("value", name)

        val contextIssueData = JSONObject()
        contextIssueData.put("name", "issue")
        contextIssueData.put("display_name", "Issue")
        contextIssueData.put("tooltip_text", "Issue")
        contextIssueData.put("data_type", "text")
        contextIssueData.put("value", issue)

        val contextReservationNumberData = JSONObject()
        contextReservationNumberData.put("name", "reservation_number")
        contextReservationNumberData.put("display_name", "Reservation number")
        contextReservationNumberData.put("tooltip_text", "Reservation number")
        contextReservationNumberData.put("data_type", "text")
        contextReservationNumberData.put("value", reservationNumber)

        val contextField = JSONObject()
        contextField.put("fields", JSONArray(
            listOf(
                contextNameData,
                contextIssueData,
                contextReservationNumberData
            )
        ))

        val data = JSONObject()
        data.put("talkdesk_phone_number", BuildConfig.TALKDESK_PHONE_NUMBER)
        data.put("contact_phone_number", phoneNumber)
        data.put("context", contextField)

        return data.toString()
    }

    companion object {
        const val CALLBACK_ENDPOINT = "/calls/callback"
    }
}
