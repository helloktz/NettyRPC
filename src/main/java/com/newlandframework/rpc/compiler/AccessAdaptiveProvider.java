package com.newlandframework.rpc.compiler;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.google.common.io.Files;
import com.newlandframework.rpc.compiler.intercept.SimpleMethodInterceptor;
import com.newlandframework.rpc.core.ReflectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccessAdaptiveProvider extends AbstractAccessAdaptive implements AccessAdaptive {

	@Override
	protected Class<?> doCompile(String clsName, String javaSource) throws ClassNotFoundException, IOException {
		File tempFileLocation = Files.createTempDir();
		compiler = new NativeCompiler(tempFileLocation);
		Class<?> type = compiler.compile(clsName, javaSource);
		tempFileLocation.deleteOnExit();
		return type;
	}

	@Override
	public Object invoke(String javaSource, String method, Object[] args) {
		if (StringUtils.isNoneEmpty(javaSource, method)) {
			try {
				Class<?> type = compile(javaSource, Thread.currentThread().getContextClassLoader());
				Object object = ReflectionUtils.newInstance(type);
				Thread.currentThread().getContextClassLoader().loadClass(type.getName());
				Object proxy = getFactory().createProxy(object, new SimpleMethodInterceptor(), type);
				return MethodUtils.invokeMethod(proxy, method, args);
			} catch (ReflectiveOperationException e) {
				log.error(e.getMessage(), e);
			}
		}
		return null;
	}
}
