package com.example.project3.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project3.R
import com.example.project3.models.ValueModel

class AnalysisRecAdapter(list: ArrayList<ValueModel>) : RecyclerView.Adapter<AnalysisRecAdapter.MyViewHolder>() {
    private val list : ArrayList<ValueModel>
    init {
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.analysis_list_item,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.name.text = item.name
        holder.value.text = item.value

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addItem(name : String,value : String){
        list.add(ValueModel(name, value))
        notifyItemInserted(list.lastIndex+1)
    }

    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val name : TextView
        val value : TextView
        init {
            name = itemView.findViewById(R.id.name)
            value = itemView.findViewById(R.id.value)
        }
    }

}