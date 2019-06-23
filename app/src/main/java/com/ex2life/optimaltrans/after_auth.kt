package com.ex2life.optimaltrans

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.ex2life.optimaltrans.adapter.CustomViewHolder
import com.ex2life.optimaltrans.adapter.city_adapter
import kotlinx.android.synthetic.main.activity_after_auth.*
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL







class after_auth : AppCompatActivity() {


    val APP_PREFERENCES = "userauth"
    val APP_PREFERENCES_EMAIL = "Email"
    val APP_PREFERENCES_TOKEN = "Token"
    lateinit var city_name:Array<String>
    lateinit var city_shop:Array<String>
    lateinit var city_money:Array<String>
    lateinit var city_id:Array<String>
    lateinit var textView5: TextView
    lateinit var progressBar: ProgressBar

    var mSettings: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_after_auth)
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        //CityView.setBackgroundColor(Color.BLUE)
        textView5=findViewById(R.id.textView5)
        progressBar=findViewById(R.id.progressBar2)
        CityView.layoutManager=LinearLayoutManager(this)
        city_name = emptyArray<String>()
        city_shop = emptyArray<String>()
        city_money = emptyArray<String>()
        city_id = emptyArray<String>()
        get_cities()
        val city_title=intent.getStringExtra(CustomViewHolder.CITY_TITLE_KEY)
        val city_id=intent.getStringExtra(CustomViewHolder.CITY_ID_KEY)
        this.setTitle(city_title)
    }

    fun get_cities () {
        val Task = postSeAsync()
        Task.execute()
    }

    fun stalin(view: View) {
        // Создаем объект Intent для вызова новой Activity
        val intent1 = Intent(this, OsnovActivity::class.java)
        // запуск activity
        startActivity(intent1)
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

            try {
                val url = URL("https://abramizsaransk.000webhostapp.com/unprotected/get_cities")
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod="POST"
                urlConnection.setDoOutput(true);
                val wr = DataOutputStream(urlConnection.getOutputStream())
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
            val json_source = result

            // выводим сырые данные без обработки
            //textView5.text = json_source

            try {
                val cities_arr = JSONArray(json_source)
                for (i in 0..cities_arr.length()-1) {
                    val arrayElement = cities_arr.getJSONObject(i);
                    //textView5.text =  textView5.text.toString()+arrayElement.getString("name") + " ";
                    city_name += arrayElement.getString("name")
                    city_shop += arrayElement.getString("count_shops")
                    city_money += arrayElement.getString("money")
                    city_id += arrayElement.getString("id")
                }
                textView5.setVisibility(View.GONE)
                progressBar.setVisibility(View.GONE)
                CityView.adapter=city_adapter(city_name, city_shop, city_money, city_id)


                //val results = jsonObject.getJSONObject("results")
                //val mySiteName = results.getString("sitename")
                //val myUrl = results.getString("url")

                //JsonObject.text = "\njsonObject:\n$jsonObject"
                //JsonResult.text = "\nresults:\n$results"
                //JsonMyName.text = "\nИмя сайта:\n$mySiteName"
                //JsonMyUrl.text = "\nАдрес сайта:\n$myUrl"

            } catch (e: JSONException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
                Toast.makeText(this@after_auth, e.toString(), Toast.LENGTH_LONG)
                    .show()
            }
        }

    }

    fun logoutDel (view: View) {
        val editor = mSettings!!.edit()
        editor.remove(APP_PREFERENCES_EMAIL)
        editor.remove(APP_PREFERENCES_TOKEN)
        editor.apply()
        val intent = Intent(this, MainActivity::class.java)
        // запуск activity
        startActivity(intent)
        finish();
    }
}
