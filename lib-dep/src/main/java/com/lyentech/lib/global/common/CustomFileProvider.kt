package com.lyentech.lib.global.common

import androidx.core.content.FileProvider
/**
 * @author by jason-何伟杰，2022/12/19
 * des:重写fileProvider可以解决多库冲突
 */
class CustomFileProvider : FileProvider() {
}