package com.lyentech.dep.ui

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lyentech.dep.R
import com.lyentech.dep.adapter.MainListAdapter
import com.lyentech.dep.viewmodel.MainVm
import com.lyentech.lib.base.BaseGActivity
import com.lyentech.lib.base.bindView
import com.lyentech.lib.base.delayUi
import com.lyentech.lib.base.toast
import com.lyentech.lib.bean.NetStatusBean
import com.lyentech.lib.global.common.UiHelper.clickFilter
import com.lyentech.lib.utils.printD
import com.lyentech.lib.widget.shape.ShapeApi
import com.lyentech.lib.widget.shape.divider
import com.lyentech.lib.widget.shape.round
import com.lyentech.lib.widget.shape.setRoundEle
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : BaseGActivity() {

    private lateinit var rvList: RecyclerView
    private lateinit var adapter: MainListAdapter
    private val vmMain: MainVm = createViewModel(MainVm()) as MainVm
    override fun initAty() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        rvList = bindView(R.id.rvList)
        rvList.layoutManager = LinearLayoutManager(this)
        adapter = MainListAdapter()
        adapter.submitList(menuList())
        rvList.adapter = adapter
        adapter.setOnItemClickListener { adapter, view, position ->
            adapter.getItem(position)?.let { toast(it) }
        }
        buildShape()
        vmMain.getObs().observe(this) {
            printD("aty>$it")
        }
    }

    private fun menuList(): MutableList<String> {
        val list = mutableListOf<String>()
        list.add("reset>")
        list.add("get>")
        list.add("post>")
        list.add("json>")
        list.add("loading>")
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
        v3.clickFilter { vmMain.testGetUrl() }
        v4.clickFilter { vmMain.testJson() }
    }

    //可控加载态和当前任务进度，如网络，进度，延时
    private fun buildHttp() {

    }

    override fun buildEdge() {
        super.buildEdge()
    }

    //生命周期响应协程的数据发送
    fun lifecycleDeal() {
        lifecycleScope.launch {
            //同样表示只有在Activity处于Started状态的情况下，协程中的代码才会执行。
            repeatOnLifecycle(Lifecycle.State.STARTED) {
            //每次都重新开始也不太好呀
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onNetEvent(netStatusBean: NetStatusBean) {
        if (netStatusBean.tag == NetStatusBean.SUC) {
            printD("s>")
        } else if (netStatusBean.tag == NetStatusBean.ERR) {
            printD("e>")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }
}