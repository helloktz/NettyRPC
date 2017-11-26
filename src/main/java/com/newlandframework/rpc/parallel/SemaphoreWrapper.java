package com.newlandframework.rpc.parallel;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SemaphoreWrapper {
	protected final AtomicBoolean released = new AtomicBoolean(false);
	@Getter
	@Setter
	protected Semaphore semaphore;

	public SemaphoreWrapper() {
		semaphore = new Semaphore(1);
	}

	public SemaphoreWrapper(int permits) {
		semaphore = new Semaphore(permits);
	}

	public SemaphoreWrapper(int permits, boolean fair) {
		semaphore = new Semaphore(permits, fair);
	}

	public SemaphoreWrapper(Semaphore semaphore) {
		this.semaphore = semaphore;
	}

	public void release() {
		if (semaphore != null && released.compareAndSet(false, true))
			semaphore.release();
	}

	public void acquire() {
		if (this.semaphore != null) {
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
				Thread.currentThread().interrupt();
			}
		}
	}

	public boolean isRelease() {
		return released.get();
	}
}
