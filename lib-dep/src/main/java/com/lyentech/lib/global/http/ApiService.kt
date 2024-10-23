package com.lyentech.lib.global.http

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Url

/**
 * @author by jason-何伟杰，2022/12/19
 * des:retrofit+协程
 */
interface ApiService {

    @GET
    suspend fun doGetRequest(@Url url: String): ResponseBody

    @POST
    suspend fun doJsonRequest(@Url url: String, @Body requestBody: RequestBody): ResponseBody

    @FormUrlEncoded
    @POST
    suspend fun doHttpRequest(@Url url: String, @FieldMap map: Map<String, String>): ResponseBody

    //下面请求带cookie的请求
    @GET
    suspend fun doGetRequest(@Url url: String, @Header("Cookie") session: String): ResponseBody

    @POST
    suspend fun doJsonRequest(
        @Url url: String,
        @Body requestBody: RequestBody,
        @Header("Cookie") session: String
    ): ResponseBody

    @POST
    suspend fun doHttpRequest(
        @Url url: String,
        @FieldMap map: Map<String, String>,
        @Header("Cookie") session: String
    ): ResponseBody


    @Multipart
    @POST
    suspend fun uploadFile(
        @Url url: String,
        @Part file: MultipartBody.Part,
        @PartMap params: HashMap<String, RequestBody>,
//        @Header("token") token: String
    ): ResponseBody

    //一次上传多个文件
    @Multipart
    @POST
    suspend fun uploadFileList(
        @Url url: String,
        @Part file: List<MultipartBody.Part>,
        @PartMap params: HashMap<String, RequestBody>
    ): ResponseBody
}

interface ProgressListener {
    fun onProgress(progress: Long, total: Long)
}


