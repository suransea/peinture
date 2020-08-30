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

package top.srsea.peinture.vlparser.parse

import top.srsea.peinture.vlparser.ast.*
import top.srsea.peinture.vlparser.lex.Lexer
import top.srsea.peinture.vlparser.token.*
import kotlin.reflect.KClass

open class ParseException(msg: String) : Exception(msg)

class UnexpectedTokenException(expected: KClass<out Token>, actual: Token) : ParseException(
    "expected a ${expected.simpleName}, actual token is ${actual::class.simpleName} \"${actual.literals}\""
)

class Parser(src: String) {
    private val lexer = Lexer(src)
    private var token = Special.END as Token

    init {
        next()
    }

    fun parse(): Root {
        val vars = mutableListOf<Var>()
        var decl = null as Decl?
        while (token != Special.END) {
            when (token) {
                Keyword.LET -> vars += parseVar()
                is IdentLit -> {
                    decl?.also { throw ParseException("at most one root declaration") }
                    decl = parseDecl()
                }
                else -> throw ParseException("expected a ident or \"let\"")
            }
        }
        decl ?: throw ParseException("at least one root declaration")
        return Root(decl, vars)
    }

    private fun parseDecl(type: IdentLit = expect()): Decl {
        var arg = TupleRhs()
        if (token == Symbol.LPAREN) {
            arg = parseTuple()
        }
        expect(Symbol.LBRACE)
        val props = mutableListOf<Prop>()
        val decls = mutableListOf<Decl>()
        while (true) {
            if (token == Symbol.RBRACE) {
                break
            }
            val ident = expect<IdentLit>()
            when (token) {
                Symbol.ASSIGN -> props += parseProp(ident)
                Symbol.LPAREN, Symbol.LBRACE -> decls += parseDecl(ident)
                is IdentLit, Symbol.RBRACE -> continue
                else -> expect(Symbol.ASSIGN)
            }
        }
        expect(Symbol.RBRACE)
        return Decl(type.literals, arg, props, decls)
    }

    private fun parseVar(): Var {
        expect(Keyword.LET)
        val name = expect<IdentLit>()
        expect(Symbol.ASSIGN)
        val decl = parseDecl()
        return Var(name.literals, decl)
    }

    private fun parseProp(name: IdentLit): Prop {
        expect(Symbol.ASSIGN)
        return Prop(name.literals, parseRhs())
    }

    private fun parseRhs(): Rhs = when (token) {
        is ValueLit -> ValueRhs(expect<ValueLit>().literals)
        Symbol.LPAREN -> parseTuple()
        Symbol.LBRACK -> parseArray()
        else -> throw ParseException(
            "expected a rhs expression, actual token is ${token::class.simpleName} \"${token.literals}\""
        )
    }

    private fun parseTuple(): TupleRhs {
        expect(Symbol.LPAREN)
        val rhs = mutableListOf<Rhs>()
        while (token != Symbol.RPAREN) {
            rhs += parseRhs()
            if (token == Symbol.COMMA) {
                expect(Symbol.COMMA)
            }
        }
        expect(Symbol.RPAREN)
        return TupleRhs(rhs)
    }

    private fun parseArray(): ArrayRhs {
        expect(Symbol.LBRACK)
        val rhs = mutableListOf<Rhs>()
        while (token != Symbol.RBRACK) {
            rhs += parseRhs()
            if (token == Symbol.COMMA) {
                expect(Symbol.COMMA)
            }
        }
        expect(Symbol.RBRACK)
        return ArrayRhs(rhs)
    }

    private inline fun <reified T : Token> expect(): T {
        val tok = token
        if (tok !is T) {
            throw UnexpectedTokenException(T::class, tok)
        }
        return tok.also { next() }
    }

    private fun expect(token: Token): Token {
        val tok = this.token
        if (tok != token) {
            throw UnexpectedTokenException(token::class, tok)
        }
        return tok.also { next() }
    }

    private fun next() {
        token = lexer.lex()
        if (token is Comment) next()  // skip comment
    }
}
