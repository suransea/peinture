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

package top.srsea.peinture.vlparser.type

private const val ZERO = "0"
private const val EMPTY = ""
private const val TRANSPARENT = "#00000000"

sealed class Widget {
    var id = EMPTY
    var color: String? = null
    var transform: Transform? = null
    var constraint = Constraint()
    var padding = Rect()
}

class Rect {
    var top = ZERO
    var left = ZERO
    var right = ZERO
    var bottom = ZERO
}

class Constraint {
    var width = EMPTY
    var height = EMPTY
    var baselineToBaseLine = EMPTY
    var leftToLeft = EMPTY
    var leftToRight = EMPTY
    var topToTop = EMPTY
    var topToBottom = EMPTY
    var rightToRight = EMPTY
    var rightToLeft = EMPTY
    var bottomToBottom = EMPTY
    var bottomToTop = EMPTY
    var margin = Rect()
}

class Transform {
    var pivot: Pair<String, String>? = null
    var scroll = ZERO to ZERO
    var translation = Triple(ZERO, ZERO, ZERO)
    var scale = 1f to 1f
    var rotation = Triple(0f, 0f, 0f)
    var alpha: Float? = null
}

class Gradient {
    var colors = arrayOf(TRANSPARENT, TRANSPARENT)
    var type: String? = null
    var orientation: String? = null
    var radius = ZERO
    var center = 0.5f to 0.5f
}

class Composite : Widget() {
    val widgets = mutableListOf<Widget>()
}

class Card : Widget() {
    var widget: Widget? = null
    var cardRadius = ZERO
}

class Empty : Widget()

class Text(val text: String) : Widget() {
    var textSize: String? = null
    var textColor: String? = null
    var textStyle: String? = null
    var deleteLine: Boolean? = null
    var underLine: Boolean? = null
    var maxLines: Int? = null
}

class Image(val src: String) : Widget() {
    var scaleType: String? = null
}

class Shape : Widget() {
    var shape: String? = null
    var fillColor = TRANSPARENT
    var strokeColor = TRANSPARENT
    var strokeWidth = ZERO
    var strokeLength = ZERO
    var strokeSpace = ZERO
    var cornerRadii = arrayOf(ZERO to ZERO, ZERO to ZERO, ZERO to ZERO, ZERO to ZERO)
    var gradient: Gradient? = null
}
