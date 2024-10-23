package com.lyentech.lib.global.http

/**
 * @author jason-何伟杰，2020-01-10
 * des:网络状态切换
 */
sealed class LoadStates(val msg: String) {
    class Loading(msg: String = "") : LoadStates(msg)
    class LoadSuc(msg: String = "") : LoadStates(msg)
    class LoadFail(msg: String) : LoadStates(msg)
    class LoadRefresh(msg: String = "") : LoadStates(msg)
}