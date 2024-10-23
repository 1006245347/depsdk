package com.lyentech.dep.adapter

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.lyentech.dep.R

class MainListAdapter : BaseQuickAdapter<String, QuickViewHolder>() {
    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: String?) {
        holder.setText(R.id.tvDes, item)
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_main, parent)
    }
}