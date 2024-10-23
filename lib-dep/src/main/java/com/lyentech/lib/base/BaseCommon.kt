package com.lyentech.lib.base

import android.content.Context
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.chad.library.adapter.base.BaseQuickAdapter
import com.lyentech.lib.global.common.UiHelper
import com.lyentech.lib.global.common.UiHelper.clickFilter
import kotlinx.coroutines.*
import org.json.JSONObject

/**
 * @author by jason-何伟杰，2022/12/19
 * des:只有AppCompatActivity有实现各类周期函数监听功能
 */
internal val curLifeScopeTaskMap = hashMapOf<Int, Job>()

/*Activity作用域下可控的延迟任务*/
fun FragmentActivity.delayUi(
    defaultDispatcher: CoroutineDispatcher = Dispatchers.Main,
    mills: Long = 1000L,
    cancelTag: Int = 1,
    block: suspend CoroutineScope.() -> Unit
) {
    val job = lifecycleScope.launch(defaultDispatcher) {
        delay(mills)
        block()
    }
    curLifeScopeTaskMap[cancelTag] = job //setTag
}

fun FragmentActivity.task(
    defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
    cancelTag: Int = 3,
    block: suspend CoroutineScope.() -> Unit
) {
    val job = lifecycleScope.launch(defaultDispatcher) { block() }
    curLifeScopeTaskMap[cancelTag] = job
}

fun FragmentActivity.uiTask(
    defaultDispatcher: CoroutineDispatcher = Dispatchers.Main,
    cancelTag: Int = 4,
    block: suspend CoroutineScope.() -> Unit
) {
    val job = lifecycleScope.launch(defaultDispatcher) { block() }
    curLifeScopeTaskMap[cancelTag] = job
}

fun Fragment.uiTask(
    defaultDispatcher: CoroutineDispatcher = Dispatchers.Main,
    cancelTag: Int = 4,
    block: suspend CoroutineScope.() -> Unit
) {
    val job = lifecycleScope.launch(defaultDispatcher) { block() }
    curLifeScopeTaskMap[cancelTag] = job
}

fun Fragment.task(
    defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
    cancelTag: Int = 3,
    block: suspend CoroutineScope.() -> Unit
) {
    val job = lifecycleScope.launch(defaultDispatcher) { block() }
    curLifeScopeTaskMap[cancelTag] = job
}

/*会把 lifecycleScope整个协程域相关的任务都停止*/
fun FragmentActivity.cancelDelay(cancelTag: Int = 1) {
    val job = curLifeScopeTaskMap[cancelTag]
    job?.cancel()
}

fun FragmentActivity.launchAty(context: Context, cls: Class<*>?) {
    UiHelper.switch2Aty(context, cls)
}

fun ViewModel.delayTask(
    defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
    mills: Long = 2000L,
    cancelTag: Int = 1,
    block: suspend CoroutineScope.() -> Unit
) {
    val job = viewModelScope.launch(defaultDispatcher) {
        delay(mills)
        block()
    }
    curLifeScopeTaskMap[cancelTag] = job //setTag
}

fun ViewModel.cancelDelay(cancelTag: Int = 1) {
    val job = curLifeScopeTaskMap[cancelTag]
    job?.cancel()
}

fun FragmentActivity.toast(text: String) {
    lifecycleScope.launch(Dispatchers.Main) {
        UiHelper.toast(text)
    }
}

fun Fragment.toast(text: String) {
    lifecycleScope.launch(Dispatchers.Main) {
        UiHelper.toast(text = text)
    }
}

fun FragmentActivity.tryTask(block: () -> Unit, errBlock: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
        errBlock()
    }
}

fun FragmentActivity.tryTask(block: () -> Unit) {
    tryTask(block) { }
}

/**过滤重复点击*/
fun FragmentActivity.clickFilter(@IdRes id: Int, mills: Long = 500, block: () -> Unit) {
    findViewById<View>(id).clickFilter(mills) { block() }
}

fun <T : View> FragmentActivity.bindView(@IdRes id: Int): T {
    return findViewById<T>(id)
}

fun BaseQuickAdapter<*, *>.setNewList(list: Collection<Any>?) {
    submitList(list as List<Nothing>)
}

fun BaseQuickAdapter<*, *>.addList(list: Collection<Any>?) {
    addAll(list as List<Nothing>)
}

fun JSONObject.toStr() :String{
    return this.toString().replace("\\","")
}







