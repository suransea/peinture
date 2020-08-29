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

import top.srsea.peinture.vlparser.ast.Decl
import top.srsea.peinture.vlparser.parse.Parser
import top.srsea.peinture.vlparser.type.*

typealias WidgetMap = HashMap<String, Widget>

open class AnalyzeException(msg: String) : Exception(msg)

class Analyzer(vl: String) {
    private val root = Parser(vl).parse()
    private val varMap = root.vars.map { it.name to it.decl }.toMap()
    private val widgetMap = WidgetMap()

    fun analyze(): Widget {
        return analyzeWidget(root.decl)
            ?: throw AnalyzeException("unrecognized top level declaration \"${root.decl.type}\"")
    }

    private fun analyzeWidget(decl: Decl): Widget? {
        val result = when (decl.type) {
            "Composite" -> analyzeComposite(decl)
            "Empty" -> Empty()
            "Text" -> analyzeText(decl)
            "Image" -> analyzeImage(decl)
            in widgetMap -> widgetMap[decl.type]
            in varMap -> varMap[decl.type]?.let {
                analyzeWidget(it)?.apply {
                    widgetMap[decl.type] = this
                }
            }
            else -> null
        }
        return result?.also {
            attachProps(it, decl)
        }
    }

    private fun analyzeComposite(decl: Decl): Composite {
        return Composite().apply {
            decl.decls.forEach {
                analyzeWidget(it)?.also { widget ->
                    widgets += widget
                }
            }
        }
    }

    private fun analyzeText(decl: Decl): Text {
        decl.arg ?: throw AnalyzeException("the view 'Text' require an argument")
        return Text(decl.arg.literals).apply {
            decl.props.forEach {
                val value = it.value.literals
                when (it.name) {
                    "textSize" -> textSize = value
                    "textColor" -> textColor = value
                }
            }
        }
    }

    private fun analyzeImage(decl: Decl): Image {
        val src = decl.props.lastOrNull { it.name == "src" }?.value?.literals
            ?: decl.arg?.literals
            ?: throw AnalyzeException("the view 'Image' require an argument 'src'")
        return Image(src)
    }

    private fun attachProps(widget: Widget, decl: Decl) {
        decl.props.forEach {
            val value = it.value.literals
            when (it.name) {
                "id" -> widget.id = value
                "width" -> widget.width = value
                "height" -> widget.height = value
                "color" -> widget.color = value
                "size" -> {
                    widget.height = value
                    widget.width = value
                }
            }
        }
        widget.constraint = obtainConstraint(decl)
        widget.margin = obtainRect(decl, type = "Margin")
        widget.padding = obtainRect(decl, type = "Padding")
    }

    private fun obtainConstraint(decl: Decl): Constraint {
        return Constraint().apply {
            decl.decls.filter {
                it.type == "Constraint"
            }.flatMap {
                it.props
            }.forEach {
                val value = it.value.literals
                when (it.name) {
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
    }

    private fun obtainRect(decl: Decl, type: String): Rect {
        return Rect().apply {
            decl.decls.filter {
                it.type == type
            }.flatMap {
                it.props
            }.forEach {
                val value = it.value.literals
                when (it.name) {
                    "top" -> top = value
                    "bottom" -> bottom = value
                    "left" -> left = value
                    "right" -> right = value
                }
            }
        }
    }
}
