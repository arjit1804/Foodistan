package com.example.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.activity.model.Faqs
import com.example.foodistaan.R

class FaqAdapter(val context:Context, val list: ArrayList<Faqs>):RecyclerView.Adapter<FaqAdapter.FaqViewHolder>() {

    class FaqViewHolder(view:View):RecyclerView.ViewHolder(view){

        val txtQuestion:TextView = view.findViewById(R.id.txtQuestion)
        val txtAnswer:TextView = view.findViewById(R.id.txtAnswer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_faqs_row,parent,false)

        return FaqViewHolder(view)
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {

        val faq = list[position]
        holder.txtQuestion.text = "Q"+((position+1).toString())+"."+faq.ques
        holder.txtAnswer.text = "A"+((position+1).toString())+"."+faq.ans

    }

    override fun getItemCount(): Int {
        return list.size
    }
}