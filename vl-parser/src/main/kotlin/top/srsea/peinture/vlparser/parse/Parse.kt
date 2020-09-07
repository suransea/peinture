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
    private var token = End as Token

    init {
        next()
    }

    fun parse(): Root {
        val vars = mutableListOf<Var>()
        val decls = mutableListOf<Decl>()
        while (token != End) {
            when (token) {
                Keyword.LET -> vars += parseVar()
                is IdentLit -> {
                    decls += parseDecl()
                }
                else -> throw ParseException("expected a ident or \"let\"")
            }
        }
        return decls.takeIf { it.size == 1 }?.let { Root(it[0], vars) }
            ?: throw ParseException("expected one top declaration, actual count is ${decls.size}")
    }

    private fun parseDecl(type: IdentLit = expect()): Decl {
        val arg = if (token == Symbol.LPAREN) parseTuple() else TupleRhs()
        expect(Symbol.LBRACE)
        val props = mutableListOf<Prop>()
        val decls = mutableListOf<Decl>()
        loop@ while (token != Symbol.RBRACE) {
            val ident = expect<IdentLit>()
            when (token) {
                Symbol.ASSIGN -> props += parseProp(ident)
                Symbol.LPAREN, Symbol.LBRACE -> decls += parseDecl(ident)
                is IdentLit, Symbol.RBRACE -> continue@loop
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
        return Var(name.literals, parseDecl())
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
        val current = token
        if (current !is T) {
            throw UnexpectedTokenException(T::class, current)
        }
        return current.also { next() }
    }

    private fun expect(expected: Token): Token {
        val current = token
        if (current != expected) {
            throw UnexpectedTokenException(expected::class, current)
        }
        return current.also { next() }
    }

    private fun next() {
        token = lexer.lex()
        if (token is Comment) next()  // skip comment
    }
}
