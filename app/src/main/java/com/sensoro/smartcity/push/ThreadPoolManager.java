package com.sensoro.smartcity.push;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author JL-DDONG
 */
public class ThreadPoolManager {
    private final static ThreadPoolManager instance = new ThreadPoolManager();
    private final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private final int MINIMUM_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private final int KEEP_ALIVE = 5;
    private final BlockingQueue<Runnable> mWorkQueue = new LinkedBlockingQueue<Runnable>(128);
    private final ThreadFactory mThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "myThread #" + mCount.getAndIncrement());
        }
    };
    //    private final ReentrantReadWriteLock mLock = new ReentrantReadWriteLock(true);
    private final ThreadPoolExecutor executor;

    private ThreadPoolManager() {
        final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(MINIMUM_POOL_SIZE,
                MAXIMUM_POOL_SIZE, KEEP_ALIVE,
                TimeUnit.SECONDS, mWorkQueue, mThreadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        executor = threadPoolExecutor;
    }

    public static ThreadPoolManager getInstance() {
        return instance;
    }

    /**
     * 从线程池中执行任务
     */

    public void execute(Runnable task) {
        if (task != null) {
//            mLock.readLock().lock();
//            try {
            executor.execute(task);
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                mLock.readLock().unlock();
//            }

        }
    }

    /**
     * 从线程池中移除任务
     */

    public void remove(Runnable task) {
        if (task != null) {
//            mLock.readLock().lock();
//            try {
            executor.remove(task);
//            } catch (Exception e) {
//            e.printStackTrace();
//            } finally {
//                mLock.readLock().unlock();
//            }
        }
    }

    /**
     * 反序列化时内存Hook这段代码
     *
     * @return
     */
    private Object readReslove() {
        return instance;
    }
}
