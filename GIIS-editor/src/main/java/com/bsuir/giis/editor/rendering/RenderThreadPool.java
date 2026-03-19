package com.bsuir.giis.editor.rendering;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RenderThreadPool {

    public static final int THREAD_COUNT =
        Math.max(1, Runtime.getRuntime().availableProcessors() - 1);

    private static volatile ExecutorService pool;

    public static ExecutorService getPool() {
        if (pool == null || pool.isShutdown()) {
            synchronized (RenderThreadPool.class) {
                if (pool == null || pool.isShutdown()) {
                    pool = Executors.newFixedThreadPool(THREAD_COUNT);
                }
            }
        }
        return pool;
    }

    public static void shutdown() {
        if (pool != null) {
            pool.shutdown();
            try {
                if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                    pool.shutdownNow();
                }
            } catch (InterruptedException e) {
                pool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
