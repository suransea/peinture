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
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import top.srsea.peinture.vlparser.analysis.Analyzer

private fun Int.toMeasureSpec(): Int {
    return when (this) {
        ViewGroup.LayoutParams.MATCH_PARENT -> View.MeasureSpec.AT_MOST
        ViewGroup.LayoutParams.WRAP_CONTENT -> View.MeasureSpec.UNSPECIFIED
        else -> View.MeasureSpec.makeMeasureSpec(this, View.MeasureSpec.EXACTLY)
    }
}

class Drawer(val context: Context, val imageLoader: ImageLoader = GladeBlockingImageLoader) {

    fun drawBitmap(vl: String, config: Bitmap.Config): Bitmap {
        val rootView = drawView(vl)
        val width = rootView.layoutParams.width
        val height = rootView.layoutParams.height
        rootView.measure(width.toMeasureSpec(), height.toMeasureSpec())
        rootView.layout(0, 0, rootView.measuredWidth, rootView.measuredHeight)
        return Bitmap.createBitmap(rootView.measuredWidth, rootView.measuredHeight, config).apply {
            val canvas = Canvas(this)
            rootView.draw(canvas)
        }
    }

    fun drawView(vl: String) = Analyzer(vl).analyze().toView(this)
}

interface ImageLoader {
    fun load(src: String, view: ImageView)
}

object GladeImageLoader : ImageLoader {
    override fun load(src: String, view: ImageView) {
        Glide.with(view).load(src).into(view)
    }
}

object GladeBlockingImageLoader : ImageLoader {
    override fun load(src: String, view: ImageView) {
        val bitmap = Glide.with(view).asBitmap().load(src).submit().get()
        view.setImageBitmap(bitmap)
    }
}
