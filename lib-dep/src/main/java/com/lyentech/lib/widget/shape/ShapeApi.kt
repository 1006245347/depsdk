package com.lyentech.lib.widget.shape

import android.graphics.Paint
import android.view.View
import android.view.ViewGroup
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.OffsetEdgeTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.shape.TriangleEdgeTreatment
import com.lyentech.lib.R
import com.lyentech.lib.global.common.UiHelper

/**
 * @author by jason-何伟杰，2024/10/23
 * des: MaterialShapeDrawable 设置背景
 */
object ShapeApi {

    //设置4等圆角、背景色,corner值够大就是圆
    fun round(corner: Float = 16f, color: Int = R.color.cBasic)
            : MaterialShapeDrawable {
        val model = ShapeAppearanceModel.builder()
            .setAllCorners(CornerFamily.ROUNDED, corner)
            .build()
        val drawable = MaterialShapeDrawable(model).apply {
            setTint(UiHelper.getColor(color))
        }
        return drawable
    }

    fun roundX(
        left: Float,
        right: Float,
        bLeft: Float,
        bRight: Float,
        tintColor: Int = R.color.cBasic
    ): MaterialShapeDrawable {
        val model = ShapeAppearanceModel.builder()
            .setTopLeftCorner(CornerFamily.ROUNDED, left)
            .setTopRightCorner(CornerFamily.ROUNDED, right)
            .setBottomLeftCorner(CornerFamily.ROUNDED, bLeft)
            .setBottomRightCorner(CornerFamily.ROUNDED, bRight)
            .build()
        val drawable = MaterialShapeDrawable(model).apply {
            setTint(UiHelper.getColor(tintColor))
        }

        return drawable
    }

    //圆角、边框
    fun roundStroke(
        corner: Float = 16f,
        stroke: Float = 4f,
        strokeColor: Int = R.color.cBasic,
        tintColor: Int = R.color.cBasic
    ): MaterialShapeDrawable {
        val model = ShapeAppearanceModel.builder()
            .setAllCorners(CornerFamily.ROUNDED, corner)
            .build()
        val drawable = MaterialShapeDrawable(model).apply {
            setTint(UiHelper.getColor(tintColor))
            setStroke(stroke, UiHelper.getColor(strokeColor))
        }
        return drawable
    }

    //圆角、背景色、阴影色
    fun setRoundEle(
        corner: Float = 30f,
        tintColor: Int = R.color.cBasic,
        eleColor: Int = R.color.cGrey333333,
        elevation: Float = 20f, view: View
    ) {
        val model = ShapeAppearanceModel.builder()
            .setAllCorners(CornerFamily.ROUNDED, corner)
            .build()
        val drawable = MaterialShapeDrawable(model).apply {
            setTint(UiHelper.getColor(tintColor))
            paintStyle = Paint.Style.FILL
            //绘制阴影
            shadowCompatibilityMode = MaterialShapeDrawable.SHADOW_COMPAT_MODE_ALWAYS
            initializeElevationOverlay(view.context)
            setShadowColor(UiHelper.getColor(eleColor)) //阴影颜色
            setElevation(elevation)
        }
        view.background = drawable
        view.clipToOutline = false //禁止裁剪阴影
        (view.parent as ViewGroup).clipChildren = false
    }

    fun divider(tintColor: Int = R.color.cGrey333333): MaterialShapeDrawable {
        val model = ShapeAppearanceModel.builder().build()
        val drawable = MaterialShapeDrawable(model).apply {
            setTint(UiHelper.getColor(tintColor))
        }
        return drawable
    }

    //类似气泡
    fun bottomEdge(corner: Float, offset: Float, tintColor: Int = R.color.cBasic, view: View) {
        val model = ShapeAppearanceModel.builder()
            .setAllCorners(CornerFamily.ROUNDED, corner)
            .setBottomEdge(OffsetEdgeTreatment(TriangleEdgeTreatment(12f, false), offset))
            .build()
        val drawable = MaterialShapeDrawable(model).apply {
            setTint(UiHelper.getColor(tintColor))
            paintStyle = Paint.Style.FILL
        }
        (view.parent as ViewGroup).clipChildren = false
        view.background = drawable
    }

    //动态切换背景,drawable是view旧设置的background
    fun updateShape(drawable: MaterialShapeDrawable, model: ShapeAppearanceModel) {
        drawable.shapeAppearanceModel = model
        drawable.invalidateSelf()
    }

}

//扩展函数
fun View.round(corner: Float = 16f, color: Int = R.color.cBasic) {
    this.background = ShapeApi.round(corner, color)
}

fun View.roundX(
    left: Float,
    right: Float,
    bLeft: Float,
    bRight: Float,
    tintColor: Int = R.color.cBasic
) {
    this.background = ShapeApi.roundX(left, right, bLeft, bRight, tintColor)
}

fun View.roundStroke(
    corner: Float = 16f,
    stroke: Float = 4f,
    strokeColor: Int =R.color.cBasic,
    tintColor: Int = R.color.cBasic
) {
    this.background = ShapeApi.roundStroke(corner, stroke, strokeColor, tintColor)
}

fun View.divider(tintColor: Int = R.color.cGrey333333) {
    this.background = ShapeApi.divider(tintColor)
}

fun View.setRoundEle(
    corner: Float = 16f,
    tintColor: Int = R.color.cBasic,
    eleColor: Int = R.color.cYellowFFA900,
    elevation: Float = 8f
) {
    val model = ShapeAppearanceModel.builder()
        .setAllCorners(CornerFamily.ROUNDED, corner)
        .build()
    val drawable = MaterialShapeDrawable(model).apply {
        setTint(UiHelper.getColor(tintColor))
        paintStyle = Paint.Style.FILL
        //绘制阴影
        shadowCompatibilityMode = MaterialShapeDrawable.SHADOW_COMPAT_MODE_ALWAYS
        initializeElevationOverlay(context)
        setShadowColor(UiHelper.getColor(eleColor)) //阴影颜色
        setElevation(elevation)
    }
    this.background = drawable
    this.clipToOutline = false //禁止裁剪阴影
    (this.parent as ViewGroup).clipChildren = false
}
