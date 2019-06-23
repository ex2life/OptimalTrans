package com.ex2life.optimaltrans

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ex2life.optimaltrans.adapter.rout_adapter
import kotlinx.android.synthetic.main.fragment_rout.*
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

lateinit var city_name:Array<String>
val APP_PREFERENCES = "userauth"
val APP_PREFERENCES_EMAIL = "Email"
val APP_PREFERENCES_TOKEN = "Token"
val APP_PREFERENCES_NAME = "Name"
var mSettings: SharedPreferences? = null


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [RoutFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [RoutFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class RoutFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val let = arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        city_name = emptyArray<String>()
        RoutList.layoutManager= LinearLayoutManager(activity)

        mSettings = this.getActivity()!!.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        get_routs()
    }


    fun logoutDel () {
        val editor = mSettings!!.edit()
        editor.remove(APP_PREFERENCES_EMAIL)
        editor.remove(APP_PREFERENCES_TOKEN)
        editor.remove(APP_PREFERENCES_NAME)
        editor.apply()
        val intent = Intent(context, MainActivity::class.java)
        // запуск activity
        startActivity(intent)
        getActivity()!!.finish();
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RoutFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RoutFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun get_routs () {
        val Task = postSeAsync()
        Task.execute()
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
            val urlParameters = "email=$email&token=$token"
            try {
                val url = URL("https://abramizsaransk.000webhostapp.com/unprotected/user_rout")
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

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect()
                }
            }
            return null
        }
        override fun onPostExecute(result: String?) {
            if (result=="false"){
                val builder = AlertDialog.Builder(getActivity())
                builder.setTitle("Не получилось:(")
                    .setMessage("Токен поломался или вы сменили пароль. Необходимо авторизоваться заново.")
                val alert = builder.create()
                alert.show()
                logoutDel()
            }else {
                val json_source = result

                // выводим сырые данные без обработки
                //textView5.text = json_source

                try {
                    val routs_arr = JSONArray(json_source)
                    for (i in 0..routs_arr.length()-1) {
                        val arrayElement = routs_arr.getJSONObject(i);
                        //textView5.text =  textView5.text.toString()+arrayElement.getString("name") + " ";
                        city_name += arrayElement.getString("city")
                    }
                    if (city_name.count()==0){
                        city_name += "Нет маршрутов"
                        city_name += "Обратитесь к менеджеру"
                    }
                    RoutList.adapter= rout_adapter(city_name)
                    textView8.visibility=View.GONE
                    progressBar5.visibility=View.GONE
                    //textView6.visibility=View.GONE
                    //progressBar3.visibility=View.GONE

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
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

    }

}

