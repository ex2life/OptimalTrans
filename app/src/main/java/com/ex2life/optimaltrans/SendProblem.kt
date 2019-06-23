package com.ex2life.optimaltrans

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_send_problem.*
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class SendProblem : AppCompatActivity() {


    val APP_PREFERENCES = "userauth"
    val APP_PREFERENCES_EMAIL = "Email"
    val APP_PREFERENCES_TOKEN = "Token"

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_problem)
        assert(supportActionBar != null)   //null check
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        setTitle("Сообщение руководству");
    }

    fun logoutDel () {
        val editor = mSettings!!.edit()
        editor.remove(APP_PREFERENCES_EMAIL)
        editor.remove(APP_PREFERENCES_TOKEN)
        editor.remove(APP_PREFERENCES_NAME)
        editor.apply()
        val intent = Intent(this, MainActivity::class.java)
        // запуск activity
        startActivity(intent)
        finish();
    }

    fun postSe (view: View) {
        val catTask = postSeAsync()
        catTask.execute()
    }


    internal inner class postSeAsync : AsyncTask<Void, Void, String>() {


        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
        }

        override fun onPreExecute() {
            super.onPreExecute()
            editText.visibility=View.GONE
            textView10.visibility=View.GONE
            SendButton.visibility=View.GONE
            progressBar6.visibility=View.VISIBLE
            textView12.visibility=View.VISIBLE
        }

        override fun doInBackground(vararg params: Void?): String? {

            var urlConnection: HttpURLConnection? = null
            val problem = editText.text.toString()
            val email = mSettings!!.getString(APP_PREFERENCES_EMAIL, "");
            val token = mSettings!!.getString(APP_PREFERENCES_TOKEN, "");
            val urlParameters = "email=$email&problem=$problem&token=$token"

            try {
                val url = URL("https://abramizsaransk.000webhostapp.com/unprotected/new_problem")
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "POST"
                urlConnection.setDoOutput(true);
                val wr = DataOutputStream(urlConnection.getOutputStream())
                wr.writeBytes(urlParameters)  //пишем параметры в поток
                wr.flush()
                wr.close()
                val reader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                try {
                    val results = StringBuilder()
                    while (true) {
                        val line = reader.readLine()
                        if (line == null) break
                        results.append(line)
                    }
                    return results.toString()
                } finally {
                    reader.close()
                }
            } catch (ex: Exception) {

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect()
                }
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            if (result=="false"){
                val builder = AlertDialog.Builder(this@SendProblem)
                builder.setTitle("Не получилось:(")
                    .setMessage("Токен поломался или вы сменили пароль. Необходимо авторизоваться заново.")
                val alert = builder.create()
                alert.show()
                logoutDel()
            }else{

                progressBar6.visibility=View.GONE
                textView12.setText("Отправлено")
            }
        }
    }
}
