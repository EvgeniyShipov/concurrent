package ru.sbt.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ScalableThreadPool implements ThreadPool {

    private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
    private final AtomicInteger currentWorkedThread = new AtomicInteger(0);
    private final Lock lock = new ReentrantLock();
    private final int minThread;
    private final int maxThread;

    public ScalableThreadPool(int minThread, int maxThread) {
        this.minThread = minThread;
        this.maxThread = maxThread;
    }

    @Override
    public void start() {
        for (int i = 0; i < minThread; i++) {
            new ScalableThreadPool.Worker().start();
        }
    }

    public void execute(Runnable runnable) {
        lock.lock();
        tasks.add(runnable);
        if (currentWorkedThread.get() >= minThread && currentWorkedThread.get() < maxThread) {
            Thread thread = new Thread(() -> {
                try {
                    currentWorkedThread.incrementAndGet();
                    while (!tasks.isEmpty()) {
                        Runnable poll = tasks.poll();
                        poll.run();
                    }
                } finally {
                    currentWorkedThread.decrementAndGet();
                    lock.unlock();
                }
            });
            thread.start();
        }
    }

    public class Worker extends Thread {
        @Override
        public void run() {
            while (true) {
                lock.lock();
                try {
                    currentWorkedThread.incrementAndGet();
                    Runnable poll = tasks.poll();
                    poll.run();
                } finally {
                    currentWorkedThread.decrementAndGet();
                    lock.unlock();
                }
            }
        }
    }
}

