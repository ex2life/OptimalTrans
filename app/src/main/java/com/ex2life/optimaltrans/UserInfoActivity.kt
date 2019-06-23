package com.ex2life.optimaltrans

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.ex2life.optimaltrans.adapter.UserCustomViewHolder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_info.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class UserInfoActivity : AppCompatActivity() {


    lateinit var NameText: TextView
    lateinit var RoleText: TextView
    lateinit var MobileText: TextView
    lateinit var EmailText: TextView
    lateinit var user_id: String

    lateinit var NeedForm: ConstraintLayout
    lateinit var LoadForm: ConstraintLayout

    val APP_PREFERENCES = "userauth"
    val APP_PREFERENCES_EMAIL = "Email"
    val APP_PREFERENCES_TOKEN = "Token"
    val APP_PREFERENCES_NAME = "Name"

    var mSettings: SharedPreferences? = null

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
        assert(supportActionBar != null)   //null check
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        NameText = findViewById(R.id.NameText)
        MobileText = findViewById(R.id.MobileText)
        EmailText = findViewById(R.id.EmailText)
        RoleText = findViewById(R.id.RoleText)
        NeedForm=findViewById(R.id.NeedForm)
        LoadForm=findViewById(R.id.LoadForm)
        val user_title = intent.getStringExtra(UserCustomViewHolder.USER_TITLE_KEY)
        user_id = intent.getStringExtra(UserCustomViewHolder.USER_ID_KEY)
        NameText.setText(user_title)
        this.setTitle(user_title)
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        postSe()
    }

    fun logoutDel (view: View) {
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

    fun logoutDel2 () {
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

    fun postSe () {
        val catTask = postSeAsync()
        catTask.execute()
    }


    internal inner class postSeAsync : AsyncTask<Void, Void, String>() {


        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Void?): String? {

            var urlConnection: HttpURLConnection? = null

            val email=mSettings!!.getString(APP_PREFERENCES_EMAIL, "")
            val token=mSettings!!.getString(APP_PREFERENCES_TOKEN, "")

            val urlParameters = "userid=$user_id&email=$email&token=$token"

            try {
                val url = URL("https://abramizsaransk.000webhostapp.com/unprotected/user_info")
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod="POST"
                urlConnection.setDoOutput(true);
                val wr = DataOutputStream(urlConnection.getOutputStream())
                wr.writeBytes(urlParameters)
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

                //textView.text="не2"
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect()
                }
            }
            return null
        }

        override fun onPostExecute(result: String?) {

            if (result=="false"){
                val builder = AlertDialog.Builder(this@UserInfoActivity)
                builder.setTitle("Не получилось:(")
                    .setMessage("Токен поломался или вы сменили пароль. Необходимо авторизоваться заново.")
                val alert = builder.create()
                alert.show()
                logoutDel2()
            }else{
                val json_source = result


                try {
                    val json_obj = JSONObject(json_source)
                    val user_info=json_obj.getJSONArray("user_info").getJSONObject(0)
                    val user_role=json_obj.getJSONArray("user_role").getJSONObject(0)
                    val user_mobile=json_obj.getJSONArray("user_mobile").getJSONObject(0)
                    val avatar=json_obj.getJSONArray("avatar").getJSONObject(0)
                    NameText.setText(user_info.getString("name"))
                    RoleText.setText(user_role.getString("description"))
                    MobileText.setText("+"+user_mobile.getString("number"))
                    EmailText.setText(user_info.getString("email"))
                    Picasso.get()
                        .load("https://abramizsaransk.000webhostapp.com"+avatar.getString("path"))
                        .placeholder(R.drawable.ic_arrow_downward_black_24dp)
                        .error(R.drawable.ic_error_black_24dp)
                        .into(profile_image)

                    //val token = json_arr.getString("token");

                } catch (e: JSONException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                    Toast.makeText(this@UserInfoActivity, e.toString(), Toast.LENGTH_LONG)
                        .show()
                }
            }


            NeedForm.setVisibility(View.VISIBLE)
            LoadForm.visibility=View.GONE

        }

    }
}
