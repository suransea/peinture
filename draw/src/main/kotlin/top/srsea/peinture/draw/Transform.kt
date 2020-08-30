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
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import top.srsea.peinture.draw.view.CardView
import top.srsea.peinture.vlparser.type.*

private fun String.toId(): Int = when (this) {
    "" -> ConstraintLayout.LayoutParams.UNSET
    "parent" -> ConstraintLayout.LayoutParams.PARENT_ID
    else -> toInt()
}

private fun String.toColor(): Int = Color.parseColor(this)

private fun String.toSize(ctx: Context): Int = when {
    this == "match" -> ViewGroup.LayoutParams.MATCH_PARENT
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
    widget.color?.apply {
        setBackgroundColor(toColor())
    }
    val param = ConstraintLayout.LayoutParams(widget.width.toSize(context), widget.height.toSize(context))
    val constraint = widget.constraint
    val margin = widget.margin
    layoutParams = param.apply {
        // margin
        topMargin = margin.top.toSize(context)
        leftMargin = margin.left.toSize(context)
        rightMargin = margin.right.toSize(context)
        bottomMargin = margin.bottom.toSize(context)

        // constraint
        topToTop = constraint.tt.toId()
        topToBottom = constraint.tb.toId()
        leftToLeft = constraint.ll.toId()
        leftToRight = constraint.lr.toId()
        rightToRight = constraint.rr.toId()
        rightToLeft = constraint.rl.toId()
        bottomToBottom = constraint.bb.toId()
        bottomToTop = constraint.bt.toId()
    }

    val padding = widget.padding
    setPadding(
        padding.left.toSize(context),
        padding.top.toSize(context),
        padding.right.toSize(context),
        padding.bottom.toSize(context)
    )
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
            it.textSize = toSize(drawer.context).toFloat()
        }
        textColor?.apply {
            it.setTextColor(toColor())
        }
        deleteLine?.apply {
            it.paint.isStrikeThruText = this
        }
        underLine?.apply {
            it.paint.isUnderlineText = this
        }
        textStyle?.apply {
            it.typeface = toTypeFace()
        }
    }
    is Image -> ImageView(drawer.context).also {
        it.adjustViewBounds = true
        scaleType?.apply {
            it.scaleType = toScaleType()
        }
        drawer.imageLoader.load(src, it)
    }
    is Empty -> View(drawer.context)
    is Card -> CardView(drawer.context).also {
        widget?.apply {
            it.addView(toView(drawer))
        }
        it.radius = cardRadius.toSize(drawer.context)
    }
    is Shape -> View(drawer.context).also {
        val bg = GradientDrawable()
        shape?.apply {
            bg.shape = toShape()
        }
        bg.setColor(fillColor.toColor())
        bg.setStroke(
            strokeWidth.toSize(drawer.context),
            strokeColor.toColor(),
            strokeLength.toSize(drawer.context).toFloat(),
            strokeSpace.toSize(drawer.context).toFloat()
        )
        bg.cornerRadii = cornerRadii.flatMap { point ->
            listOf(point.first, point.second)
        }.map { size ->
            size.toSize(drawer.context).toFloat()
        }.toFloatArray()

        gradient?.apply {
            type?.apply {
                bg.gradientType = toGradientType()
            }
            bg.colors = colors.map(String::toColor).toIntArray()
            orientation?.apply {
                bg.orientation = toGradientOrientation()
            }
            bg.gradientRadius = radius.toSize(drawer.context).toFloat()
            bg.setGradientCenter(center.first, center.second)
        }
        it.background = bg
    }
}.also { it.setup(this) }
