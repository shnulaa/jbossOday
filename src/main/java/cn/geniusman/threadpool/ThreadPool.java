package cn.geniusman.threadpool;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.geniusman.constant.Constant;

public final class ThreadPool {

	/** the log instance **/
	private static final Logger log = LoggerFactory.getLogger(ThreadPool.class);

	/** the single instance **/
	private static ThreadPool instance;
	/** the service of thread pool **/
	private ExecutorService service;

	/** the thread name index **/
	private final AtomicInteger threadNameIndex = new AtomicInteger(1);

	/** the factory of thread. set the thread name and is Daemon **/
	private final ThreadFactory threadFactory = new ThreadFactory() {
		public Thread newThread(Runnable r) {
			final Thread t = new Thread(r);
			t.setName("Search-Thread" + threadNameIndex.getAndIncrement());
			t.setDaemon(true);
			return t;
		}
	};

	/**
	 * private constructor <br>
	 * initialize the thread pool service
	 */
	private ThreadPool() {
		// default thread number is 10
		// if increase this number, the system will crashed due to IO
		// too many process will be executed
		service = Executors.newFixedThreadPool(Constant.THREAD_NUMBER,
				threadFactory);
	}

	/**
	 * waitForTerminat
	 */
	public void waitForTerminat() {
		try {
			service.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			log.warn("the current thread is Interrupted..");
		}
	}

	/**
	 * create the thread pool with threadNumber
	 * 
	 * @param threadNumber
	 */
	private ThreadPool(int threadNumber) {
		service = Executors.newFixedThreadPool(threadNumber, threadFactory);
	}

	/**
	 * get the singleton instance
	 * 
	 * @return the singleton instance
	 */
	public static synchronized ThreadPool getInstance() {
		if (instance == null) {
			instance = new ThreadPool();
		}
		return instance;
	}

	/**
	 * get the singleton instance
	 * 
	 * @return the singleton instance
	 */
	public static synchronized ThreadPool getInstance(int threadNumber) {
		if (instance == null) {
			instance = new ThreadPool(threadNumber);
		}
		return instance;
	}

	/**
	 * submit the task asynchronously
	 * 
	 * @param r
	 *            the Runnable instance (worker)
	 */
	public void submitTask(final Runnable r) {
		if (service != null) {
			service.submit(r);
		}
	}


	/**
	 * submit the task asynchronously and return the Future <br>
	 * result
	 * 
	 * @param c
	 *            the Callable instance (worker)
	 * @return the Future instance
	 */
	public <T> Future<T> submitTask(final Callable<T> c) {
		if (service != null) {
			return service.submit(c);
		}
		return null;
	}

	/**
	 * shutdown the service
	 */
	public void shutdown() {
		if (service != null) {
			service.shutdown();
		}
	}

	/**
	 * force shutdown the service
	 */
	public void forceShutdown() {
		if (service != null) {
			service.shutdownNow();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

}
