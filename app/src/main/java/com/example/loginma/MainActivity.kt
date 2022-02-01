package com.example.loginma

import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import android.view.WindowManager
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    val client = OkHttpClient()
    val FORM = MediaType.parse( "application/x-www-form-urlencoded")
    fun httpPost(url: String, body: RequestBody, success: (response: Response) -> Unit, failure: () -> Unit) {
        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Accept", "application/json")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                failure()

            }

            override fun onResponse(call: Call, response: Response) {
                success(response)
            }

            })
        }

    fun login(login: String, pass: String) {
        Toast.makeText(this, "Logowanie ($login;$pass)", Toast.LENGTH_SHORT).show()
        val url = "http://172.27.203.234:3000/login"
        val body = RequestBody.create(FORM, "username=" + login + "&password=" + pass)
        httpPost(url, body, fun(response: Response){
            Log.v("INFO", "Succeeded")
            val response_string = response.body()?.string()
            val json = JSONObject(response_string)
            if(json.has("message")) {
                this.runOnUiThread {
                    Toast.makeText(this, json["message"] as String, Toast.LENGTH_SHORT).show()
                }
            }
                else if (json.has("token")){
                    this.runOnUiThread(){
                        Toast.makeText(this, json["token"] as String, Toast.LENGTH_SHORT).show()
                    }
                }


            if (response_string != null) {
                Log.v("INFO", response_string)
            }
        },
        fun(){
            Log.v("INFO","Failed")
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (email_field.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
        login_btn.setOnClickListener {
            val login = email_field.text.toString()
            val pass = pass_field.text.toString()
            login(login, pass)


        }
    }
}