package com.lyentech.dep

import com.lyentech.lib.global.common.CoreApplicationProvider
import com.lyentech.lib.global.common.UmInitService

class DSApp :CoreApplicationProvider() {

    override fun onCreate() {
        super.onCreate()
        UmInitService.init(true)
    }
}