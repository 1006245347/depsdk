package com.lyentech.lib.base

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.gyf.immersionbar.ImmersionBar

import com.lyentech.lib.global.listener.SimpleImmersionOwner
import com.lyentech.lib.R
import com.lyentech.lib.global.listener.SimpleImmersionProxy

/**
 * @author by jason-何伟杰，2023/1/5
 * des:当fragment有toolbar统一处理状态栏
 */
abstract class BaseGStatusFragment : BaseGFragment(), SimpleImmersionOwner {
    protected var statusBarView: View? = null
    protected var toolbar: Toolbar? = null

    override fun buildRootView(view: View): View {
        statusBarView = view.findViewById(R.id.status_bar_view)
        toolbar = view.findViewById(R.id.baseToolbar)
        fitsLayoutOverlap()
        return super.buildRootView(view)
    }

    private fun fitsLayoutOverlap() {
        if (statusBarView != null) {
            ImmersionBar.setStatusBarView(this, statusBarView)
        } else {
            ImmersionBar.setTitleBar(this, toolbar)
        }
    }

    override fun initImmersionBar() {
        ImmersionBar.with(this).statusBarColor(R.color.cBasic)
            .statusBarDarkFont(false)
            .keyboardEnable(false).init()
    }

    /**
     * ImmersionBar代理类
     */
    private val mSimpleImmersionProxy: SimpleImmersionProxy = SimpleImmersionProxy(this)
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        mSimpleImmersionProxy.isUserVisibleHint = isVisibleToUser
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mSimpleImmersionProxy.onActivityCreated(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSimpleImmersionProxy.onDestroy()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        mSimpleImmersionProxy.onHiddenChanged(hidden)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mSimpleImmersionProxy.onConfigurationChanged(newConfig)
        //旋转屏幕为什么要重新设置布局与状态栏重叠呢？因为旋转屏幕有可能使状态栏高度不一样，如果你是使用的静态方法修复的，所以要重新调用修复
        fitsLayoutOverlap()
    }

    /**
     * 是否可以实现沉浸式，当为true的时候才可以执行initImmersionBar方法
     * Immersion bar enabled boolean.
     *
     * @return the boolean
     */
    override
    fun immersionBarEnabled(): Boolean {
        return true
    }
}