package com.lyentech.lib.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

/**
 * @author by jason-何伟杰，2023/1/5
 */
object CoroutineUtils {

    /*默认运行在子线程*/
    fun workInSub(
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        block: suspend CoroutineScope.() -> Unit
    ) = CoroutineScope(dispatcher).launch { block() }

    /*runBlocking会阻塞当前线程，再延时任务，再跑runBlocking后代码，整体看是按代码顺序执行*/
    fun subDelayTask(
        mills: Long = 2000,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        block: suspend CoroutineScope.() -> Unit
    ) = runBlocking {
        launch(dispatcher) {  //在作用域内开启子协程,当所有子协程都完成父协程才结束
            delay(mills)
            block()
        }
    }

    /*带超时限制协程*/
    suspend fun subLimitTask(mills: Long = 10000, block: suspend CoroutineScope.() -> Unit) =
        withTimeoutOrNull(mills) {
            block()
        }

    /*在子线程执行后，结束后给主线程*/
    fun asyncTask(sub: suspend CoroutineScope.() -> Unit, main: suspend CoroutineScope.() -> Unit) =
        CoroutineScope(Dispatchers.Default).launch {
            sub()
            withContext(Dispatchers.Main) {
                main()
            }
        }

    /*线程安全，线程遇到安全问题时我们一般有2种处理方案：一种是加锁，另外一种是使用线程安全的数据结构*/
    fun safeTask() = runBlocking {
        var n = AtomicInteger()
        val list = mutableListOf<Job>()
        repeat(100) {
            list.add(GlobalScope.launch {
                repeat(100) { n.incrementAndGet() }
            })
        }
        list.forEach {
            it.join()
        }
        printD("n = $n")   //实例结果=10000
    }

    fun unSafeTask() = runBlocking {
        var n = 0
        val list = mutableListOf<Job>()
        repeat(100) {
            list.add(GlobalScope.launch {
                repeat(100) { n++ }
            })
        }
        list.forEach {
            it.join()
        }
        printD("n2= $n")
    }

    //启动异步进行的并行任务
    suspend fun doAsyncTask() {
        withContext(Dispatchers.Default) {
            val cost = measureTimeMillis {
                val task1 = async (start=CoroutineStart.LAZY){ doTask1() }
                val task2 = async (start=CoroutineStart.LAZY){ doTask2() }
                task1.start()
                printD("Has Start?")
                val result1=task1.await() //start=CoroutineStart.LAZY ,只有结果通过 await 获取的时候协程才会启动
                val result2=task2.await()
                printD("result=${result1+result2} ${Thread.currentThread().name}")
            }
            printD("cost=$cost")
        }
    }

    suspend fun doTask1(): Int {
        delay(3000L)
        printD("running1>${System.currentTimeMillis()} ${Thread.currentThread().name}")
        return 9
    }

    suspend fun doTask2(): Int {
        delay(3000L)
        printD("running2>${System.currentTimeMillis()} ${Thread.currentThread().name}")
        return 33
    }
}