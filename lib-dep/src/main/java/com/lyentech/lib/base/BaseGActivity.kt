package com.lyentech.lib.base

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.lyentech.lib.global.http.LoadStates
import com.lyentech.lib.R
import com.lyentech.lib.global.common.GlobalCode
import com.lyentech.lib.global.common.UiHelper
import com.lyentech.lib.utils.printD
import com.lyentech.lib.widget.loading.GLoading
import com.lyentech.lib.widget.loading.LoadProgressAdapter
import kotlinx.coroutines.cancel

/**
 * @author by jason-何伟杰，2022/3/10
 * des:mmvm封装基类  组件化封装
 */
abstract class BaseGActivity : AppCompatActivity() {
    var toolBar: Toolbar? = null
    val globalVm = createViewModel(GlobalVm()) as GlobalVm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildEdge()
        setContentView(getLayoutId())
        initImmersionBar()

        initAty()
    }

    open fun buildEdge() {
        enableEdgeToEdge()
    }

    open fun initImmersionBar() {
        toolBar = findViewById(R.id.baseToolbar)
        setBarBackgroundColor()
        val ivBar = toolBar?.findViewById<ImageView>(R.id.iv_back)
        ivBar?.setOnClickListener { backAty() }
//        ImmersionBar.with(this)
//            .titleBar(toolBar) //标题栏
//            .statusBarDarkFont(true) //顶部状态栏字体
//            .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
//            .init() //底部导航栏
    }

    abstract fun initAty()
    abstract fun getLayoutId(): Int

    open fun createViewModel(t: BaseVm): BaseVm {
        t.loadState.observe(this) {
            when (it) {
                is LoadStates.LoadSuc -> showLoadSuc()
                is LoadStates.LoadFail -> showLoadErr(it.msg)
                is LoadStates.Loading -> showLoading(it.msg)
                else -> showLoadSuc()
            }
        }
        return t
    }

    open fun setBarTitle(txt: String, color: Int = UiHelper.getColor(R.color.cWhite)) {
        val tvBar = toolBar?.findViewById<TextView>(R.id.tv_bar_title)
        tvBar?.text = txt
        tvBar?.setTextColor(color)
    }

    open fun setBarRight(
        rightTxt: String,
        color: Int = UiHelper.getColor(R.color.cWhite),
        show: Int = View.VISIBLE
    ) {
        val tvBar = toolBar?.findViewById<TextView>(R.id.tv_bar_right)
        tvBar?.text = rightTxt
        tvBar?.setTextColor(color)
        tvBar?.visibility = show
        tvBar?.setOnClickListener { rightBarEvent() }
    }

    open fun setBarBackgroundColor(color: Int = UiHelper.getColor(R.color.cBasic)) {
        toolBar?.setBackgroundColor(color)
    }

    open fun rightBarEvent() {

    }

    open fun backAty() {
        finish()
    }

    var mLoading: GLoading.Holder? = null
    open fun initLoadingStatusViewIfNeed() {
        try {
            if (null == mLoading) {
                GLoading.initDefault(LoadProgressAdapter())
                val loading = GLoading.default
                mLoading =
                    loading?.wrap(this)?.withRetry { onLoadRetry() }?.withCancel { onLoadCancel() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**重新加载*/
    open fun onLoadRetry() {

    }

    /**取消请求*/
    open fun onLoadCancel(isCancel: Boolean = false) {
        if (isCancel)
            globalVm.viewModelScope.cancel()
        initLoadingStatusViewIfNeed()
        mLoading?.showLoadSuccess()
    }

    /**显示加载中*/
    open fun showLoading(tip: String?) {
        uiTask {
            initLoadingStatusViewIfNeed()
            mLoading?.showLoading(tip)
        }
    }

    /**显示加载成功*/
    open fun showLoadSuc() {
        uiTask {
            initLoadingStatusViewIfNeed()
            mLoading?.showLoadSuccess()
        }
    }

    /**显示加载失败*/
    open fun showLoadErr(str: String?) {
        uiTask {
            initLoadingStatusViewIfNeed()
            if (GlobalCode.isJobErr(str)) {
                mLoading?.withData(str)
                mLoading?.showLoadFailed(str)
            }
        }
        printD(mLoading?.getData<String>().toString())
    }

    /**显示加载的数据为空*/
    open fun showEmpty() {
        uiTask {
            initLoadingStatusViewIfNeed()
            mLoading?.showEmpty()
        }
    }

    fun getCurAty(): FragmentActivity {
        return this
    }

    override fun onDestroy() {
        super.onDestroy()
        /**取消ViewModel运行的所有协程所用的任务*/
        globalVm.viewModelScope.cancel()
    }
}