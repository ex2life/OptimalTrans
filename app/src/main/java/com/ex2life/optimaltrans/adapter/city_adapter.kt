package com.ex2life.optimaltrans.adapter

import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ex2life.optimaltrans.R
import com.ex2life.optimaltrans.after_auth
import kotlinx.android.synthetic.main.city_list.view.*

class city_adapter(val city_name:Array<String>, val city_shop:Array<String>, val city_money:Array<String>, val city_id:Array<String>): RecyclerView.Adapter<CustomViewHolder>() {

    override fun getItemCount(): Int {
        return city_name.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.city_list, parent, false)
        return CustomViewHolder(cellForRow)
    }
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.view.CityName.text=city_name[position]
        holder.view.CityShop.text=city_shop[position]+" маг."
        holder.view.CityMoney.text=city_money[position]+" тыс.руб."
        if (position%2!=0){
            holder.view.setBackgroundColor(Color.rgb(215,215,215))
        }
        holder.city_name=city_name[position]
        holder.city_id=city_id[position]
    }

}

class CustomViewHolder(val view:View, var city_name: String?=null, var city_id: String?=null): RecyclerView.ViewHolder(view){

    companion object {
        val CITY_TITLE_KEY="CITY_TITLE"
        val CITY_ID_KEY="ID_TITLE"
    }

    init {
        view.setOnClickListener {
            println("Test")
            val intent = Intent(view.context, after_auth::class.java)
            intent.putExtra(CITY_TITLE_KEY, city_name)
            intent.putExtra(CITY_ID_KEY, city_id)
            //view.context.startActivity(intent)
        }
    }
}