package com.example.project3.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project3.R
import com.example.project3.models.Report
import com.example.project3.uiComponents.StatusChip

class AreaAdapter(list: ArrayList<Report>,context : Context) : RecyclerView.Adapter<AreaAdapter.MyViewHolder>() {
    var list: ArrayList<Report>
    var context : Context

    init{
        this.list = list
        this.context = context
    }

    inner class MyViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        val place = itemView.findViewById<TextView>(R.id.area_place)!!
        val date = itemView.findViewById<TextView>(R.id.area_date)!!
        val chip = itemView.findViewById<View>(R.id.chip)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.area_list_item,parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val report = list[position]

        val add = report.place.split(",").map{it.trim()}
        holder.place.text = "${add[0]}\n${add[1]}"
        holder.date.text = report.date
        val chip = StatusChip(holder.chip,context)
        if(report.algae>= report.dirty) chip.setText("algae")
        else chip.setText("dirty")
    }

    fun addItem(report: Report){
        list.add(report)
        notifyItemInserted(list.lastIndex+1)
    }
    fun clear(){
        list.clear()
        notifyItemRangeRemoved(0,list.size)
    }


}