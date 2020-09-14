/*
 * Copyright (C) 2020 sea
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.srsea.peinture.draw

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.support.constraint.ConstraintLayout
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import top.srsea.peinture.draw.view.ClipView
import top.srsea.peinture.vlparser.type.*

private fun String.toId(): Int = when (this) {
    "" -> ConstraintLayout.LayoutParams.UNSET
    "parent" -> ConstraintLayout.LayoutParams.PARENT_ID
    else -> toInt()
}

private fun String.toColor(): Int = Color.parseColor(this)

private fun String.toSize(ctx: Context): Int = when {
    this == "parent" -> ViewGroup.LayoutParams.MATCH_PARENT
    this == "wrap" -> ViewGroup.LayoutParams.WRAP_CONTENT
    endsWith("dp") -> {
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, substring(0, length - 2).toFloat(),
            ctx.resources.displayMetrics
        ).toInt()
    }
    endsWith("sp") -> {
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, substring(0, length - 2).toFloat(),
            ctx.resources.displayMetrics
        ).toInt()
    }
    endsWith("pt") -> {
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_PT, substring(0, length - 2).toFloat(),
            ctx.resources.displayMetrics
        ).toInt()
    }
    endsWith("px") -> {
        substring(0, length - 2).toInt()
    }
    else -> toInt()
}

private fun String.toTypeFace(): Typeface = when (this) {
    "bold" -> Typeface.DEFAULT_BOLD
    "monospace" -> Typeface.MONOSPACE
    "sans_serif" -> Typeface.SANS_SERIF
    "serif" -> Typeface.SERIF
    "italic" -> Typeface.defaultFromStyle(Typeface.ITALIC)
    "bold_italic" -> Typeface.defaultFromStyle(Typeface.BOLD_ITALIC)
    "normal" -> Typeface.defaultFromStyle(Typeface.NORMAL)
    else -> Typeface.DEFAULT
}

private fun String.toScaleType(): ImageView.ScaleType = when (this) {
    "matrix" -> ImageView.ScaleType.MATRIX
    "fit_xy" -> ImageView.ScaleType.FIT_XY
    "fit_start" -> ImageView.ScaleType.FIT_START
    "fit_center" -> ImageView.ScaleType.FIT_CENTER
    "fit_end" -> ImageView.ScaleType.FIT_END
    "center" -> ImageView.ScaleType.CENTER
    "center_crop" -> ImageView.ScaleType.CENTER_CROP
    "center_inside" -> ImageView.ScaleType.CENTER_INSIDE
    else -> ImageView.ScaleType.MATRIX
}

private fun String.toShape(): Int = when (this) {
    "oval" -> GradientDrawable.OVAL
    "rectangle" -> GradientDrawable.RECTANGLE
    else -> GradientDrawable.RECTANGLE
}

private fun String.toGradientType() = when (this) {
    "linear" -> GradientDrawable.LINEAR_GRADIENT
    "radial" -> GradientDrawable.RADIAL_GRADIENT
    "sweep" -> GradientDrawable.SWEEP_GRADIENT
    else -> GradientDrawable.LINEAR_GRADIENT
}

private fun String.toGradientOrientation() = when (this) {
    "bl_tr" -> GradientDrawable.Orientation.BL_TR
    "bt" -> GradientDrawable.Orientation.BOTTOM_TOP
    "br_tl" -> GradientDrawable.Orientation.BR_TL
    "lr" -> GradientDrawable.Orientation.LEFT_RIGHT
    "rl" -> GradientDrawable.Orientation.RIGHT_LEFT
    "tl_br" -> GradientDrawable.Orientation.TL_BR
    "tb" -> GradientDrawable.Orientation.TOP_BOTTOM
    "tr_bl" -> GradientDrawable.Orientation.TR_BL
    else -> GradientDrawable.Orientation.LEFT_RIGHT
}

private fun View.setup(widget: Widget) {
    id = widget.id.toId()
    alpha = widget.alpha
    val constraint = widget.constraint
    val param = ConstraintLayout.LayoutParams(constraint.width.toSize(context), constraint.height.toSize(context))
    layoutParams = param.apply {
        topMargin = constraint.top.toSize(context)
        leftMargin = constraint.left.toSize(context)
        rightMargin = constraint.right.toSize(context)
        bottomMargin = constraint.bottom.toSize(context)

        baselineToBaseline = constraint.baselineToBaseLine.toId()
        topToTop = constraint.topToTop.toId()
        topToBottom = constraint.topToBottom.toId()
        leftToLeft = constraint.leftToLeft.toId()
        leftToRight = constraint.leftToRight.toId()
        rightToRight = constraint.rightToRight.toId()
        rightToLeft = constraint.rightToLeft.toId()
        bottomToBottom = constraint.bottomToBottom.toId()
        bottomToTop = constraint.bottomToTop.toId()

        if (constraint.widthToHeight.isNotEmpty()) {
            dimensionRatio = constraint.widthToHeight
        }
        if (constraint.heightToWidth.isNotEmpty()) {
            dimensionRatio = constraint.heightToWidth
        }
    }

    val padding = widget.padding
    setPadding(
        padding.left.toSize(context),
        padding.top.toSize(context),
        padding.right.toSize(context),
        padding.bottom.toSize(context)
    )

    widget.transform?.apply {
        pivot?.apply {
            pivotX = first.toSize(context).toFloat()
            pivotY = second.toSize(context).toFloat()
        }
        scaleX = scale.first
        scaleY = scale.second
        rotationX = rotation.first
        rotationY = rotation.second
        this@setup.rotation = rotation.third
        scrollX = scroll.first.toSize(context)
        scrollY = scroll.second.toSize(context)
        translationX = translation.first.toSize(context).toFloat()
        translationY = translation.second.toSize(context).toFloat()
        translationZ = translation.third.toSize(context).toFloat()
    }

    val bg = GradientDrawable()
    widget.shape?.apply {
        bg.shape = toShape()
    }
    bg.setColor(widget.color.toColor())
    bg.setStroke(
        widget.borderWidth.toSize(context),
        widget.borderColor.toColor(),
        widget.borderLength.toSize(context).toFloat(),
        widget.borderSpace.toSize(context).toFloat()
    )
    bg.cornerRadii = widget.cornerRadii.flatMap { point ->
        point.toList()
    }.map { size ->
        size.toSize(context).toFloat()
    }.toFloatArray()

    widget.gradient?.apply {
        type?.apply {
            bg.gradientType = toGradientType()
        }
        bg.colors = colors.map(String::toColor).toIntArray()
        orientation?.apply {
            bg.orientation = toGradientOrientation()
        }
        bg.gradientRadius = radius.toSize(context).toFloat()
        bg.setGradientCenter(center.first, center.second)
    }
    background = bg
}

fun Widget.toView(drawer: Drawer): View = when (this) {
    is Composite -> ConstraintLayout(drawer.context).also {
        widgets.forEach { widget ->
            it.addView(widget.toView(drawer))
        }
    }
    is Text -> TextView(drawer.context).also {
        it.text = text
        textSize?.apply {
            it.setTextSize(TypedValue.COMPLEX_UNIT_PX, toSize(it.context).toFloat())
        }
        textColor?.apply {
            it.setTextColor(toColor())
        }
        it.paint.isStrikeThruText = deleteLine
        it.paint.isUnderlineText = underLine
        it.paint.isAntiAlias = true
        textStyle?.apply {
            it.typeface = toTypeFace()
        }
        maxLines?.apply {
            it.maxLines = this
        }
        it.ellipsize = TextUtils.TruncateAt.END
    }
    is Image -> ImageView(drawer.context).also {
        it.adjustViewBounds = true
        scaleType?.apply {
            it.scaleType = toScaleType()
        }
        drawer.imageLoader.load(src, it)
    }
    is Empty -> View(drawer.context)
    is Clip -> ClipView(drawer.context).also {
        widget?.apply {
            it.addView(toView(drawer))
        }
        it.radii = cornerRadii.flatMap { point ->
            point.toList()
        }.map { size ->
            size.toSize(drawer.context).toFloat()
        }.toFloatArray()
        it.shape = shape ?: ""
    }
}.also { it.setup(this) }
