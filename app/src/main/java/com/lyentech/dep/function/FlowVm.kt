package com.lyentech.dep.function

import androidx.lifecycle.viewModelScope
import com.lyentech.lib.base.BaseVm
import com.lyentech.lib.utils.printD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.function.ToLongFunction

class FlowVm : BaseVm() {

    //水源
    val timeFlow1 = flow {
        var time = 0
        while (true) {
            emit(time)
            delay(1000)
            time++
        }
    }

    //水龙头接受数据
    fun revResult() {
        viewModelScope.launch {
            timeFlow1.collect { time ->
                printD("time>$time")
                delay(3000)
            }
        }

        viewModelScope.launch {
            timeFlow1.collectLatest { time ->
                //只要最后一个值，切换不同的协程
            }
        }
    }

    //多个flow使用,操作符
    fun coverResult() {
        runBlocking {
            val flow = flowOf(1, 2, 3, 4, 5)
            flow.filter { it % 2 == 0 }
                .onEach { printD("遍历中间状态$it") }
                .map { it * it }.collect { printD("$it") }
        }
    }

    fun handleResult() {
        runBlocking {
            flow {
                emit(1)
                emit(2)
                delay(600)
                emit(3)
                delay(100)
                emit(4)
                delay(100)
                emit(5)
            }.debounce(500)//只有两条数据之间的间隔超过500毫秒才能发送
                .collect {
                    printD("$it")
                }
        }
    }

    fun multi1() {
        runBlocking {
            flowOf(1, 2, 3)
                .flatMapConcat {//依次发送数据
                    flowOf("a$it", "b$it") //传入第二个flow
                }.collect {}
        }
    }

    fun reqToken(): Flow<String> = flow {
        emit("token")
    }

    fun reqInfo(token: String): Flow<String> = flow {
        emit("userInfo")
    }

    fun multi11() { //获取token后拉取用户信息
        runBlocking {
            reqToken()  //flatMapMerge 并发执行 
                .flatMapConcat { token ->
                    reqInfo(token)
                }.flatMapConcat { token1-> //可以串拼多个flow
                    reqInfo(token1)
                }
                .flowOn(Dispatchers.IO)
                .collect { userInfo ->
                    printD(userInfo)
                }
        }
    }
}