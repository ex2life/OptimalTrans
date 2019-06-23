package com.ex2life.optimaltrans.adapter

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ex2life.optimaltrans.R
import kotlinx.android.synthetic.main.rout_list.view.*

class rout_adapter(val city_name:Array<String>): RecyclerView.Adapter<RoutCustomViewHolder>() {

    override fun getItemCount(): Int {
        return city_name.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutCustomViewHolder {
        val layoutInflater= LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.rout_list, parent, false)
        return RoutCustomViewHolder(cellForRow)
    }
    override fun onBindViewHolder(holder: RoutCustomViewHolder, position: Int) {
        holder.view.RoutCityName.text=city_name[position]
        if (position%2!=0){
            holder.view.setBackgroundColor(Color.rgb(215,215,215))
        }
    }

}

class RoutCustomViewHolder(val view: View, var city_name: String?=null, var city_id: String?=null): RecyclerView.ViewHolder(view){

//    companion object {
//        val CITY_TITLE_KEY="CITY_TITLE"
//        val CITY_ID_KEY="ID_TITLE"
//    }
//
//    init {
//        view.setOnClickListener {
//            println("Test")
//            val intent = Intent(view.context, after_auth::class.java)
//            intent.putExtra(CITY_TITLE_KEY, city_name)
//            intent.putExtra(CITY_ID_KEY, city_id)
//            //view.context.startActivity(intent)
//        }
//    }
}