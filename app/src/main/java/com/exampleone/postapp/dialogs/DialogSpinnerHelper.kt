package com.exampleone.postapp.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exampleone.postapp.R
import com.exampleone.postapp.utils.CityHelper

class DialogSpinnerHelper {
    fun showSpinnerDialog(context: Context, list: ArrayList<String>, tvSelection: TextView) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        val rootView = LayoutInflater.from(context).inflate(R.layout.spinner_layout, null)
        val adapter = RcViewDialogSpAdapter(tvSelection, dialog)
        val rcSpView = rootView.findViewById<RecyclerView>(R.id.rvSpView)
        val sv = rootView.findViewById<SearchView>(R.id.svSpinner)
        rcSpView.layoutManager = LinearLayoutManager(context)
        rcSpView.adapter = adapter
        dialog.setView(rootView)
        adapter.upDateAdapter(list)
        setSearchView(adapter, list, sv)
        dialog.show()
    }

    private fun setSearchView(
        adapter: RcViewDialogSpAdapter,
        list: ArrayList<String>,
        sv: SearchView?
    ) {
        sv?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val tempList = CityHelper.filterListData(list, newText)
                adapter.upDateAdapter(tempList)
                return true
            }
        })
    }
}