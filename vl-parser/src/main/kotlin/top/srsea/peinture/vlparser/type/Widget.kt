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

private const val NO_ID = ""
private const val MATCH = "match"
private const val ZERO = "0"
private const val TRANSPARENT = "#00000000"

sealed class Widget {
    var id = NO_ID
    var width = MATCH
    var height = MATCH
    var color = TRANSPARENT
    var constraint = Constraint()
    var padding = Rect()
    var margin = Rect()
}

class Rect {
    var top = ZERO
    var left = ZERO
    var right = ZERO
    var bottom = ZERO
}

class Constraint {
    var baseline = NO_ID
    var ll = NO_ID
    var lr = NO_ID
    var tt = NO_ID
    var tb = NO_ID
    var rr = NO_ID
    var rl = NO_ID
    var bb = NO_ID
    var bt = NO_ID
}

class Composite : Widget() {
    val widgets = mutableListOf<Widget>()
}

class Empty : Widget()

class Text(val text: String) : Widget() {
    var textSize: String? = null
    var textColor: String? = null
    var textStyle: String? = null
    var deleteLine: Boolean? = null
    var underLine: Boolean? = null
}

class Image(val src: String) : Widget() {
    var scaleType: String? = null
}
