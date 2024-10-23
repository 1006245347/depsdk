package com.lyentech.lib.utils

import com.coolerfall.download.DownloadCallback
import com.coolerfall.download.DownloadManager
import com.coolerfall.download.DownloadRequest
import com.coolerfall.download.Priority
import com.lyentech.lib.global.common.CoreApplicationProvider
import com.lyentech.lib.global.common.UiHelper
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @author by jason-何伟杰，2023/4/25
 * des:下载类库
 * 下载失败：1.下载链接问号后还带其他参数，不规则的截取
 *  2.长时间后台下载 3.断点下载偶尔报错
 */
class DownloadUtils {

    companion object {
        val manager: DownloadManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            DownloadManager.Builder().context(UiHelper.getContext()).threadPoolSize(1).build()
        }

        @JvmStatic
        fun startTask(url: String, rootDir: File? = null, block: (String, String) -> Unit): Int {
            try {
                val request =
                    DownloadRequest.Builder().url(url).progressInterval(2000, TimeUnit.MILLISECONDS)
                        .relativeFilepath(formatUrl(url)).retryTime(1)
                        .downloadCallback(object : DownloadCallback {
                            private var startTimeStamp: Long = 0
                            private var startSize: Long = 0
                            override fun onStart(downloadId: Int, totalBytes: Long) {
                                super.onStart(downloadId, totalBytes)
                                startTimeStamp = System.currentTimeMillis()
                            }

                            override fun onProgress(
                                downloadId: Int, bytesWritten: Long, totalBytes: Long
                            ) {
                                super.onProgress(downloadId, bytesWritten, totalBytes)
//                                val progress = (bytesWritten * 100f / totalBytes).toInt()
//                                if (progress % 3 == 0 || progress % 4 == 0)
//                                    printD("progress-$progress")
//                                val speed: Int
//                                val currentTimeStamp = System.currentTimeMillis()
//                                val deltaTime = (currentTimeStamp - startTimeStamp + 1).toInt()
//                                speed =
//                                    ((bytesWritten - startSize) * 1000 / deltaTime).toInt() / 1024
//                                startSize = bytesWritten
//                                printD("speed-$speed kb/s")
                            }

                            override fun onSuccess(downloadId: Int, filepath: String) {
                                super.onSuccess(downloadId, filepath)
                                //suc-/storage/emulated/0/Android/data/com.lyentech.ark/files/Download/f6f09ff717b64564ed52b3563b63f812.mp4
                                printD("suc-$filepath")
                                MMKVUtils.addStr(formatUrl(url), filepath)
                                block(url, filepath)
                            }
                        }).build()
                val taskId = manager.add(request)
                if (rootDir != null) {
                    request.rootDownloadDir(rootDir.absolutePath)
                } else {
                    request.rootDownloadDir(CoreApplicationProvider.getDownLoadDir()!!.absolutePath)
                }
                return taskId
            } catch (e: Exception) {
                e.printStackTrace()
                return -1 //如果正在下也会返回-1
            }
        }

        @JvmStatic
        fun startQueueTask(vararg urls: String): MutableList<Int> {
            return startQueueTask(mutableListOf(*urls))
        }

        /*启动下载队列，第一个最优下*/
        @JvmStatic
        fun startQueueTask(urls: MutableList<String>): MutableList<Int> {
            val taskIds = mutableListOf<Int>()
            try {
                urls.forEach {
                    if (!isAlreadyDownload(it)) {
                        val request = DownloadRequest.Builder().url(it)
                            .progressInterval(10000, TimeUnit.MILLISECONDS)
                            .priority(if (it == urls[0]) Priority.HIGH else Priority.NORMAL)
                            .downloadCallback(object : DownloadCallback {
                                override fun onProgress(
                                    downloadId: Int, bytesWritten: Long, totalBytes: Long
                                ) {
                                    super.onProgress(downloadId, bytesWritten, totalBytes)
//                                        val progress = (bytesWritten * 100f / totalBytes).toInt()
//                                        printD("$it-progress-$progress")
                                }

                                override fun onSuccess(downloadId: Int, filepath: String) {
                                    super.onSuccess(downloadId, filepath)
                                    printD("suc-$filepath")
                                    MMKVUtils.addStr(formatUrl(it), filepath)
                                }
                            }).build()
                        taskIds.add(manager.add(request))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return taskIds
        }

        @JvmStatic //这种判断没有账号隔离,而且多个账号都下载同一个，那么修改时间记录
        fun isAlreadyDownload(url: String?): Boolean {
            val filePath = MMKVUtils.getStr(formatUrl(url + ""))
            if (null != filePath && File(filePath).exists()) {
                return true
            }
            return false
        }

        @JvmStatic
        fun stopAllTask() {
            manager.cancelAll()
        }

        @JvmStatic
        fun stopTaskById(taskId: Int) {
            manager.cancel(taskId)
        }

        @JvmStatic
        fun release() {
            manager.release()
        }

        /*work in sub thread*/
        @JvmStatic
        fun deleteFileByUrl(url: String) {
            val filePath = MMKVUtils.getStr(url)
            FileUtils.delete(filePath!!)
        }

        @JvmStatic
        fun formatUrl(url: String): String {
            return UiHelper.subFormatUrl(url)
        }
    }
}