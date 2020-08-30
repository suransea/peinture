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

package top.srsea.peinture.vlparser.lex

import top.srsea.peinture.vlparser.token.*

private const val END = 0x1A.toChar()

class LexException(msg: String) : Exception(msg)

private fun Char.isLetter() = this in 'A'..'Z' || this in 'a'..'z' || this == '_'

private fun Char.isDecimal() = this in '0'..'9'

private fun Char.isIdentPart() = isLetter() || isDecimal()

private fun Char.isWhitespace() = this == ' ' || this == '\t' || this == '\n' || this == '\r'

class Lexer(src: String) {
    private val src = src.toCharArray()
    private var pos = -1
    private var ch = END

    init {
        next()
    }

    fun lex(): Token {
        skipWhitespace()
        return when {
            ch.isLetter() -> lexIdent().tryTransform()
            ch.isDecimal() -> lexNumber()
            else -> when (ch) {
                '-' -> lexNumber()
                '\'', '\"' -> lexString()
                '/' -> lexComment()
                '(' -> lexTo(Symbol.LPAREN)
                ')' -> lexTo(Symbol.RPAREN)
                '[' -> lexTo(Symbol.LBRACK)
                ']' -> lexTo(Symbol.RBRACK)
                '{' -> lexTo(Symbol.LBRACE)
                '}' -> lexTo(Symbol.RBRACE)
                '=' -> lexTo(Symbol.ASSIGN)
                ',' -> lexTo(Symbol.COMMA)
                END -> lexTo(Special.END)
                else -> lexTo(Special.ILLEGAL)
            }
        }
    }

    private fun lexIdent(): IdentLit {
        val begin = pos
        nextWhen { ch.isIdentPart() }
        return IdentLit(literalsFrom(begin))
    }

    private fun lexNumber(): Literals {
        val begin = pos
        if (ch == '-') {
            next()
            if (!ch.isDecimal()) {
                throw LexException("expected a number at pos $pos")
            }
        }
        nextWhen { ch.isDecimal() }
        if (ch != '.') {
            return IntLit(literalsFrom(begin))
        }

        next() // consume '.'
        if (!ch.isDecimal()) {
            throw LexException("expected a number at pos $pos")
        }
        nextWhen { ch.isDecimal() }
        return FloatLit(literalsFrom(begin))
    }

    private fun lexString(): StringLit {
        val beginChar = ch
        next()
        val begin = pos
        while (ch != beginChar) {
            if (ch == '\n') {
                throw LexException("unexpected char '\\n' at pos $pos")
            }
            if (ch == '\\') {
                if (peek() == beginChar) {
                    next()
                }
            }
            next()
        }
        if (ch == END) {
            throw LexException("expected the terminal char '$beginChar' from pos $begin")
        }
        val result = StringLit(literalsFrom(begin))
        next() // consume terminal char
        return result
    }

    private fun lexComment(): Comment {
        val begin = pos
        next()
        when (ch) {
            '/' -> {
                nextWhen { ch != '\n' }
                return Comment(literalsFrom(begin))
            }
            '*' -> {
                nextUntil { ch == '*' && peek() == '/' }
                if (ch == END) {
                    throw LexException("expected the terminal '*/' from pos $begin")
                }
                next()  // consume '*'
                next()  // consume '/'
                return Comment(literalsFrom(begin))
            }
            else -> throw LexException("expected a '/' or '*' at pos $pos")
        }
    }

    private fun lexTo(token: Token) = token.also { next() }

    private fun literalsFrom(begin: Int) = String(src, begin, pos - begin)

    private fun peek() = src.getOrElse(pos + 1) { END }

    private fun next() {
        ch = src.getOrElse(++pos) { END }
    }

    private fun nextWhen(pred: () -> Boolean) {
        while (pred()) next()
    }

    private fun nextUntil(pred: () -> Boolean) {
        while (!pred()) next()
    }

    private fun skipWhitespace() {
        while (ch.isWhitespace()) next()
    }
}
