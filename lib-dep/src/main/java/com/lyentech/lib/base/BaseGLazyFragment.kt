package com.lyentech.lib.base

import android.os.Bundle
import android.view.View

abstract class BaseGLazyFragment : BaseGStatusFragment() {

    protected var isViewCreate = false
    protected var isLoadData = false
    protected var isFirstVisible = true

    //在createView后
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewCreate = true
        init(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (isFragmentVisible(this) && this.isAdded) {
            if (parentFragment == null || isFragmentVisible(parentFragment)) {
                onLazyInitData()
                isLoadData = true
                isFirstVisible = false
            }
        }
    }

    abstract fun onLazyInitData()

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isFragmentVisible(this) && !isLoadData && isViewCreate && isAdded) {
            onLazyInitData()
            isLoadData=true
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        //onHiddenChanged调用在Resumed之前，所以此时可能fragment被add, 但还没resumed
        if (!hidden && !this.isResumed)
            return
        //使用hide和show时，fragment的所有生命周期方法都不会调用，除了onHiddenChanged（）
        if (!hidden && isFirstVisible && this.isAdded) {
            onLazyInitData()
            isFirstVisible = false
        }
    }

}