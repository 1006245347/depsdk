package com.lyentech.dep.function

import androidx.lifecycle.viewModelScope
import com.lyentech.lib.base.BaseVm
import com.lyentech.lib.utils.printD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Timer
import java.util.TimerTask
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
                }.flatMapConcat { token1 -> //可以串拼多个flow
                    reqInfo(token1)
                }
                .flatMapLatest { //未100毫秒就再发的就取消前一个
                    flow {
                        delay(100)
                        emit(it)
                    }
                }
                .flowOn(Dispatchers.IO)
                .collect { userInfo ->
                    printD(userInfo)  //水龙头 反向影响 水印发送速度
                }
        }
    }

    //https://blog.csdn.net/guolin_blog/article/details/127939641
    fun multi22() {
        //.zip 并行合并操作
        //背压策略  流速不均匀
        runBlocking {
            flow {
                emit(1)
            }.buffer()
                .conflate() //丢弃过期数据
                .collect {}
        }
    }

    //StateFlow 冷流类似LiveData,没有接收端flow不工作
//  粘性  如果在观察者还没有开始工作的情况下，发送者就已经先将消息发出来了，
//    稍后观察者才开始工作，那么此时观察者还应该收到刚才发出的那条消息
    private val _stateFlow = MutableStateFlow(0) //带初始值
    val stateFlow = _stateFlow.asStateFlow()

    fun startTimer() {
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {

            }
        }, 0, 1000)
    }

    val timeFlow2 = flow {
        var time = 0
        while (true) {
            emit(time)
            delay(1000)
            time++
        }
    }

    //stateIn函数将其他flow转 stateFlow
    val stateFlow2 = timeFlow2.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5),
        0
    )

    //要涉及非粘性，用MutableSharedFlow，消费者启动前都不保留数据
    private val _loginFlow = MutableSharedFlow<String>() //不需初始化
    val loginFlow = _loginFlow.asSharedFlow()
    fun startLogin() {
        viewModelScope.launch {
            _loginFlow.emit("suc")//发送没有value的方式
        }
    }
}