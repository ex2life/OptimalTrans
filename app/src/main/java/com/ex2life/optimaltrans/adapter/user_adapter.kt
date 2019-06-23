package com.ex2life.optimaltrans.adapter

import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ex2life.optimaltrans.R
import com.ex2life.optimaltrans.UserInfoActivity
import kotlinx.android.synthetic.main.user_list.view.*



class user_adapter(val user_name:Array<String>, val user_email:Array<String>, val user_id:Array<String>): RecyclerView.Adapter<UserCustomViewHolder>() {
    override fun getItemCount(): Int {
        return user_name.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserCustomViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.user_list, parent, false)


        return UserCustomViewHolder(cellForRow)
    }
    override fun onBindViewHolder(holder: UserCustomViewHolder, position: Int) {
        holder.view.UserName.text=user_name[position]
        holder.view.UserEmail.text=user_email[position]
        if (position%2!=0){
            holder.view.setBackgroundColor(Color.rgb(215,215,215))
        }
        holder.user_name=user_name[position]
        holder.user_id=user_id[position]
    }
}

class UserCustomViewHolder(val view:View, var user_name: String?=null, var user_id: String?=null): RecyclerView.ViewHolder(view){

    companion object {
        val USER_TITLE_KEY="USER_TITLE"
        val USER_ID_KEY="ID_TITLE"
    }

    init {
        view.setOnClickListener {
            val intent = Intent(view.context, UserInfoActivity::class.java)
                        intent.putExtra(USER_TITLE_KEY, user_name)
                        intent.putExtra(USER_ID_KEY, user_id)
                        view.context.startActivity(intent)
        }
    }
}