package com.lyentech.lib.widget.loading

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.lyentech.lib.R
import com.lyentech.lib.utils.NetUtils.isConnected

class LoadProgressAdapter : GLoading.Adapter {

    override fun getView(
        holder: GLoading.Holder,
        convertView: View?,
        status: Int,
        tip: String?
    ): View {
        var progressView: ProgressView? = null
        if (convertView is ProgressView) {
            progressView = convertView
        }
        if (null == progressView) {
            progressView = ProgressView(holder.context, holder.retryTask, holder.cancelTask)
        }
        progressView.setStatus(status, tip)
        return progressView
    }

    internal class ProgressView(context: Context?, retryTask: Runnable?, cancelTask: Runnable?) :
        RelativeLayout(context), View.OnClickListener {
        private val tvLoading: TextView
        private val ivCancel: ImageView
        private val mRetryTask: Runnable?
        private val mCancelTask: Runnable?
        private var modelStatus: Int = GLoading.STATUS_LOAD_SUCCESS


        init {
            LayoutInflater.from(context).inflate(R.layout.layout_loading, this)
            tvLoading = findViewById(R.id.tv_loading)
            ivCancel = findViewById(R.id.iv_cancel)
            mRetryTask = retryTask
            mCancelTask = cancelTask
        }

        fun setStatus(status: Int, tip: String?) {
            var show = true
            var str: Int = R.string.dep_loading
            when (status) {
                GLoading.STATUS_LOAD_SUCCESS -> show = false
                GLoading.STATUS_LOADING -> str = R.string.dep_loading
                GLoading.STATUS_LOAD_FAILED -> {
                    str = R.string.dep_load_err
                    val networkConn = isConnected(context)
                    if (!networkConn) {
                        str = R.string.dep_load_failed_network
                    }
                }
                GLoading.STATUS_EMPTY_DATA -> str = R.string.dep_warning_empty
            }
            modelStatus = status
            findViewById<View>(R.id.rl_loading).setOnClickListener(this)
            findViewById<RelativeLayout>(R.id.rl_root).setOnClickListener(this)
            tvLoading.setOnClickListener(this)
            ivCancel.setOnClickListener(this)
            if (!TextUtils.isEmpty(tip)) {
                tvLoading.text = tip
            } else {
                tvLoading.setText(str)
            }
            visibility = if (show) VISIBLE else GONE
        }

        override fun onClick(v: View) {
            when (v.id) {
                R.id.iv_cancel -> {
                    mCancelTask?.run()
                }

                R.id.tv_loading -> {
                    mRetryTask?.run()
                }

                R.id.rl_loading -> {
                    mRetryTask?.run()
                }

                R.id.rl_root -> {
                    if (modelStatus == GLoading.STATUS_LOAD_FAILED) {
                        mCancelTask?.run()
                    }
                }
            }
        }
    }
}