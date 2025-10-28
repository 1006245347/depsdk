package com.lyentech.lib.global.common

import com.umeng.commonsdk.UMConfigure

object UmInitService {

    fun init(isDebug: Boolean = false) {
        UMConfigure.init(
            CoreApplicationProvider.appContext,
           "690078468560e34872d7c98d" , "Umeng", UMConfigure.DEVICE_TYPE_PHONE, ""
        )
        UMConfigure.setLogEnabled(isDebug)
    }
}