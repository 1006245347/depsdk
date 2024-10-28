package com.lyentech.lib.utils

import android.os.Environment
import androidx.fragment.app.FragmentActivity

import java.io.File

/**
 * @author by jason-何伟杰，2024/10/22
 * des:一些有用的知识或资料
 */
@Deprecated("只读")
object MockUtils {

    const val testMp4Url1 =
        "https://gelimall.oss-cn-shenzhen.aliyuncs.com/video/2020/7/28/3b36465c-63de-4419-a3b8-68b192725347.mp4"

    const val testMp4Url2 =
        "https://gelimall.oss-cn-shenzhen.aliyuncs.com/video/2020/7/28/3b36465c-63de-4419-a3b8-68b192725347.mp4?sign=e3faa595d9dcccb7720d8d3277ee71ab&t=1729746000"
    const val testJpgUrl1 =
        "https://malloss.gree.com/gree-mall-v2/cc06e180c9004c85acd309128d18ff81.jpg"

    const val testGet =
        "https://venus.leayun.cn/venus/external/commodity/screen/spuUniqueId/detail?spuUniqueId=102253838563540992&clubId=124706646550118400"

    const val testPostJson =
        "http://offlineretail.leayun.cn/cucomeApi/v1/msg/call" //{"deviceSN":"F4911E69986F","position":"横屏"}

    fun saveCsvFile() {
        //https://blog.csdn.net/qq_41657996/article/details/126835887
        //http://www.manongjc.com/detail/26-upnvwnulikcgewt.html
        //https://blog.csdn.net/weixin_37923592/article/details/106674903  CsvUtil
    }

    fun customSaveConfig(activity: FragmentActivity) {
        activity.apply {
            //内部存储 只能被我们app访问,不需要存储权限,卸载app后，系统会自动清理app存储的文件
            var file: File? = null

            file = cacheDir         //存储临时数据，系统内存不够自动清 /data/data/应用包名/cache
            printLog(file)
            file =
                externalCacheDir    //数据超过1mb /storage/emulated/0/Android/data/com.cooler.downloadsample/cache
            printLog(file)          //google推荐使用这个，但是文件会随着卸载被删除
            file =
                filesDir            //data/data/应用包名/files    魅族 /data/user/0/com.cooler.downloadsample/files
            file = File("${filesDir.absolutePath}" + "/config") //在files下添加自定义文件夹
            printLog(file)

            //外部存储分 公共目录 和 私有目录
            //公共目录
            file = Environment.getExternalStorageDirectory() // /storage/emulated/0
            printLog(file)
            file =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) // /storage/emulated/0/DCIM
            printLog(file)

            //1. DIRECTORY_MUSIC
            //音乐/storage/emulated/0/Music
            //2. DIRECTORY_PODCASTS
            //播客/storage/emulated/0/Podcasts
            //3. DIRECTORY_RINGTONES
            //来电铃声
            //4. DIRECTORY_ALARMS
            //闹钟/storage/emulated/0/Alarms
            //5. DIRECTORY_NOTIFICATIONS
            //通知/storage/emulated/0/Notifications
            //6. DIRECTORY_PICTURES
            //图片/storage/emulated/0/Pictures
            //7. DIRECTORY_MOVIES
            //电影/storage/emulated/0/Movies
            //8. DIRECTORY_DOWNLOADS
            //下载文件存储路径/storage/emulated/0/Download
            //9. DIRECTORY_DCIM
            //媒体文件/storage/emulated/0/DCIM
            //10. DIRECTORY_DOCUMENTS
            //文档/storage/emulated/0/Documents


            //私有目录,无需权限
            // getExternalFilesDir是手机中设置 → 应用 → 具体应用详情→ 清除数据 的操作对象
            //这里竟然能传 Environment ,但是私有目录
            file =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES) //storage/emulated/0/Android/data/packagename/files/Pictures
            printLog(file)
            file = getExternalFilesDir("jason_files") //自定义目录
            printLog(file)
            //对应着应用程序内的外部缓存，同样是用来存储临时数据的。但是其由于脱离了应用管理，因此并不会在空间少时被自动清除,设置 → 应用 → 具体应用详情→ 清除缓存的操作对象
            file = externalCacheDir  ///storage/sdcard/Android/data/应用包名/cache
            printLog(file)

            //另外
            file = Environment.getDownloadCacheDirectory()  // /data/cache
            printLog(file)
            file = Environment.getDataDirectory()           // /data
            printLog(file)
            file = Environment.getRootDirectory()           // /system
            printLog(file)
        }
    }

    /**
     * Block 语法格式
     * 块名:(参数:参数类型) -> 返回值类型
     */
    fun main() {

        // 无入参，无返回值）简单回调
        test1 {
            println("hello block")
        }

        // (无入参，有返回值）调用返回一个字符串
        test2 {
            "block return test2"
        }

        //有入参，有返回值）传2个数字，返回他们的结果
        test3 { x, y -> x + y }

        // （有入参，无返回值）传2个数字，回调处打印参数
        val result = test4 { x, y -> x * y }
        println("result test4 ==> $result")
    }

    /**
     * block（（无入参，无返回值）简单回调）
     */
    fun test1(block: () -> Unit) {
        block()
    }

    /**
     * block(无入参，有返回值）调用返回一个字符串)
     */
    fun test2(block: () -> String) {
        val value = block()
        println("block return value====>$value")
    }

    /**
     * block ===> 有入参，有返回值）传2个数字，返回他们的结果
     */
    fun test3(block: (x: Int, y: Int) -> Int) {
        val result = block(1, 3)
        println("block cal value =====> $result")
    }

    /**
     * block ===> （有入参，无返回值）传2个数字，回调处打印参数
     */
    fun test4(block: (x: Int, y: Int) -> Int): Int {
        return block(1, 2)
    }

    /**
     * Author: L
     * 2022/10/1
     * Description:
     * 1. 泛型扩展数函特征定义：
     *    01. 函数中不能使用return关键字,   示例：block: () -> T
     *    02. 函数中传入it,   示例： block: (T) -> T
     *    03. 函数中传入this,     示例： block: T.() -> T
     *    04. kotlin的函数参数定义了一个函数，规定了该函数的参数，返回， 给函数域中的传值等语法特征
     */


    /**
     *  1. block: () -> T
     *    01.传入的函数无参
     *    02.该函数最后一行需要是调用者对象类型，而且无return
     */
    fun <T> T.method1(block: () -> T): T {
        return block()
    }

    /**
     *  2. block: (T) -> T
     *    01.传入的函数带有自身作为参数
     *    02.该函数最后一行需要是调用者对象类型，而且无return
     *    03.把调用者作为it,传入定义的lambda表达式函数域中
     */
    fun <T> T.method2(block: (T) -> T): T {
        return block(this)
    }

    /**
     * 3. block: T.() -> T
     *   01.传入的函数无参
     *   02.该函数最后一行需要是调用者对象类型，而且无return
     *   03.把调用者作为this,传入定义的lambda表达式函数域中
     */
    fun <T> T.method3(block: T.() -> T): T {
//        Log.d("=======1==this:$this")
        return block()
    }

    fun main1() {

        "==1==".method1 {
//            Log.d("======2===this:")
            "";
            // 03. 不能带有return
        }

        "==2==".method2 {
//            Log.d("======2===this:$it")
            it
            // 03. 不能带有return
        }

        "==3==".method3 {
//            Log.d("======2===this:$this")
            this
            // 0.3 不能带有return
        }
    }
}