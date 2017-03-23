package org.yoon_technology.engine;

import java.util.LinkedList;

public class Concurrency {

	public static class WorkQueue {

		private final PoolWorker[] threads;
		private final LinkedList<Runnable> queue;

		public WorkQueue(int numThreads) {

			queue = new LinkedList<>();
			threads = new PoolWorker[numThreads];
			for (int j = 0; j < numThreads; j++) {
				threads[j] = new PoolWorker();
				threads[j].start();
			}
		}

		public void execute(Runnable r) {

			synchronized(this.queue) {
				queue.addLast(r);
				queue.notify();
			}
		}

		private class PoolWorker extends Thread {

			@Override
			public void run() {
				Runnable r;

				while (true) {
					synchronized(queue) {
						while (queue.isEmpty()) {
							try {
								queue.wait();
							}
							catch (InterruptedException ignored) {}
						}
						r = queue.removeFirst();
					}
					try {
						r.run();
					} catch (RuntimeException e) {}
				}
			}
		}
	}
}
