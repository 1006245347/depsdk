package com.lyentech.lib.global.http

import android.text.TextUtils
import com.google.gson.Gson
import com.lyentech.lib.global.common.GlobalCode
import com.lyentech.lib.utils.printD
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @author by jason-何伟杰，2022/12/19
 * des:
 */
class RetrofitFactory private constructor() {
    private val retrofit: Retrofit
    val client: OkHttpClient

    init {
        val gson = Gson().newBuilder()
            .setLenient()
            .serializeNulls()
            .create()
        client = initOkHttpClient()
        retrofit = Retrofit.Builder()
            .baseUrl(GlobalCode.HOST)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    companion object {
        private val instance: RetrofitFactory by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            RetrofitFactory()
        }

        private fun parseResponse(result: String, isParse: Boolean = true): String {
            return stateResponse(result, isParse)
        }

        /* isParse:不解释结果，直接回传结果 */
        private fun stateResponse(result: String, isParse: Boolean = true): String {
            printD("http_result:$result")
            if (!isParse) return result
            var jo: JSONObject? = null
            try {
                jo = JSONObject(result) //如果接口崩了，会乱码造成崩溃
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }
            return when {
                "0" == jo.optString("code") -> result
                "0" == jo.optString("State") -> result
                "1000" == jo.optString("code") -> result
                "1000" == jo.optString("State") -> result
                else -> { //如果code不等于0，那么将整个返回体置null
//                    return ""  //注意这里-所有异常都会返回一个空字符，一直走到这都没切换线程
                    return result
                }
            }
        }

        fun <T> getResponse(result: String, cls: Class<T>): T? {
            if (TextUtils.isEmpty(result)) {
                return null
            } else {
                val jo = JSONObject(result)
                var t: T?
                val gson = Gson()
                t = gson.fromJson(jo.getJSONObject("data").toString(), cls)
                return t
            }
        }

        private fun getLocalToken(): String {
            return ""
        }

        suspend fun doGetRequest(
            url: String,
            otherArg: String = "",
            cookie: String? = GlobalCode.TOKEN,
            isParse: Boolean = true
        ): String {
            printD("doGet>$url$otherArg")
            var result: String = try {
                if (cookie.isNullOrEmpty()) {
                    instance.createService(ApiService::class.java)
                        .doGetRequest(url + getLocalToken() + otherArg).string()
                } else {
                    instance.createService(ApiService::class.java)
                        .doGetRequest(url + getLocalToken() + otherArg, "" + cookie).string()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                "{ \"message\":\"服务异常\",\"code\":777 }"
            }
            return parseResponse(result, isParse)
        }

        suspend fun doPostRequest(
            url: String,
            map: MutableMap<String, String>,
            otherArg: String = "",      //"&page_size=${CommunityConfig.LOAD_SIZE_IN_PAGE}&page=$curPage"
            cookie: String? = GlobalCode.TOKEN,
            isParse: Boolean = true
        ): String {
            printD("post>$url$otherArg")
            var result: String = try {
                if (cookie.isNullOrEmpty()) {
                    instance.createService(ApiService::class.java)
                        .doHttpRequest(url + getLocalToken() + otherArg, map).string()
                } else {
                    instance.createService(ApiService::class.java)
                        .doHttpRequest(url + getLocalToken() + otherArg, map, cookie).string()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                "{ \"message\":\"服务异常\",\"code\":777 }"
            }
            return parseResponse(result, isParse)
        }

        suspend fun doPostJsonRequest(
            url: String,
            jsonParam: String,
            otherArg: String = "",
            cookie: String? = GlobalCode.TOKEN,
            isParse: Boolean = true
        ): String {
            printD("postJson>$url$otherArg")
            printD("js=$jsonParam")
            var result: String = try {
                val body = jsonParam.toRequestBody("application/json;charset=utf-8".toMediaType())
                if (cookie.isNullOrEmpty()) {
                    instance.createService(ApiService::class.java)
                        .doJsonRequest(url + getLocalToken() + otherArg, body).string()
                } else {
                    instance.createService(ApiService::class.java)
                        .doJsonRequest(url + getLocalToken() + otherArg, body, cookie).string()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                "{ \"message\":\"服务异常\",\"code\":777 }"
            }
            return parseResponse(result, isParse)
        }


        suspend fun <T> doGetResponse(url: String, otherArg: String = "", cls: Class<T>): T? {
            return getResponse(doGetRequest(url, otherArg), cls)
        }

        suspend fun <T> doPostResponse(
            url: String,
            map: MutableMap<String, String>,
            cls: Class<T>
        ): T? {
            return getResponse(doPostRequest(url, map), cls)
        }

        suspend fun <T> doPostJsonResponse(url: String, jsonParam: String, cls: Class<T>): T? {
            return getResponse(doPostJsonRequest(url, jsonParam), cls)
        }

        suspend fun upload2File(
            url: String,
            paramsMap: HashMap<String, RequestBody>,
            fileKey: String,
            filePath: String,
            progressListener: ProgressListener
        ): ResponseBody? {  //新版写法
            val requestBody = File(filePath).asRequestBody("application/octet-stream".toMediaType())
            val fileRequest = UploadBody(requestBody, progressListener)
            val part = MultipartBody.Part.createFormData(fileKey, File(filePath).name, fileRequest)
            try {
                return instance.createService(ApiService::class.java)
                    .uploadFile(url, part, paramsMap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        suspend fun upload2FileList(
            url: String, paramsMap: HashMap<String, RequestBody>,
            fileKey: String, filePaths: List<String>, progressListener: ProgressListener
        ): ResponseBody? {
            val parts = filePaths.mapIndexed { index, path ->
                val file = File(path)
                val requestBody = file.asRequestBody("application/octet-stream".toMediaType())
                val fileRequest = UploadBody(requestBody, progressListener)
                MultipartBody.Part.createFormData("$fileKey${index + 1}", file.name, fileRequest)
            }
            try {
                return instance.createService(ApiService::class.java)
                    .uploadFileList(url, parts, paramsMap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

    }

    private fun initOkHttpClient(): OkHttpClient {
        val log = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { msg ->
            printD(msg, "okHttp")
        }).setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(log)
            .connectTimeout(10, TimeUnit.SECONDS) //30
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    fun <T> createService(api: Class<T>): T {
        return retrofit.create(api)
    }
}