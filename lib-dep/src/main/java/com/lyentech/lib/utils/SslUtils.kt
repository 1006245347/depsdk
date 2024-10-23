package com.lyentech.lib.utils

import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

object SslUtils {

    fun handleUrl() {
        HttpsURLConnection.setDefaultSSLSocketFactory(createSSLSocketFactory())
        HttpsURLConnection.setDefaultHostnameVerifier(TrustAllHostnameVerifier())
    }

    /**
     * 默认信任所有的证书
     */
    fun createSSLSocketFactory(): SSLSocketFactory? {
        var sSLSocketFactory: SSLSocketFactory? = null
        try {
            val sc = SSLContext.getInstance("TLS")
            sc.init(null, arrayOf(TrustAllManager() as TrustManager), SecureRandom())
            sSLSocketFactory = sc.socketFactory
        } catch (e: Exception) {
        }
        return sSLSocketFactory
    }

    class TrustAllManager : X509TrustManager {
        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate?> {
            return arrayOfNulls(0)
        }
    }

    class TrustAllHostnameVerifier : HostnameVerifier {
        override fun verify(hostname: String, session: SSLSession): Boolean {
            return true
        }
    }

    //第二种方式
    fun ssl() {
        try {
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                })
            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory)
            HttpsURLConnection.setDefaultHostnameVerifier { hostname, session -> true }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}