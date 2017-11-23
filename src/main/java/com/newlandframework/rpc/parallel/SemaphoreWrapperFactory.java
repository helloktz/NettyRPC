package com.newlandframework.rpc.parallel;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SemaphoreWrapperFactory extends SemaphoreWrapper {
	private static final SemaphoreWrapperFactory INSTANCE = new SemaphoreWrapperFactory();

	public static SemaphoreWrapperFactory getInstance() {
		return INSTANCE;
	}

	private SemaphoreWrapperFactory() {
		super();
	}

	@Override
	public void acquire() {
		if (semaphore != null) {
			try {
				while (true) {
					boolean result = released.get();
					if (released.compareAndSet(result, true)) {
						semaphore.acquire();
						break;
					}
				}
			} catch (InterruptedException e) {
				log.error(e);
			}
		}
	}

	@Override
	public void release() {
		if (semaphore != null) {
			while (true) {
				boolean result = released.get();
				if (released.compareAndSet(result, false)) {
					semaphore.release();
					break;
				}
			}
		}
	}
}
