package com.lyentech.dep.ui

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lyentech.dep.R
import com.lyentech.dep.adapter.MainListAdapter
import com.lyentech.lib.base.BaseGActivity
import com.lyentech.lib.base.bindView
import com.lyentech.lib.base.delayUi
import com.lyentech.lib.base.toast
import com.lyentech.lib.global.common.UiHelper.clickFilter
import com.lyentech.lib.widget.shape.ShapeApi
import com.lyentech.lib.widget.shape.divider
import com.lyentech.lib.widget.shape.round
import com.lyentech.lib.widget.shape.setRoundEle

class MainActivity : BaseGActivity() {

    private lateinit var rvList: RecyclerView
    private lateinit var adapter: MainListAdapter
    override fun initAty() {
        rvList = bindView(R.id.rvList)
        rvList.layoutManager = LinearLayoutManager(this)
        adapter = MainListAdapter()
        adapter.submitList(menuList())
        rvList.adapter = adapter
        adapter.setOnItemClickListener { adapter, view, position ->
            adapter.getItem(position)?.let { toast(it) }
        }
        buildShape()
    }

    private fun menuList(): MutableList<String> {
        val list = mutableListOf<String>()
        list.add("reset>")
        list.add("get>")
        return list
    }

    private fun buildShape() {
        val v1 = bindView<View>(R.id.v1)
        val v2 = bindView<View>(R.id.v2)
        val v3 = bindView<View>(R.id.v3)
        val v4 = bindView<View>(R.id.v4)

        v1.background = ShapeApi.round(100f)

        v2.background = ShapeApi.roundX(40f, 40f, 10f, 10f)
        v3.background = ShapeApi.roundStroke(strokeColor = com.lyentech.lib.R.color.cYellowFFA900)
        v4.setRoundEle(eleColor = com.lyentech.lib.R.color.CRedF40C0C)

        val line = bindView<View>(R.id.line)
        delayUi {
            line.divider()
        }
        v1.clickFilter {
            ShapeApi.bottomEdge(16f, 50f, view = v1)
        }
        v2.clickFilter { v2.round(100f) }
        v3.clickFilter {  }
        v4.clickFilter { }
    }

    override fun buildEdge() {
        super.buildEdge()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }
}