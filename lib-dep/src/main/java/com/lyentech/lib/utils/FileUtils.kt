package com.lyentech.lib.utils

/**
 * @author by jason-何伟杰，2023/1/6
 * des: 文件路径、删除工具
 */
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import com.lyentech.lib.R
import com.lyentech.lib.global.common.CoreApplicationProvider
import com.lyentech.lib.global.common.UiHelper
import java.io.*
import java.nio.channels.FileChannel
import java.util.*

object FileUtils {

    val ROOT_DIR = "Android/data/" + UiHelper.getString(R.string.dep_lib_name)
    val DOWNLOAD_DIR = "download"
    val CACHE_DIR = "cache"
    val ICON_DIR = "icon"

    val APP_STORAGE_ROOT = "vehicle"

    /**
     * 判断SD卡是否挂载
     */
    val isSDCardAvailable: Boolean
        get() = Environment.MEDIA_MOUNTED == Environment
            .getExternalStorageState()

    /**
     * 获取下载目录
     */
    val downloadDir: String?
        get() = getDir(DOWNLOAD_DIR)

    /**
     * 获取缓存目录
     */
    val cacheDir: String?
        get() = getDir(CACHE_DIR)

    /**
     * 获取icon目录
     */
    val iconDir: String?
        get() = getDir(ICON_DIR)

    /**
     * 获取SD下的应用目录
     */
    val externalStoragePath: String
        get() {
            val sb = StringBuilder()
            sb.append(Environment.getExternalStorageDirectory().absolutePath)
            sb.append(File.separator)
            sb.append(ROOT_DIR)
            sb.append(File.separator)
            return sb.toString()
        }

    /**
     * 获取SD下当前APP的目录
     */
    val appExternalStoragePath: String
        get() {
            val sb = StringBuilder()
            sb.append(Environment.getExternalStorageDirectory().absolutePath)//妈的，是系统根目录，不是/storage/
            sb.append(File.separator)
            sb.append(APP_STORAGE_ROOT)
            sb.append(File.separator)
            return sb.toString()
        }

    /**
     * 获取应用的cache目录
     */
    val cachePath: String?
        get() {
            val f = UiHelper.getContext().cacheDir
            return if (null == f) {
                null
            } else {
                f.absolutePath + "/"
            }
        }

    /**
     * 获取应用目录，当SD卡存在时，获取SD卡上的目录，当SD卡不存在时，获取应用的cache目录
     */
    fun getDir(name: String): String? {
        val sb = StringBuilder()
        if (isSDCardAvailable) {
            sb.append(appExternalStoragePath)
        } else {
            sb.append(cachePath)
        }
        sb.append(name)
        sb.append(File.separator)
        val path = sb.toString()
        return if (createDirs(path)) {
            path
        } else {
            null
        }
    }

    /**
     * 创建文件夹
     */
    fun createDirs(dirPath: String): Boolean {
        val file = File(dirPath)
        return if (!file.exists() || !file.isDirectory) {
            file.mkdirs()
        } else true
    }

    /**
     * 产生图片的路径，这里是在缓存目录下
     */
    fun generateImgePathInStoragePath(): String {
        return getDir(ICON_DIR) + System.currentTimeMillis().toString() + ".jpg"
    }

    /**
     * 发起剪裁图片的请求
     *
     * @param activity    上下文
     * @param srcFile     原文件的File
     * @param output      输出文件的File
     * @param requestCode 请求码
     */
    fun startPhotoZoom(activity: Activity, srcFile: File, output: File, requestCode: Int) {
        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(getImageContentUri(activity, srcFile), "image/*")
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true")

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 800)
        intent.putExtra("outputY", 480)
        // intent.putExtra("return-data", false);

        //        intent.putExtra(MediaStore.EXTRA_OUTPUT,
        //                Uri.fromFile(new File(FileUtils.picPath)));

        intent.putExtra("return-data", false)// true:不返回uri，false：返回uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output))
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        // intent.putExtra("noFaceDetection", true); // no face detection

        activity.startActivityForResult(intent, requestCode)
    }

    /**
     * 安卓7.0裁剪根据文件路径获取uri
     */
    @SuppressLint("Range")
    fun getImageContentUri(context: Context, imageFile: File): Uri? {
        val filePath = imageFile.absolutePath
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media._ID),
            MediaStore.Images.Media.DATA + "=? ",
            arrayOf(filePath), null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(
                cursor.getColumnIndex(MediaStore.MediaColumns._ID)
            )
            val baseUri = Uri.parse("content://media/external/images/media")
            return Uri.withAppendedPath(baseUri, "" + id)
        } else {
            if (imageFile.exists()) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA, filePath)
                return context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                )
            } else {
                return null
            }
        }
    }

    /**
     * 复制bm
     *
     * @param bm
     * @return
     */
    fun saveBitmap(bm: Bitmap): String {
        var croppath = ""
        try {
            val f = File(generateImgePathInStoragePath())
            //得到相机图片存到本地的图片
            croppath = f.path
            if (f.exists()) {
                f.delete()
            }
            val out = FileOutputStream(f)
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return croppath
    }

    /**
     * 按质量压缩bm
     *
     * @param bm
     * @param quality 压缩率
     * @return
     */
    fun saveBitmapByQuality(bm: Bitmap, quality: Int): String {
        var croppath = ""
        try {
            val f = File(generateImgePathInStoragePath())
            //得到相机图片存到本地的图片
            croppath = f.path
            if (f.exists()) {
                f.delete()
            }
            val out = FileOutputStream(f)
            bm.compress(Bitmap.CompressFormat.JPEG, quality, out)
            out.flush()
            out.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return croppath
    }

    @Throws(IOException::class)
    fun copy(src: File, dst: File) {
        val inStream = FileInputStream(src)
        val outStream = FileOutputStream(dst)
        val inChannel = inStream.channel
        val outChannel = outStream.channel
        inChannel.transferTo(0L, inChannel.size(), outChannel)
        inStream.close()
        outStream.close()
    }

    @JvmStatic
    fun copyAssetsToDst(context: Context, srcDir: String, dstDir: String): Boolean {
        try {
            val fileNames = context.assets.list(srcDir)
            if (fileNames!!.isNotEmpty()) {
                val file = File(CoreApplicationProvider.getAppCacheDir(), dstDir)
                if (!file.exists()) file.mkdirs()
                for (fileName in fileNames) {
                    if (srcDir != "") { // assets 文件夹下的目录
                        copyAssetsToDst(
                            context, srcDir + File.separator + fileName,
                            dstDir + File.separator + fileName
                        )
                    } else {// assets 文件夹
                        copyAssetsToDst(context, fileName, dstDir + File.separator + fileName)
                    }
                }
            } else {
                val outFile = File(CoreApplicationProvider.getAppCacheDir(), dstDir)
                printD("copy_dst>${outFile.absolutePath}")
                val io = context.assets.open(srcDir)
                val fos = FileOutputStream(outFile)
                val buffer = ByteArray(1024)
                var byteCount: Int
                while (io.read(buffer).also { byteCount = it } != -1) {
                    fos.write(buffer, 0, byteCount)
                }
                fos.flush()
                io.close()
                fos.close()
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * 根据图片文件类型获取图片文件的后缀名
     *
     * @param filePath
     * @return
     */
    fun getImageFileExt(filePath: String): String {
        val mFileTypes = HashMap<String, String>()
        mFileTypes["FFD8FF"] = ".jpg"
        mFileTypes["89504E47"] = ".png"
        mFileTypes["474946"] = ".gif"
        mFileTypes["49492A00"] = ".tif"
        mFileTypes["424D"] = ".bmp"

        val value = mFileTypes[getFileHeader(filePath)]
        val ext = if (TextUtils.isEmpty(value)) ".jpg" else value
        return "" + ext
    }

    /**
     * 获取文件头信息
     *
     * @param filePath
     * @return
     */
    @JvmStatic
    fun getFileHeader(filePath: String): String? {
        var `is`: FileInputStream? = null
        var value: String? = null
        try {
            `is` = FileInputStream(filePath)
            val b = ByteArray(3)
            `is`.read(b, 0, b.size)
            value = bytesToHexString(b)
        } catch (e: Exception) {
        } finally {
            if (null != `is`) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                }

            }
        }
        return value
    }

    /**
     * 将byte字节转换为十六进制字符串
     *
     * @param src
     * @return
     */
    @JvmStatic
    private fun bytesToHexString(src: ByteArray?): String? {
        val builder = StringBuilder()
        if (src == null || src.isEmpty()) {
            return null
        }
        var hv: String
        for (i in src.indices) {
            hv = Integer.toHexString(src[i].toInt() and 0xFF).uppercase()
            if (hv.length < 2) {
                builder.append(0)
            }
            builder.append(hv)
        }
        val header = builder.toString()
        return header
    }

    //复制文件
    @JvmStatic
    fun copyFileOrDirectory(srcDir: String?, dstDir: String?) {
        try {
            val src = File(srcDir)
            val dst = File(dstDir, src.name)
            if (src.isDirectory) {
                val files = src.list()
                val filesLength = files.size
                for (i in 0 until filesLength) {
                    val src1 = File(src, files[i]).path
                    val dst1 = dst.path
                    copyFileOrDirectory(src1, dst1)
                }
            } else {
                copyFile(src, dst)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    @Throws(IOException::class)
    fun copyFile(sourceFile: File?, destFile: File) {
        if (!destFile.parentFile.exists()) destFile.parentFile.mkdirs()
        if (!destFile.exists()) {
            destFile.createNewFile()
        }
        var source: FileChannel? = null
        var destination: FileChannel? = null
        try {
            source = FileInputStream(sourceFile).channel
            destination = FileOutputStream(destFile).channel
            destination.transferFrom(source, 0, source.size())
        } finally {
            source?.close()
            destination?.close()
        }
    }

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param filePath 要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    @JvmStatic
    fun delete(filePath: String): Boolean {
        val file = File(filePath)
        return if (!file.exists()) {
            printD("删除文件失败:" + filePath + "不存在！")
            false
        } else {
            if (file.isFile) deleteFile(filePath) else deleteDirectory(filePath)
        }
    }

    /**
     * 删除单个文件
     *
     * @param filePath 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    @JvmStatic
    fun deleteFile(filePath: String): Boolean {
        val file = File(filePath)
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        return if (file.exists() && file.isFile) {
            if (file.delete()) {
                printD("删除单个文件" + filePath + "成功！")
                true
            } else {
                printD("删除单个文件" + filePath + "失败！")
                false
            }
        } else {
            printD("删除单个文件失败：" + filePath + "不存在！")
            false
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    @JvmStatic
    fun deleteDirectory(dir: String): Boolean {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        var dir = dir
        if (!dir.endsWith(File.separator)) dir += File.separator
        val dirFile = File(dir)
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory) {
            println("删除目录失败：" + dir + "不存在！")
            return false
        }
        var flag = true
        // 删除文件夹中的所有文件包括子目录
        val files = dirFile.listFiles()
        for (i in files.indices) {
            // 删除子文件
            if (files[i].isFile) {
                flag = deleteFile(files[i].absolutePath)
                if (!flag) break
            } else if (files[i].isDirectory) {
                flag = deleteDirectory(
                    files[i]
                        .absolutePath
                )
                if (!flag) break
            }
        }
        if (!flag) {
            println("删除目录失败！")
            return false
        }
        // 删除当前目录
        return if (dirFile.delete()) {
            println("删除目录" + dir + "成功！")
            true
        } else {
            false
        }
    }

    /*----------------------外部：分区存储目录路径------------------------*/
    /**
     *  分区存储-Cache目录
     */
    fun getAppExternalCachePath(subDir: String? = null): String {
        val path = UiHelper.getContext().externalCacheDir?.absolutePath?.let { StringBuilder(it) }
        subDir?.let {
            path?.append(File.separator)?.append(it)?.append(File.separator)
        }
        val dir = File(path.toString())
        if (!dir.exists()) dir.mkdir()
        return path.toString()
    }

    /**
     *  分区存储-File目录
     */
    fun getAppExternalFilePath(subDir: String? = null): String {
        val path = UiHelper.getContext().getExternalFilesDir(subDir)?.absolutePath
        val dir = File(path.toString())
        if (!dir.exists()) dir.mkdir()
        return path.toString()
    }
    /*--------------------------------------------------*/


    /*-----------------------内部：私有目录--------------------------*/
    /**
     *  私有目录-files
     */
    fun getAppFilePath(subDir: String? = null): String {
        val path = StringBuilder(UiHelper.getContext().filesDir.absolutePath)
        subDir?.let {
            path.append(File.separator).append(it).append(File.separator)
        }
        val dir = File(path.toString())
        if (!dir.exists()) dir.mkdir()
        return path.toString()
    }

    /**
     * 私有目录-cache
     */
    fun getAppCachePath(subDir: String? = null): String {
        val path = StringBuilder(UiHelper.getContext().cacheDir.absolutePath)
        subDir?.let {
            path.append(File.separator).append(it).append(File.separator)
        }
        val dir = File(path.toString())
        if (!dir.exists()) dir.mkdir()
        return path.toString()
    }

    /*------------------------cache子目录------------------------*/
    fun getAudioPathEndWithSeparator(): String {
        return getAppCachePath("audio")
    }

    fun getTxtPathEndWithSeparator(): String {
        return getAppCachePath("txt")
    }

    fun getMp3PathEndWithSeparator(): String {
        return getAppCachePath("mp3")
    }

    fun getTempPathEndWithSeparator(): String {
        return getAppCachePath("temp")
    }

    /*--------------------------------------------------*/


    /*-----------------外部：公共目录（需要权限）----------------*/
    /**
     *  Pictures
     */
    fun getExternalPicturesPath(subDir: String? = null): String {
        val path = StringBuilder(Environment.getExternalStorageDirectory().absolutePath)
            .append(File.separator)
            .append(Environment.DIRECTORY_PICTURES)
        subDir?.let {
            path.append(File.separator).append(it).append(File.separator)
        }
        val dir = File(path.toString())
        if (!dir.exists()) dir.mkdir()
        return path.toString()
    }

    /**
     *  Download
     */
    fun getExternalDownloadPath(subDir: String? = null): String {
        val path = StringBuilder(Environment.getExternalStorageDirectory().absolutePath)
            .append(File.separator)
            .append(Environment.DIRECTORY_DOWNLOADS)
        subDir?.let {
            path.append(File.separator).append(it).append(File.separator)
        }
        val dir = File(path.toString())
        if (!dir.exists()) dir.mkdir()
        return path.toString()
    }

    /**
     *  DCIM
     */
    fun getExternalCameraPath(subDir: String? = null): String {
        val path = StringBuilder(Environment.getExternalStorageDirectory().absolutePath)
            .append(File.separator)
            .append(Environment.DIRECTORY_DCIM)
        subDir?.let {
            path.append(File.separator).append(it).append(File.separator)
        }
        val dir = File(path.toString())
        if (!dir.exists()) dir.mkdir()
        return path.toString()
    }

    /**
     *  Music
     */
    fun getExternalMusicPath(subDir: String? = null): String {
        val path = StringBuilder(Environment.getExternalStorageDirectory().absolutePath)
            .append(File.separator)
            .append(Environment.DIRECTORY_MUSIC)
        subDir?.let {
            path.append(File.separator).append(it).append(File.separator)
        }
        val dir = File(path.toString())
        if (!dir.exists()) dir.mkdir()
        return path.toString()
    }
    /*---------------------------------------------------------*/


}
