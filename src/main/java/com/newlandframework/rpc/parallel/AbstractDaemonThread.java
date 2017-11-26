package com.newlandframework.rpc.parallel;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractDaemonThread implements Runnable {
	protected final Thread thread;
	private static final long JOIN_TIME = 90 * 1000L;
	protected volatile boolean hasNotified = false;
	@Getter
	protected volatile boolean stoped = false;

	public AbstractDaemonThread() {
		this.thread = new Thread(this, this.getDeamonThreadName());
	}

	public abstract String getDeamonThreadName();

	public void start() {
		this.thread.start();
	}

	public void shutdown() {
		this.shutdown(false);
	}

	public void stop() {
		this.stop(false);
	}

	public void makeStop() {
		this.stoped = true;
	}

	public void stop(final boolean interrupt) {
		this.stoped = true;
		synchronized (this) {
			if (!this.hasNotified) {
				this.hasNotified = true;
				this.notifyAll();
			}
		}

		if (interrupt) {
			this.thread.interrupt();
		}
	}

	public void shutdown(final boolean interrupt) {
		this.stoped = true;
		synchronized (this) {
			if (!this.hasNotified) {
				this.hasNotified = true;
				this.notifyAll();
			}
		}

		try {
			if (interrupt) {
				this.thread.interrupt();
			}

			if (!this.thread.isDaemon()) {
				this.thread.join(this.getJoinTime());
			}
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
		}
	}

	public void wakeup() {
		synchronized (this) {
			if (!this.hasNotified) {
				this.hasNotified = true;
				this.notifyAll();
			}
		}
	}

	protected void waitForRunning(long interval) {
		synchronized (this) {
			if (this.hasNotified) {
				this.hasNotified = false;
				this.onWaitEnd();
				return;
			}

			try {
				this.wait(interval);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
				Thread.currentThread().interrupt();
			} finally {
				this.hasNotified = false;
				this.onWaitEnd();
			}
		}
	}

	protected void onWaitEnd() {
	}

	public long getJoinTime() {
		return JOIN_TIME;
	}
}
