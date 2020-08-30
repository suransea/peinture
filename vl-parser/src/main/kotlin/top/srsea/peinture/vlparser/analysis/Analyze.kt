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

package top.srsea.peinture.vlparser.analysis

import top.srsea.peinture.vlparser.ast.*
import top.srsea.peinture.vlparser.parse.Parser
import top.srsea.peinture.vlparser.type.*

open class AnalyzeException(msg: String) : Exception(msg)

private fun Rhs.asString() = (this as? ValueRhs)?.value ?: throw AnalyzeException("expected an value rhs expression")

private fun Rhs.asStringArray() = when (this) {
    is TupleRhs -> items.map { it.asString() }.toTypedArray()
    is ArrayRhs -> items.map { it.asString() }.toTypedArray()
    else -> throw AnalyzeException("expected an array or tuple rhs expression")
}

private fun Rhs.asPairArray() = when (this) {
    is TupleRhs -> items.map { it.asStringArray() }.map { it[0] to it[1] }.toTypedArray()
    is ArrayRhs -> items.map { it.asStringArray() }.map { it[0] to it[1] }.toTypedArray()
    else -> throw AnalyzeException("expected an array or tuple rhs expression")
}

private fun Rhs.asPair() = when (this) {
    is TupleRhs -> items.map { it.asString() }.let { it[0] to it[1] }
    is ArrayRhs -> items.map { it.asString() }.let { it[0] to it[1] }
    else -> throw AnalyzeException("expected an array or tuple rhs expression")
}

private fun Rhs.asTriple() = when (this) {
    is TupleRhs -> items.map { it.asString() }.let { Triple(it[0], it[1], it[2]) }
    is ArrayRhs -> items.map { it.asString() }.let { Triple(it[0], it[1], it[2]) }
    else -> throw AnalyzeException("expected an array or tuple rhs expression")
}

class Analyzer(vl: String) {
    private val root = Parser(vl).parse()
    private val varMap = root.vars.map { it.name to it.decl }.toMap()

    fun analyze(): Widget {
        return analyzeWidget(root.decl)
            ?: throw AnalyzeException("unrecognized top level declaration \"${root.decl.type}\"")
    }

    private fun analyzeWidget(decl: Decl): Widget? = when (decl.type) {
        "Composite" -> analyzeComposite(decl)
        "Empty" -> Empty()
        "Text" -> analyzeText(decl)
        "Image" -> analyzeImage(decl)
        "Card" -> analyzeCard(decl)
        "Shape" -> analyzeShape(decl)
        in varMap -> varMap[decl.type]?.let { analyzeWidget(it) }
        else -> null
    }?.also { attachProps(it, decl) }

    private fun analyzeComposite(decl: Decl) = Composite().apply {
        decl.decls.forEach {
            analyzeWidget(it)?.also { widget ->
                widgets += widget
            }
        }
    }

    private fun analyzeText(decl: Decl): Text {
        val text = decl.arg.items.getOrNull(0) as? ValueRhs
            ?: throw AnalyzeException("the view 'Text' require an argument")
        return Text(text.value).apply {
            decl.props.forEach {
                val value = it.value.asString()
                when (it.name) {
                    "textSize" -> textSize = value
                    "textColor" -> textColor = value
                    "textStyle" -> textStyle = value
                    "underLine" -> underLine = value.toBoolean()
                    "deleteLine" -> deleteLine = value.toBoolean()
                    "maxLines" -> maxLines = value.toInt()
                }
            }
        }
    }

    private fun analyzeImage(decl: Decl): Image {
        val src = decl.props.lastOrNull { it.name == "src" }?.value
            ?: decl.arg.items.getOrNull(0)
            ?: throw AnalyzeException("the view 'Image' require an argument 'src'")
        return Image(src.asString()).apply {
            decl.props.forEach {
                val value = it.value.asString()
                when (it.name) {
                    "scaleType" -> scaleType = value
                }
            }
        }
    }

    private fun analyzeCard(decl: Decl) = Card().apply {
        decl.decls.forEach {
            analyzeWidget(it)?.also { analyzedWidget ->
                widget = analyzedWidget
            }
        }
        decl.props.forEach {
            val value = it.value.asString()
            when (it.name) {
                "cardRadius" -> cardRadius = value
            }
        }
    }

    private fun analyzeShape(decl: Decl) = Shape().apply {
        decl.props.forEach {
            val value = it.value
            when (it.name) {
                "shape" -> shape = value.asString()
                "fillColor" -> fillColor = value.asString()
                "strokeColor" -> strokeColor = value.asString()
                "strokeWidth" -> strokeWidth = value.asString()
                "strokeLength" -> strokeLength = value.asString()
                "strokeSpace" -> strokeSpace = value.asString()
                "cornerRadii" -> cornerRadii = value.asPairArray()
                "cornerRadius" -> cornerRadii = value.asString().let { size ->
                    arrayOf(size to size, size to size, size to size, size to size)
                }
            }
        }
        gradient = obtainGradient(decl)
    }

    private fun attachProps(widget: Widget, decl: Decl) {
        decl.props.forEach {
            val value = it.value
            when (it.name) {
                "id" -> widget.id = value.asString()
                "width" -> widget.width = value.asString()
                "height" -> widget.height = value.asString()
                "color" -> widget.color = value.asString()
                "size" -> {
                    widget.height = value.asString()
                    widget.width = value.asString()
                }
            }
        }
        widget.constraint = obtainConstraint(decl)
        widget.margin = obtainRect(decl, type = "Margin")
        widget.padding = obtainRect(decl, type = "Padding")
        widget.transform = obtainTransform(decl)
    }

    private fun obtainConstraint(decl: Decl) = Constraint().apply {
        decl.decls.lastOrNull {
            it.type == "Constraint"
        }?.props?.forEach {
            val value = it.value.asString()
            when (it.name) {
                "baseline" -> baseline = value
                "ll" -> ll = value
                "lr" -> lr = value
                "tt" -> tt = value
                "tb" -> tb = value
                "rl" -> rl = value
                "rr" -> rr = value
                "bt" -> bt = value
                "bb" -> bb = value
            }
        }
    }

    private fun obtainTransform(decl: Decl) = Transform().apply {
        decl.decls.lastOrNull {
            it.type == "Transform"
        }?.props?.forEach {
            val value = it.value
            when (it.name) {
                "pivot" -> pivot = value.asPair()
                "scroll" -> scroll = value.asPair()
                "translation" -> translation = value.asTriple()
                "scale" -> scale = value.asPair().run { first.toFloat() to second.toFloat() }
                "rotation" -> rotation = value.asTriple()
                    .run { Triple(first.toFloat(), second.toFloat(), third.toFloat()) }
                "alpha" -> alpha = value.asString().toFloat()
            }
        }
    }

    private fun obtainRect(decl: Decl, type: String) = Rect().apply {
        decl.decls.lastOrNull {
            it.type == type
        }?.props?.forEach {
            val value = it.value.asString()
            when (it.name) {
                "top" -> top = value
                "bottom" -> bottom = value
                "left" -> left = value
                "right" -> right = value
            }
        }
    }

    private fun obtainGradient(decl: Decl): Gradient? {
        val gradient = decl.decls.lastOrNull { it.type == "Gradient" }
        gradient ?: return null
        return Gradient().apply {
            gradient.props.forEach { prop ->
                val value = prop.value
                when (prop.name) {
                    "colors" -> colors = value.asStringArray()
                    "type" -> type = value.asString()
                    "orientation" -> orientation = value.asString()
                    "radius" -> radius = value.asString()
                    "center" -> center = value.asStringArray()
                        .map(String::toFloat)
                        .let {
                            it[0] to it[1]
                        }
                }
            }
        }
    }
}
