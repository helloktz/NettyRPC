package com.newlandframework.rpc.compiler.weaver;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractClassTransformer implements Transformer {
	@Override
	public Class<?> transform(ClassLoader classLoader, Class<?>... proxyClasses) {
		return null;
	}

	protected Method[] findImplementationMethods(Class<?>[] proxyClasses) {
		Map<MethodDescriptor, Method> descriptorMap = new HashMap<>(1024);
		Set<MethodDescriptor> finalSet = new HashSet<>();

		for (int i = 0; i < proxyClasses.length; i++) {
			Class<?> proxyInterface = proxyClasses[i];
			Method[] methods = proxyInterface.getMethods();
			for (int j = 0; j < methods.length; j++) {
				MethodDescriptor descriptor = new MethodDescriptor(methods[j]);
				if (Modifier.isFinal(methods[j].getModifiers())) {
					finalSet.add(descriptor);
				} else if (!descriptorMap.containsKey(descriptor)) {
					descriptorMap.put(descriptor, methods[j]);
				}
			}
		}

		Collection<Method> results = descriptorMap.values();
		for (MethodDescriptor signature : finalSet) {
			results.remove(descriptorMap.get(signature));
		}

		return results.toArray(new Method[results.size()]);
	}
}
