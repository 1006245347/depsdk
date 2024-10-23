package com.lyentech.lib.base

import android.os.Looper
import androidx.lifecycle.*

import com.lyentech.lib.global.http.LoadStates
import com.lyentech.lib.global.common.GlobalCode
import com.lyentech.lib.utils.printD
import kotlinx.coroutines.*

abstract class BaseVm() : ViewModel() {
    /**LiveData可以在某个字段更新发出通知，MutableLiveData是整个类型变量*/
    /**loadState控制界面加载状态切换，加载中，加载成功，加载失败*/
    val loadState = MutableLiveData<LoadStates>()

    fun launch(
        httpTag: Int = 1, dispatcher: CoroutineDispatcher = Dispatchers.IO,
        state: LoadStates = LoadStates.LoadSuc(),
        block: suspend CoroutineScope.() -> Unit
    ) {
        val job = viewModelScope.launch(dispatcher) {
            loadFun(state) { block() }
        }
        curLifeScopeTaskMap[httpTag] = job
        judgeAct()
    }

    /*无交互视图的后台任务*/
    fun launchQuiet(
        httpTag: Int = 22,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        block: suspend CoroutineScope.() -> Unit
    ) {
        val job = viewModelScope.launch(dispatcher) {
            block()
        }
        curLifeScopeTaskMap[httpTag] = job
    }

    fun <T> execute(
        httpTag: Int = 3, dispatcher: CoroutineDispatcher = Dispatchers.IO,
        block: suspend LiveDataScope<T>.() -> Unit
    ): LiveData<T> = liveData {
        val job = viewModelScope.launch(dispatcher) {
            loadFun { block() }
        }
        curLifeScopeTaskMap[httpTag] = job
        judgeAct()
    }

    /*显示等待视图的延迟任务*/
    fun loadingTask(
        defaultDispatcher: CoroutineDispatcher =
            Dispatchers.Default, mills: Long = 2000L,
        cancelTag: Int = 4,
        block: suspend CoroutineScope.() -> Unit
    ) {
        val job = viewModelScope.launch(defaultDispatcher) {
            val isMainThread = Thread.currentThread() == Looper.getMainLooper().thread
            if (isMainThread) {
                loadState.value = LoadStates.Loading()
            } else {
                loadState.postValue(LoadStates.Loading())
            }
            delay(mills)
            block()
            if (isMainThread) {
                loadState.value = LoadStates.LoadSuc()//要延迟后才执行
            } else {
                loadState.postValue(LoadStates.LoadSuc())
            }
        }
        curLifeScopeTaskMap[cancelTag] = job
    }

    fun getJobByTag(tag: Int): Job? {
        return curLifeScopeTaskMap[tag]
    }

    fun stopJob(tag: Int = 1, isMain: Boolean = true) {
        try {
            val job = getJobByTag(tag)
            printD("jobStop=$job")

            job?.cancel()
            handleErrLoad("was cancelled", isMain)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun loadFun(
        state: LoadStates = LoadStates.LoadSuc(),
        block: suspend () -> Unit
    ) {
        val isMainThread = Thread.currentThread() == Looper.getMainLooper().thread
        try {
//        printD("${isMainThread} main=${Looper.getMainLooper().thread} ${Thread.currentThread()} ${Looper.myLooper()==Looper.getMainLooper()}")
//            if (!NetUtil.isConnected(UiHelper.getContext())) {
//                if (state !is LoadStates.LoadRefresh) {
//                    if (isMainThread) {
//                        loadState.value = LoadStates.LoadFail("重新加载")
//                    } else {
//                        loadState.postValue(LoadStates.LoadFail("重新加载"))
//                    }
//                } else {
//                    if (isMainThread) {
//                        loadState.value = LoadStates.LoadFail("重新加载")
//                    } else {
//                        loadState.postValue(LoadStates.LoadFail("重新加载"))
//                    }
//                }
//                return
//            }
            if (state !is LoadStates.LoadRefresh) {
                if (isMainThread) {
                    loadState.value = state
                } else {
                    loadState.postValue(state)
                }
            }
            block()
            if (state !is LoadStates.LoadRefresh) {
                if (isMainThread) {
                    loadState.value = LoadStates.LoadSuc()
                } else {
                    loadState.postValue(LoadStates.LoadSuc())
                }
            } else {
                if (isMainThread) {
                    loadState.value = LoadStates.LoadRefresh()
                } else {
                    loadState.postValue(LoadStates.LoadRefresh())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            handleErrLoad("${e.message}", isMainThread, state)
        }
        judgeAct()
    }

    //判断网络状态行为
    private fun judgeAct() {
//        if (!NetUtil.isConnected(UiHelper.getContext())) {
//            UiHelper.toast(text = UiHelper.getString(R.string.load_failed_no_network))
//        }
    }

    //统一处理所有异常信息
    private fun handleErrLoad(
        str: String,
        isMain: Boolean = false,
        state: LoadStates = LoadStates.LoadSuc()
    ) {
//        printD("err=$str job:${GlobalCode.isJobErr(str)}")
        if (state is LoadStates.LoadRefresh) { //列表动画复原
            if (isMain) {
                loadState.value = LoadStates.LoadRefresh(str)
            } else {
                loadState.postValue(LoadStates.LoadRefresh(str))
            }
        } else { //普通请求loading
            if (GlobalCode.isJobErr(str)) {
                var tip = str
                if (str.contains("No value for data") || str.contains("ava.net.UnknownHostException")) { //当java.net.UnknownHostException
                    tip = "无法连接服务器"  //org.json.JSONException: No value for data
                }
                if (isMain) {
                    loadState.value = LoadStates.LoadFail(tip)
                } else {
                    loadState.postValue(LoadStates.LoadFail(tip))
                }
            } else {   //java.net.UnknownHostException 断网
                if (isMain) {
                    loadState.value = LoadStates.LoadSuc()
                } else {
                    loadState.postValue(LoadStates.LoadSuc())
                }
            }
        }
    }
}

//abstract class BaseVm() : ViewModel() {
//    /**LiveData可以在某个字段更新发出通知，MutableLiveData是整个类型变量*/
//    /**loadState控制界面加载状态切换，加载中，加载成功，加载失败*/
//    val loadState = MutableLiveData<LoadStates>()
//
//    fun launch(
//        httpTag: Int = 1, dispatcher: CoroutineDispatcher = Dispatchers.IO,
//        state: LoadStates = LoadStates.LoadSuc(),
//        block: suspend CoroutineScope.() -> Unit
//    ) {
//        val job = viewModelScope.launch(dispatcher) {
//            loadFun(state) { block() }
//        }
//        curLifeScopeTaskMap[httpTag] = job
//        judgeAct()
//    }
//
//    /*无交互视图的后台任务*/
//    fun launchQuiet(httpTag: Int = 2,dispatcher: CoroutineDispatcher = Dispatchers.IO, block: suspend CoroutineScope.() -> Unit) {
//        val job = viewModelScope.launch(dispatcher) {
//            block()
//        }
//        curLifeScopeTaskMap[httpTag] = job
//    }
//
//    fun <T> execute(
//        httpTag: Int = 3, dispatcher: CoroutineDispatcher = Dispatchers.IO,
//        block: suspend LiveDataScope<T>.() -> Unit
//    ): LiveData<T> = liveData {
//        val job = viewModelScope.launch(dispatcher) {
//            loadFun { block() }
//        }
//        curLifeScopeTaskMap[httpTag] = job
//        judgeAct()
//    }
//
//    /*显示等待视图的延迟任务*/
//    fun loadingTask(
//        defaultDispatcher: CoroutineDispatcher =
//            Dispatchers.Default, mills: Long = 2000L,
//        cancelTag: Int = 4,
//        block: suspend CoroutineScope.() -> Unit
//    ) {
//        viewModelScope.launch(Dispatchers.Main) {
//            loadState.value = LoadStates.Loading()
//            delayTask(defaultDispatcher, mills, cancelTag, block)
//            loadState.value = LoadStates.LoadSuc()//要延迟后才执行
//        }
//    }
//
//    fun getJobByTag(tag: Int): Job? {
//        return curLifeScopeTaskMap[tag]
//    }
//
//    fun stopJob(tag: Int = 1) {
//        val job = getJobByTag(tag)
//        job?.cancel()
//        handleErrLoad("was cancelled", true)
//    }
//
//    private suspend fun loadFun(
//        state: LoadStates = LoadStates.LoadSuc(),
//        block: suspend () -> Unit
//    ) {
//        val isMainThread = Thread.currentThread() == Looper.getMainLooper().thread
//        try {
//            if (state !is LoadStates.LoadRefresh) {
//                if (isMainThread) {
//                    loadState.value = state
//                } else {
//                    loadState.postValue(state)
//                }
//            }
//            block()
//            if (isMainThread) {
//                loadState.value = LoadStates.LoadSuc()
//            } else {
//                loadState.postValue(LoadStates.LoadSuc())
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            handleErrLoad("${e.message}", isMainThread, state)
//        }
//        judgeAct()
//    }
//
//    //判断网络状态行为
//    private fun judgeAct() {
//        if (!NetUtils.isConnected(UiHelper.getContext())) {
//            UiHelper.toast(text = UiHelper.getString(R.string.load_failed_no_network))
//        }
//    }
//
//    //统一处理所有异常信息
//    private fun handleErrLoad(
//        str: String,
//        isMain: Boolean = false,
//        state: LoadStates = LoadStates.LoadSuc()
//    ) {
//        printD("err=$str job:${GlobalCode.isJobErr(str)}")
//        if (state is LoadStates.LoadRefresh) { //列表动画复原
//            if (isMain) {
//                loadState.value = LoadStates.LoadRefresh(str)
//            } else {
//                loadState.postValue(LoadStates.LoadRefresh(str))
//            }
//        } else { //普通请求loading
//            if (GlobalCode.isJobErr(str)) {
//                if (isMain) {
//                    loadState.value = LoadStates.LoadFail(str)
//                } else {
//                    loadState.postValue(LoadStates.LoadFail(str))
//                }
//            } else {
//                if (isMain) {
//                    loadState.value = LoadStates.LoadSuc()
//                } else {
//                    loadState.postValue(LoadStates.LoadSuc())
//                }
//            }
//        }
//    }
//}