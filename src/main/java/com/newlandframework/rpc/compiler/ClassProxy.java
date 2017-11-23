package com.newlandframework.rpc.compiler;

import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

@Deprecated
public class ClassProxy {
	public <T> Class<T> createDynamicSubclass(Class<T> superclass) {
		Enhancer e = new Enhancer() {
			@Override
			protected void filterConstructors(Class sc, List constructors) {
				// FIXME: 2017/3/30 by tangjie
				// maybe change javassist support
			}
		};

		if (superclass.isInterface()) {
			e.setInterfaces(new Class[] { superclass });
		} else {
			e.setSuperclass(superclass);
		}

		e.setCallbackType(NoOp.class);
		Class<T> proxyClass = e.createClass();
		return proxyClass;
	}
}
