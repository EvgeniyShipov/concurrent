package ru.sbt.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FixedThreadPool implements ThreadPool {

    private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
    private final Lock lock = new ReentrantLock();
    private final int threadCount;

    public FixedThreadPool(int threadCount) {
        this.threadCount = threadCount;
    }

    @Override
    public void start() {
        for (int i = 0; i < threadCount; i++) {
            new Worker().start();
        }
    }

    @Override
    public void execute(Runnable runnable) {
        lock.lock();
        try {
            tasks.add(runnable);
        } finally {
            lock.unlock();
        }
    }

    public class Worker extends Thread {
        @Override
        public void run() {
            while (true) {
                lock.lock();
                try {
                    Runnable poll = tasks.poll();
                    poll.run();
                } finally {
                    lock.unlock();
                }
            }
        }
    }
}

