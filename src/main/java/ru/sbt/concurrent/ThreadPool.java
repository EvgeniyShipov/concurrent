package ru.sbt.concurrent;


public interface ThreadPool {
    void start();

    void execute(Runnable runnable);
}
