package com.example.recyclesample

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    lateinit var context : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        context = this

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn).setOnClickListener {
            if (isConnectedInternet()) {
                ScoreScraper().execute()
            } else {
                Toast.makeText(this, "Not connected to internet", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun isConnectedInternet() : Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        return isConnected
    }

    internal inner class ScoreScraper : AsyncTask<Void, Void, String>() {
        lateinit var progressDialog : ProgressDialog

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Scraping Data...")
            progressDialog.setMessage("Please wait")
            progressDialog.setCancelable(true)
            progressDialog.setOnCancelListener {
                cancel(true)
            }
            progressDialog.show()

        }

        override fun doInBackground(vararg p0: Void?): String {
            //val intent = Intent(this@MainActivity, ScoreActivity::class.java)

            var formBody = FormBody.Builder()
                .add("username", findViewById<EditText>(R.id.main_username).text.toString())
                .add("password", findViewById<EditText>(R.id.main_password).text.toString())
                .build()
            var client = OkHttpClient.Builder()
                .connectTimeout(0, TimeUnit.DAYS)
                .writeTimeout(0, TimeUnit.DAYS)
                .readTimeout(0, TimeUnit.DAYS)
                .build()
            var link = "https://nilai-siak.herokuapp.com/scores_combines_result"
            var request = Request.Builder()
                .url(link)
                .post(formBody)
                .build()

            var new_resp = client.newCall(request).execute().body?.string().toString()

            return new_resp
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            progressDialog.dismiss()
            if (result != null) {
                if (result.contains("success")) {
                    val intent = Intent(context, ScoreActivity::class.java)
                    intent.putExtra("JSON_data", result)

                    // Send a date value.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val current = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("HH:mm, yyyy-MM-dd")
                        val formatted = current.format(formatter)
                        intent.putExtra("date_updated", "Updated on: " + formatted)
                    } else {
                        intent.putExtra("data_updated", "Updated on: Android version not supported")
                    }


                    startActivity(intent)
                } else {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Invalid Username / Password")
                    builder.setMessage("Please check your password or username again.")
                    builder.setPositiveButton("Ok", {
                        dialog, which -> {}
                    })
                    builder.create().show()
                }
            }

        }

    }
}