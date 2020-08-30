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

package top.srsea.peinture.vlparser.ast

sealed class Node

class Root(
    val decl: Decl,
    val vars: List<Var> = emptyList()
) : Node()

sealed class Rhs : Node()

class ValueRhs(val value: String) : Rhs()

class TupleRhs(
    val items: List<Rhs> = emptyList()
) : Rhs()

class ArrayRhs(
    val items: List<Rhs> = emptyList()
) : Rhs()

class Decl(
    val type: String,
    val arg: TupleRhs = TupleRhs(),
    val props: List<Prop> = emptyList(),
    val decls: List<Decl> = emptyList()
) : Node()

class Var(
    val name: String,
    val decl: Decl
) : Node()

class Prop(
    val name: String,
    val value: Rhs
) : Node()
