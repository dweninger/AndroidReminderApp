package com.example.finalattempt5.fragments.weekdays

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.finalattempt5.R
import com.example.finalattempt5.fragments.list.ListFragmentDirections
import com.example.finalattempt5.model.Task

class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var taskList = emptyList<Task>()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_row, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = taskList[position]
        holder.itemView.findViewById<TextView>(R.id.textDisplayStartTime).text = formatTime(currentItem.start_time)
        holder.itemView.findViewById<TextView>(R.id.textDisplayEndTime).text = formatTime(currentItem.end_time)
        holder.itemView.findViewById<TextView>(R.id.textDisplayDescription).text = currentItem.description
        holder.itemView.findViewById<TextView>(R.id.textDisplayTitle).text = currentItem.title
        holder.itemView.findViewById<TextView>(R.id.textDisplayDay).text = currentItem.weekday

        holder.itemView.findViewById<RelativeLayout>(R.id.rowLayout).setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToUpdateFragment(currentItem)
            holder.itemView.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    fun setData(tasks:List<Task>) {
        this.taskList = tasks
        notifyDataSetChanged()
    }
    fun formatTime(time: String): String {
        val parts = time.split(" : ")
        var hourString = parts[0]
        var minuteString = parts[1]
        var minute = minuteString.toInt()

        if(minute < 10) {
            minuteString = "0" + minute
        }

        return String.format("%s : %s", hourString, minuteString)
    }


}