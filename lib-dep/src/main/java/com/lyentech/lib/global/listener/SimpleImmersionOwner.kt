package com.lyentech.lib.global.listener

interface SimpleImmersionOwner {
    /**
     * 初始化沉浸式代码
     * Init immersion bar.
     */
    fun initImmersionBar()

    /**
     * 是否可以实现沉浸式，当为true的时候才可以执行initImmersionBar方法
     * Immersion bar enabled boolean.
     *
     * @return the boolean
     */
    fun immersionBarEnabled(): Boolean
}