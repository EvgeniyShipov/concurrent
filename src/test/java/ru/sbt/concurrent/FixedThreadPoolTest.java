package ru.sbt.concurrent;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class FixedThreadPoolTest {
    @Test
    public void test() throws Exception {
        List<Integer> list = new ArrayList<>();
        ThreadPool threadPool = new FixedThreadPool(3);
        threadPool.start();
        for (int i = 0; i < 10; i++) {
            Runnable runnable = () -> {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    list.add(1);
                }
            };
            threadPool.execute(runnable);
        }
        Thread.sleep(2000);
        assertEquals(10, list.size());
    }
}