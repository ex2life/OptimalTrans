package com.ex2life.optimaltrans

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_osnov.*
import kotlinx.android.synthetic.main.app_bar_osnov.*
import kotlinx.android.synthetic.main.nav_header_osnov.view.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class OsnovActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    CityFragment.OnFragmentInteractionListener, UserFragment.OnFragmentInteractionListener, RoutFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    val APP_PREFERENCES = "userauth"
    val APP_PREFERENCES_EMAIL = "Email"
    val APP_PREFERENCES_TOKEN = "Token"
    val APP_PREFERENCES_NAME = "Name"
    var mSettings: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_osnov)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        val header = nav_view.getHeaderView(0)
        header.user_email.setText(mSettings!!.getString(APP_PREFERENCES_EMAIL, ""))
        header.user_name.setText(mSettings!!.getString(APP_PREFERENCES_NAME, ""))
        postSe()
        nav_view.setNavigationItemSelectedListener(this)
        nav_view.setCheckedItem(R.id.nav_routs);
        onNavigationItemSelected(nav_view.getMenu().findItem(R.id.nav_routs));

    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        try {
            when (item.itemId) {
                R.id.nav_routs -> {
                    val transaction = getSupportFragmentManager().beginTransaction()
                    transaction.replace(R.id.nuge, RoutFragment())
                    transaction.commit()
                    // Выделяем выбранный пункт меню в шторке
                    item.setChecked(true);
                    // Выводим выбранный пункт в заголовке
                    setTitle(item.getTitle());
                }
                R.id.nav_cities -> {
                    val transaction = getSupportFragmentManager().beginTransaction()
                    transaction.replace(R.id.nuge, CityFragment())
                    transaction.commit()
                    // Выделяем выбранный пункт меню в шторке
                    item.setChecked(true);
                    // Выводим выбранный пункт в заголовке
                    setTitle(item.getTitle());
                }
                R.id.nav_users -> {
                    val transaction = getSupportFragmentManager().beginTransaction()
                    transaction.replace(R.id.nuge, UserFragment())
                    transaction.commit()
                    // Выделяем выбранный пункт меню в шторке
                    item.setChecked(true);
                    // Выводим выбранный пункт в заголовке
                    setTitle(item.getTitle());
                }
                R.id.nav_send -> {
                    val intent = Intent(this, SendProblem::class.java)
                    startActivity(intent)
                }
            }
        } catch (e: Exception) {
            println("Exception handled")
        }


        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun dolgo_zato_kachestveno(){
        val builder = AlertDialog.Builder(this@OsnovActivity)
        builder.setTitle("Не получилось:(")
            .setMessage("Данная фича пока не поддерживается.")
            .setCancelable(false)
            .setNegativeButton("ОК, принял",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert = builder.create()
        alert.show()
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

            val email = mSettings!!.getString(APP_PREFERENCES_EMAIL, "");
            //val email = "abramizsaransk@gmail.com";
            val urlParameters = "email=$email"

            try {
                val url = URL("https://abramizsaransk.000webhostapp.com/unprotected/user_avatar")
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

                //textView.text="не2"
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect()
                }
            }
            return null
        }

        override fun onPostExecute(result: String?) {

            val json_source = result


            try {
                val json_obj = JSONObject(json_source)


                val avatar=json_obj.getJSONArray("avatar").getJSONObject(0)
                val header = nav_view.getHeaderView(0)
                Picasso.get()
                    .load("https://abramizsaransk.000webhostapp.com"+avatar.getString("path"))
                    .placeholder(R.drawable.ic_arrow_downward_black_24dp)
                    .error(R.drawable.ic_error_black_24dp)
                    .into(header.imageView)
                //val token = json_arr.getString("token");

            } catch (e: JSONException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
                Toast.makeText(this@OsnovActivity, e.toString(), Toast.LENGTH_LONG)
                    .show()
            }

        }

    }
}
