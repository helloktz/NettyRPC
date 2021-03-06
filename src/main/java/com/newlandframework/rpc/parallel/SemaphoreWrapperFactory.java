package com.newlandframework.rpc.parallel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
				log.error(e.getMessage(), e);
				Thread.currentThread().interrupt();
			}
		}
	}

	@Override
	public void release() {
		if (semaphore != null)
			while (true) {
				boolean result = released.get();
				if (released.compareAndSet(result, false)) {
					semaphore.release();
					break;
				}
			}
	}
}
