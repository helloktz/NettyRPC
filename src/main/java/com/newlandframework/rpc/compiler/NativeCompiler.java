package com.newlandframework.rpc.compiler;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Locale;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import lombok.Cleanup;
import lombok.Getter;
import lombok.SneakyThrows;

public class NativeCompiler implements Closeable {
	private final File tempFolder;
	@Getter
	private final URLClassLoader classLoader;

	public NativeCompiler(File tempFolder) {
		this.tempFolder = tempFolder;
		this.classLoader = createClassLoader(tempFolder);
	}

	@SneakyThrows
	private URLClassLoader createClassLoader(File tempFolder) {
		URL[] urls = { tempFolder.toURI().toURL() };
		return new URLClassLoader(urls);
	}

	public Class<?> compile(String className, String code) throws IOException, ClassNotFoundException {
		JavaFileObject sourceFile = new StringJavaFileObject(className, code);
		compileClass(sourceFile);
		return classLoader.loadClass(className);
	}

	private void compileClass(JavaFileObject sourceFile) throws IOException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
		@Cleanup
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(collector, Locale.ROOT, null);
		fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(tempFolder));
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, collector, null, null, Arrays.asList(sourceFile));
		task.call();
	}

	@Override
	@SneakyThrows
	public void close() {
		classLoader.close();
	}
}
