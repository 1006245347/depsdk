package com.lyentech.lib.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import com.lyentech.lib.global.http.LoadStates
import com.lyentech.lib.utils.ActivityStackUtils
import com.lyentech.lib.utils.printD
import com.lyentech.lib.widget.loading.GLoading
import com.lyentech.lib.widget.loading.LoadProgressAdapter
import kotlinx.coroutines.cancel

/**
 * @author jason-何伟杰，2020-01-11
 * des:FragmentStatePagerAdapter会完全销毁滑动过去的item
 * FragmentPagerAdapter适合少量静态页面
 */
abstract class BaseGFragment : Fragment() {
    lateinit var globalModel: GlobalVm
    protected var mLoading: GLoading.Holder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        globalModel = createViewModel(GlobalVm()) as GlobalVm
        buildFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return buildRootView(setContentView(container))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    //可重构api
    open fun buildFragment() {}
    abstract fun init(savedInstanceState: Bundle?)
    open fun buildRootView(view: View): View {
        mLoading = buildGLoading(view)
        printD("GLoading>$mLoading")
        return mLoading?.wrapper!!
    }

    open fun buildGLoading(view: View): GLoading.Holder? {
        return GLoading.default?.wrap(view)?.withRetry { onLoadRetry() }
            ?.withCancel { onLoadCancel(true) }
    }

    open fun initViews(view: View) {}
    open fun setContentView(container: ViewGroup?): View {
        return layoutInflater.inflate(getLayoutId(), container, false)
    }

    open fun initImmersionBar() {}

    abstract fun getLayoutId(): Int

    fun isFragmentVisible(fragment: Fragment?): Boolean {
        return !fragment?.isHidden!! && fragment.userVisibleHint
    }

    //主要是给vm添加状态
    fun createViewModel(t: BaseVm): BaseVm {
        t.loadState.observe(viewLifecycleOwner) {
            when (it) {
                is LoadStates.LoadSuc -> showLoadSuc()
                is LoadStates.LoadFail -> showLoadErr(it.msg)
                is LoadStates.Loading -> showLoading(it.msg)
                else -> {
                    showLoadSuc()
                }
            }
        }
        return t
    }

    protected fun getMyContext(): FragmentActivity {
        return requireActivity()
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            globalModel.viewModelScope.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun initLoadingStatusViewIfNeed() {
        if (null == mLoading) {
            GLoading.initDefault(LoadProgressAdapter())  //写在app
            val loading = GLoading.default
            mLoading = loading?.wrap(ActivityStackUtils.getCurAty())?.withRetry { onLoadRetry() }
                ?.withCancel { onLoadCancel() }
        }
    }

    /**重新加载*/
    open fun onLoadRetry() {}

    open fun onLoadCancel(isCancel: Boolean = false) {
        if (isCancel)
            globalModel.viewModelScope.cancel()
        getMyContext().uiTask {
            initLoadingStatusViewIfNeed()
            mLoading?.showLoadSuccess()   //去除界面
        }
    }

    /**显示加载中*/
    open fun showLoading(tip: String?) {
        getMyContext().uiTask {
            initLoadingStatusViewIfNeed()
            mLoading?.showLoading(tip)
        }
    }

    /**显示加载成功*/
    open fun showLoadSuc() {
        getMyContext().uiTask {
            initLoadingStatusViewIfNeed()
            mLoading?.showLoadSuccess()
        }
    }

    /**显示加载失败*/
    open fun showLoadErr(errTip: String) {
        getMyContext().uiTask {
            initLoadingStatusViewIfNeed()
            mLoading?.showLoadFailed(errTip)
        }
    }

    /**显示加载的数据为空*/
    open fun showEmpty() {
        getMyContext().uiTask {
            initLoadingStatusViewIfNeed()
            mLoading?.showEmpty()
        }
    }
}