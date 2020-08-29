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

package top.srsea.peinture.vlparser.token

interface Token {
    val literals: String
}


// symbols

enum class Symbol(override val literals: String) : Token {
    LPAREN("("),
    RPAREN(")"),
    LBRACE("{"),
    RBRACE("}"),
    ASSIGN("="),
}


// special tokens

enum class Special(override val literals: String) : Token {
    END("END"),
    ILLEGAL("ILLEGAL"),
}

class Comment(override val literals: String) : Token


// keywords

enum class Keyword(override val literals: String) : Token {
    LET("let")
}

private val keywordMap = Keyword.values().map { it.literals to it }.toMap()


// literals

sealed class Literals : Token

class IdentLit(override val literals: String) : Literals() {
    internal fun tryToKeyword() = keywordMap[literals] ?: this
}

sealed class ValueLit : Literals()

class IntLit(override val literals: String) : ValueLit()

class FloatLit(override val literals: String) : ValueLit()

class StringLit(override val literals: String) : ValueLit()
