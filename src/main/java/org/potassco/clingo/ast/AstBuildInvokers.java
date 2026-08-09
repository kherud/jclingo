/*
 * Copyright (C) 2021 denkbares GmbH, Germany
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package org.potassco.clingo.ast;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.ffi.ClingoH;
import org.potassco.clingo.internal.ffi.clingo_ast_argument;
import org.potassco.clingo.internal.ffi.clingo_ast_constructor;
import org.potassco.clingo.internal.ffi.clingo_ast_constructors;
import org.potassco.clingo.symbol.Symbol;

/**
 * Calls the variadic {@code clingo_ast_build}.
 * <p>
 * Which arguments a node type takes, and how they are laid out on the stack, is not transcribed here: clingo exports
 * that table itself as {@code g_clingo_ast_constructors}, and this class derives the call shape from it. The library is
 * therefore the single source of truth for its own calling convention, on every platform.
 */
final class AstBuildInvokers {

    static {
        Clingo.init();
    }

    private static final Map<AstType, Invoker> INVOKERS = new EnumMap<>(AstType.class);

    private AstBuildInvokers() {
    }

    /**
     * Builds a node of the given type.
     *
     * @param type      the type of node to build
     * @param arguments one value per constructor argument of that type: {@link Integer} for a number, {@link Symbol}
     *                  for a symbol, {@link Location} for a location, {@link String} for a string, {@link Ast} for a
     *                  node, {@code null} or {@link Ast} for an optional node, {@link String}{@code []} or
     *                  {@link StringSequence} for a string array, and {@link Ast}{@code []} or {@link AstSequence} for
     *                  a node array
     * @return the new node, owned by the caller
     */
    static MemorySegment build(AstType type, Object... arguments) {
        return invoker(type).build(type, arguments);
    }

    private static synchronized Invoker invoker(AstType type) {
        return INVOKERS.computeIfAbsent(type, key -> new Invoker(specificationOf(key)));
    }

    /**
     * Reads the constructor arguments of {@code type} from the table clingo exports.
     */
    static AttributeType[] specificationOf(AstType type) {
        MemorySegment table = ClingoH.g_clingo_ast_constructors();
        long constructorCount = clingo_ast_constructors.size(table);
        if (type.getValue() < 0 || type.getValue() >= constructorCount) {
            throw new IllegalArgumentException("the loaded clingo has no constructor for " + type);
        }
        MemorySegment constructors = clingo_ast_constructors.constructors(table)
                .reinterpret(constructorCount * clingo_ast_constructor.sizeof());
        MemorySegment constructor = clingo_ast_constructor.asSlice(constructors, type.getValue());
        long argumentCount = clingo_ast_constructor.size(constructor);
        MemorySegment argumentsOfType = clingo_ast_constructor.arguments(constructor)
                .reinterpret(argumentCount * clingo_ast_argument.sizeof());
        AttributeType[] specification = new AttributeType[(int) argumentCount];
        for (int i = 0; i < specification.length; i++) {
            specification[i] = AttributeType.fromValue(
                    clingo_ast_argument.type(clingo_ast_argument.asSlice(argumentsOfType, i)));
        }
        return specification;
    }

    /**
     * The call shape for one node type, together with the invoker built from it.
     */
    private static final class Invoker {

        private final AttributeType[] specification;
        private final ClingoH.clingo_ast_build invoker;

        private Invoker(AttributeType[] specification) {
            this.specification = specification;
            List<MemoryLayout> layouts = new ArrayList<>();
            for (AttributeType attribute : specification) {
                switch (attribute) {
                    case NUMBER -> layouts.add(ValueLayout.JAVA_INT);
                    case SYMBOL -> layouts.add(ValueLayout.JAVA_LONG);
                    case LOCATION, STRING, AST, OPTIONAL_AST -> layouts.add(ValueLayout.ADDRESS);
                    case STRING_ARRAY, AST_ARRAY -> {
                        layouts.add(ValueLayout.ADDRESS);
                        layouts.add(ValueLayout.JAVA_LONG);
                    }
                }
            }
            this.invoker = ClingoH.clingo_ast_build.makeInvoker(layouts.toArray(new MemoryLayout[0]));
        }

        private MemorySegment build(AstType type, Object[] arguments) {
            if (arguments.length != specification.length) {
                throw new IllegalArgumentException(type + " takes " + specification.length + " arguments, got "
                                                   + arguments.length);
            }
            try (Arena call = Arena.ofConfined()) {
                MemorySegment out = call.allocate(ValueLayout.ADDRESS);
                List<Object> variadic = new ArrayList<>();
                for (int i = 0; i < specification.length; i++) {
                    marshal(type, i, arguments[i], call, variadic);
                }
                Clingo.check(invoker.apply(type.getValue(), out, variadic.toArray()));
                return out.get(ValueLayout.ADDRESS, 0);
            }
        }

        private void marshal(AstType type, int index, Object argument, Arena arena, List<Object> variadic) {
            switch (specification[index]) {
                case NUMBER -> variadic.add(expect(type, index, argument, Integer.class));
                case SYMBOL -> variadic.add(argument instanceof Long value ? value
                        : expect(type, index, argument, Symbol.class).getLong());
                case LOCATION -> variadic.add(expect(type, index, argument, Location.class).toNative(arena));
                case STRING -> variadic.add(arena.allocateFrom(expect(type, index, argument, String.class)));
                case AST -> variadic.add(expect(type, index, argument, Ast.class).getPointer());
                case OPTIONAL_AST -> variadic.add(argument == null ? MemorySegment.NULL
                        : expect(type, index, argument, Ast.class).getPointer());
                case STRING_ARRAY -> {
                    String[] strings = argument instanceof StringSequence sequence
                            ? sequence.get()
                            : expect(type, index, argument, String[].class);
                    MemorySegment array = arena.allocate(ValueLayout.ADDRESS, strings.length);
                    for (int i = 0; i < strings.length; i++) {
                        array.setAtIndex(ValueLayout.ADDRESS, i, arena.allocateFrom(strings[i]));
                    }
                    variadic.add(array);
                    variadic.add((long) strings.length);
                }
                case AST_ARRAY -> {
                    MemorySegment[] nodes = argument instanceof AstSequence sequence
                            ? sequence.getPointer()
                            : pointersOf(expect(type, index, argument, Ast[].class));
                    MemorySegment array = arena.allocate(ValueLayout.ADDRESS, nodes.length);
                    for (int i = 0; i < nodes.length; i++) {
                        array.setAtIndex(ValueLayout.ADDRESS, i, nodes[i]);
                    }
                    variadic.add(array);
                    variadic.add((long) nodes.length);
                }
            }
        }

        private <T> T expect(AstType type, int index, Object argument, Class<T> expected) {
            if (!expected.isInstance(argument)) {
                throw new IllegalArgumentException("argument " + index + " of " + type + " must be a "
                                                   + expected.getSimpleName() + ", got "
                                                   + (argument == null ? "null" : argument.getClass().getName()));
            }
            return expected.cast(argument);
        }

        private static MemorySegment[] pointersOf(Ast[] nodes) {
            MemorySegment[] pointers = new MemorySegment[nodes.length];
            for (int i = 0; i < nodes.length; i++) {
                pointers[i] = nodes[i].getPointer();
            }
            return pointers;
        }
    }
}
