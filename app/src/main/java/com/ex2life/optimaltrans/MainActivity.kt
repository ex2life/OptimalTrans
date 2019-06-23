package com.ex2life.optimaltrans

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL






class MainActivity : AppCompatActivity() {

    lateinit var textView: TextView
    lateinit var textView2: TextView
    lateinit var textView3: TextView
    lateinit var textView4: TextView
    lateinit var button: Button
    lateinit var emailTextView: TextView
    lateinit var passTextView: TextView
    lateinit var progressBar: ProgressBar
    val APP_PREFERENCES = "userauth"
    val APP_PREFERENCES_EMAIL = "Email"
    val APP_PREFERENCES_TOKEN = "Token"
    val APP_PREFERENCES_NAME = "Name"
    var mSettings: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView=findViewById(R.id.user_email)
        textView2=findViewById(R.id.textView2)
        textView3=findViewById(R.id.textView3)
        textView4=findViewById(R.id.textView4)
        button=findViewById(R.id.button)
        emailTextView=findViewById(R.id.emailText)
        passTextView=findViewById(R.id.passText)
        progressBar=findViewById(R.id.progressBar)
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        checkauth();
    }

    private fun checkauth() {
        if (mSettings!!.contains(APP_PREFERENCES_TOKEN)) AuthEnd()
    }

    fun AuthEnd() {
        // Создаем объект Intent для вызова новой Activity
        val intent = Intent(this, OsnovActivity::class.java)
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
            textView2.setVisibility(View.VISIBLE)
            progressBar.setVisibility(View.VISIBLE)
            emailTextView.setVisibility(View.GONE)
            textView3.setVisibility(View.GONE)
            textView4.setVisibility(View.GONE)
            passTextView.setVisibility(View.GONE)
            button.setVisibility(View.GONE)
        }

        override fun doInBackground(vararg params: Void?): String? {

            var urlConnection: HttpURLConnection? = null
            val email=emailTextView.text.toString()
            val editor = mSettings!!.edit()
            editor.putString(APP_PREFERENCES_EMAIL, email)
            editor.apply()
            val password=passTextView.text.toString()
            val urlParameters = "email=$email&pass=$password"

            try {
                val url = URL("https://abramizsaransk.000webhostapp.com/unprotected/auth_android")
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod="POST"
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

                textView.text="не2"
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect()
                }
            }
            return null
        }
        override fun onPostExecute(result: String?) {
            if (result=="false"){
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle("Не получилось:(")
                    .setMessage("Кажется, логин или пароль не верны.")
                    .setCancelable(false)
                    .setNegativeButton("ОК, уже исправляю",
                        DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
                val alert = builder.create()
                alert.show()
            }
            else{
                val json_source = result

                // выводим сырые данные без обработки
                //textView5.text = json_source

                try {
                    val json_arr = JSONObject(json_source)
                    val name_bd = json_arr.getString("name");
                    val token = json_arr.getString("token");
                    val editor = mSettings!!.edit()
                    editor.putString(APP_PREFERENCES_TOKEN, token)
                    editor.putString(APP_PREFERENCES_NAME, name_bd)
                    editor.apply()
                } catch (e: JSONException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, e.toString(), Toast.LENGTH_LONG)
                        .show()
                }
                AuthEnd()
            }
            textView2.setVisibility(View.GONE)
            progressBar.setVisibility(View.GONE)
            emailTextView.setVisibility(View.VISIBLE)
            textView3.setVisibility(View.VISIBLE)
            textView4.setVisibility(View.VISIBLE)
            passTextView.setVisibility(View.VISIBLE)
            button.setVisibility(View.VISIBLE)
        }

    }

}
