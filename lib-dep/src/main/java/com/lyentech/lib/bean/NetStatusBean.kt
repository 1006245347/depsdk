package com.lyentech.lib.bean

class NetStatusBean {

    var tag: Int = 0

    var msg: String? = null

    constructor(tag: Int, msg: String? = null) {
        this.tag = tag
        this.msg = msg
    }

    companion object {
        const val SUC = 0
        const val ERR = 1
    }
}