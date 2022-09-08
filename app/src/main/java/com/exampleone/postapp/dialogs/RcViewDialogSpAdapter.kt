package com.exampleone.postapp.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.exampleone.postapp.R
import com.exampleone.postapp.act.EditAdsAct
import com.exampleone.postapp.databinding.SpListItemBinding

class RcViewDialogSpAdapter(var tvSelection: TextView, var dialog: AlertDialog) :
    RecyclerView.Adapter<RcViewDialogSpAdapter.SpViewHolder>() {

    val mainList = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.sp_list_item, parent, false)

        return SpViewHolder(view, tvSelection, dialog)
    }

    override fun onBindViewHolder(holder: SpViewHolder, position: Int) {
        holder.setData(mainList[position])
    }

    override fun getItemCount(): Int {
        return mainList.size
    }

    class SpViewHolder(
        itemView: View,
        var tvSelection: TextView,
        var dialog: AlertDialog
    ) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var itemText = ""
        fun setData(text: String) {
            val tvSpItem = itemView.findViewById<TextView>(R.id.tvSpItem)
            tvSpItem.text = text
            itemText = text
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
           tvSelection.text = itemText
            dialog.dismiss()
        }

    }

    fun upDateAdapter(list: ArrayList<String>) {
        mainList.clear()
        mainList.addAll(list)
        notifyDataSetChanged()
    }
}