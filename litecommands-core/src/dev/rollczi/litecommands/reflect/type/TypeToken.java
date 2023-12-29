/*
 * Copyright (C) 2006 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package dev.rollczi.litecommands.reflect.type;

import dev.rollczi.litecommands.shared.Preconditions;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeToken<T> {

    private final Class<T> rawType;
    private final Type type;

    @SuppressWarnings("unchecked")
    protected TypeToken() {
        this.type = capture();
        this.rawType = (Class<T>) TypeUtil.getRawType(type);
    }

    @SuppressWarnings("unchecked")
    private TypeToken(Type type) {
        this.type = type;
        this.rawType = (Class<T>) TypeUtil.getRawType(type);
    }

    private Type capture() {
        Type superclass = getClass().getGenericSuperclass();
        Preconditions.checkArgument(superclass instanceof java.lang.reflect.ParameterizedType, "%s isn't parameterized", superclass);

        return ((ParameterizedType) superclass).getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }

    public Class<T> getRawType() {
        return rawType;
    }

    public final boolean isPrimitive() {
        return this.type instanceof Class && ((Class<?>) this.type).isPrimitive();
    }

    public boolean isInstanceOf(TypeToken<?> typeToken) {
        return isInstanceOf(typeToken.getRawType());
    }

    public boolean isInstanceOf(Class<?> parsedType) {
        return parsedType == Object.class || parsedType.isAssignableFrom(getRawType());
    }

    public TypeToken<?> getParameterized() {
        return getParameterized(0);
    }

    public TypeToken<?> getParameterized(int index) {
        if (this.type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) this.type;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

            if (actualTypeArguments.length > index) {
                return of(actualTypeArguments[index]);
            }
        }

        throw new IllegalStateException("Cannot resolve parameterized type");
    }

    public static <T> TypeToken<T> of(Class<T> type) {
        return new SimpleTypeToken<>(type);
    }

    public static <T> TypeToken<T> of(Type type) {
        return new SimpleTypeToken<>(type);
    }

    public boolean isArray() {
        return getRawType().isArray();
    }

    public Class<?> getComponentType() {
        return getRawType().getComponentType();
    }

    public TypeToken<?> getComponentTypeToken() {
        return of(getComponentType());
    }

    private static class SimpleTypeToken<T> extends TypeToken<T> {
        public SimpleTypeToken(Class<T> type) {
            super(type);
        }

        public SimpleTypeToken(Type type) {
            super(type);
        }
    }

}