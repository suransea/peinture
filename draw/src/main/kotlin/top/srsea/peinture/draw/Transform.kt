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
import android.support.constraint.ConstraintLayout
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import top.srsea.peinture.vlparser.type.*

private fun String.toId(): Int {
    return when (this) {
        "" -> ConstraintLayout.LayoutParams.UNSET
        "parent" -> ConstraintLayout.LayoutParams.PARENT_ID
        else -> toInt()
    }
}

private fun String.toColor() = Color.parseColor(this)

private fun String.toSize(ctx: Context): Int {
    return when {
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
}


private fun View.setup(widget: Widget) {
    id = widget.id.toId()
    setBackgroundColor(widget.color.toColor())
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

fun Widget.toView(drawer: Drawer): View {
    val ctx = drawer.context
    return when (this) {
        is Composite -> ConstraintLayout(ctx).also {
            widgets.forEach { widget ->
                it.addView(widget.toView(drawer))
            }
        }
        is Text -> TextView(ctx).also {
            it.text = text
            textSize?.apply {
                it.textSize = toSize(ctx).toFloat()
            }
            textColor?.apply {
                it.setTextColor(toColor())
            }
        }
        is Image -> ImageView(ctx).also {
            it.adjustViewBounds = true
            drawer.imageLoader.load(src, it)
        }
        is Empty -> View(ctx)
    }.also {
        it.setup(this)
    }
}
