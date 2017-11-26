package com.newlandframework.rpc.compiler;

public interface Compiler {
	Class<?> compile(String sourceCode, ClassLoader classLoader);
}
