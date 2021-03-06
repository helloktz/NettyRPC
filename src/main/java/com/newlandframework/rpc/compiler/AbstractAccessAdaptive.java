package com.newlandframework.rpc.compiler;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.newlandframework.rpc.compiler.weaver.ClassProxy;
import com.newlandframework.rpc.compiler.weaver.ProxyProvider;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractAccessAdaptive implements Compiler {
	private static final Pattern PACKAGE_PATTERN = Pattern.compile("package\\s+([$_a-zA-Z][$_a-zA-Z0-9\\.]*);");

	private static final Pattern CLASS_PATTERN = Pattern.compile("class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s+");

	private static final String CLASS_END_FLAG = "}";

	@Getter
	protected ClassProxy factory = new ProxyProvider();
	protected NativeCompiler compiler = null;

	protected ClassLoader overrideThreadContextClassLoader(ClassLoader loader) {
		Thread currentThread = Thread.currentThread();
		ClassLoader threadContextClassLoader = currentThread.getContextClassLoader();
		if (loader != null && !loader.equals(threadContextClassLoader)) {
			currentThread.setContextClassLoader(loader);
			return threadContextClassLoader;
		} else {
			return null;
		}
	}

	protected ClassLoader getClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		if (cl == null) {
			cl = AbstractAccessAdaptive.class.getClassLoader();
			if (cl == null) {
				try {
					cl = ClassLoader.getSystemClassLoader();
				} catch (Exception ex) {
					log.error(ex.getMessage(), ex);
				}
			}
		}
		return cl;
	}

	@Override
	public Class<?> compile(String sourceCode, ClassLoader classLoader) {
		String code = sourceCode.trim();
		Matcher matcher = PACKAGE_PATTERN.matcher(code);
		String pkg;
		if (matcher.find()) {
			pkg = matcher.group(1);
		} else {
			pkg = "";
		}
		matcher = CLASS_PATTERN.matcher(code);
		String cls;
		if (matcher.find()) {
			cls = matcher.group(1);
		} else {
			throw new IllegalArgumentException("no such class name in " + code);
		}
		String className = pkg != null && pkg.length() > 0 ? pkg + "." + cls : cls;
		try {
			return Class.forName(className, true, (classLoader != null ? classLoader : getClassLoader()));
		} catch (ClassNotFoundException e) {
			if (!code.endsWith(CLASS_END_FLAG)) {
				throw new IllegalStateException("the java code not ends with \"}\", code: \n" + code + "\n");
			}
			try {
				return doCompile(className, code);
			} catch (Exception t) {
				log.error(t.getMessage(), t);
				throw new IllegalStateException("failed to compile class, cause: " + t.getMessage() + ", class: " + className + ", code: \n" + code, t);
			} finally {
				overrideThreadContextClassLoader(compiler.getClassLoader());
				compiler.close();
			}
		}
	}

	protected abstract Class<?> doCompile(String clsName, String javaSource) throws ClassNotFoundException, IOException;
}
