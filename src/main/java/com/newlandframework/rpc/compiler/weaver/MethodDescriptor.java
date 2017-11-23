package com.newlandframework.rpc.compiler.weaver;

import static java.lang.Character.valueOf;

import java.lang.reflect.Method;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class MethodDescriptor {
	private static final Map<Class<?>, Character> BUILDER = new ImmutableMap.Builder<Class<?>, Character>().put(Boolean.TYPE, valueOf('Z')).put(Byte.TYPE, valueOf('B')).put(Short.TYPE, valueOf('S'))
			.put(Integer.TYPE, valueOf('I')).put(Character.TYPE, valueOf('C')).put(Long.TYPE, valueOf('J')).put(Float.TYPE, valueOf('F')).put(Double.TYPE, valueOf('D')).put(Void.TYPE, valueOf('V'))
			.build();

	private final String internal;

	public MethodDescriptor(Method method) {
		final StringBuilder buf = new StringBuilder(method.getName()).append('(');
		for (Class<?> p : method.getParameterTypes()) {
			appendTo(buf, p);
		}

		buf.append(')');
		this.internal = buf.toString();
	}

	private static void appendTo(StringBuilder buf, Class<?> type) {
		if (type.isPrimitive()) {
			buf.append(BUILDER.get(type));
		} else if (type.isArray()) {
			buf.append('[');
			appendTo(buf, type.getComponentType());
		} else {
			buf.append('L').append(type.getName().replace('.', '/')).append(';');
		}
	}
}
