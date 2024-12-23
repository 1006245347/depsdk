package com.lyentech.dep.viewmodel

import com.lyentech.lib.base.BaseVm
import com.lyentech.lib.global.http.LoadStates
import com.lyentech.lib.global.http.RetrofitFactory
import com.lyentech.lib.utils.MockUtils
import com.lyentech.lib.utils.printD
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import org.json.JSONObject

class MainVm : BaseVm() {

    fun testGetUrl() {
        launch(state = LoadStates.Loading()) {
            delay(1500)
            gets()
        }
    }

    fun getObs() = execute<String> {
        gets()
    }

    suspend fun gets():String {
        val result = RetrofitFactory.doGetRequest(MockUtils.testGet)
//        printD("hi>>" + result)
        return  result
    }

    fun testJson() {
        launch (state = LoadStates.Loading()){

            val js = JSONObject()
            js.put("deviceSN", "F4911E69986F")
            js.put("position", "横屏")
            val result = RetrofitFactory.doPostJsonRequest(MockUtils.testPostJson, js.toString())
            printD(result)
        }
    }

    fun testFlow() {
//        flow<> {  }
    }
}