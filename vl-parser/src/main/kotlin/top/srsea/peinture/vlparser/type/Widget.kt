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
    var color = TRANSPARENT
    var alpha = 1f
    var transform: Transform? = null
    var constraint = Constraint()
    var padding = Padding()
    var shape: String? = null
    var borderColor = TRANSPARENT
    var borderWidth = ZERO
    var borderLength = ZERO
    var borderSpace = ZERO
    var cornerRadii = arrayOf(ZERO to ZERO, ZERO to ZERO, ZERO to ZERO, ZERO to ZERO)
    var gradient: Gradient? = null
}

class Padding {
    var top = ZERO
    var left = ZERO
    var right = ZERO
    var bottom = ZERO
}

class Constraint {
    var width = ZERO
    var height = ZERO
    var top = ZERO
    var left = ZERO
    var bottom = ZERO
    var right = ZERO
    var baselineToBaseLine = EMPTY
    var leftToLeft = EMPTY
    var leftToRight = EMPTY
    var topToTop = EMPTY
    var topToBottom = EMPTY
    var rightToRight = EMPTY
    var rightToLeft = EMPTY
    var bottomToBottom = EMPTY
    var bottomToTop = EMPTY
    var widthToHeight = EMPTY
    var heightToWidth = EMPTY
}

class Transform {
    var pivot: Pair<String, String>? = null
    var scroll = ZERO to ZERO
    var translation = Triple(ZERO, ZERO, ZERO)
    var scale = 1f to 1f
    var rotation = Triple(0f, 0f, 0f)
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

class Clip : Widget() {
    var widget: Widget? = null
}

class Empty : Widget()

class Text(val text: String) : Widget() {
    var textSize: String? = null
    var textColor: String? = null
    var textStyle: String? = null
    var deleteLine = false
    var underLine = false
    var maxLines: Int? = null
}

class Image(val src: String) : Widget() {
    var scaleType: String? = null
}
