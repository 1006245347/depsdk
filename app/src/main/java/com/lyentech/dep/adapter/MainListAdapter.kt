package com.lyentech.dep.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.lyentech.dep.R
import com.lyentech.lib.widget.shape.roundStroke

class MainListAdapter : BaseQuickAdapter<String, QuickViewHolder>() {
    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: String?) {
        holder.getView<TextView>(R.id.tvDes).apply {
            text = item
            roundStroke()
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_main, parent)
    }
}