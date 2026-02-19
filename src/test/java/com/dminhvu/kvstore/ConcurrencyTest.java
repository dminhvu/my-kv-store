package com.dminhvu.kvstore;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

public class ConcurrencyTest {
  @Test
  void testConcurrentWrites() throws InterruptedException {
    KVStore store = new InMemoryKVStore();
    int numThreads = 100;
    CountDownLatch latch = new CountDownLatch(numThreads);
    ExecutorService pool = Executors.newFixedThreadPool(numThreads);

    for (int i = 0; i < numThreads; i++) {
      final int idx = i;
      pool.submit(() -> {
        try {
          store.set("key" + idx, "value" + idx);
        } finally {
          latch.countDown();
        }
      });

    }

    latch.await(5, TimeUnit.SECONDS);
    pool.shutdown();

    assertEquals(numThreads, store.size(), "All writes should succeed");
  }

  @Test
  void testAtomicIncrement() throws InterruptedException {
    KVStore store = new InMemoryKVStore();
    int numThreads = 50;
    int incrementsPerThread = 100;
    long expected = (long) numThreads * incrementsPerThread;
    CountDownLatch latch = new CountDownLatch(numThreads);
    ExecutorService pool = Executors.newFixedThreadPool(numThreads);
    for (int i = 0; i < numThreads; i++) {
      pool.submit(() -> {
        try {
          for (int j = 0; j < incrementsPerThread; j++) {
            store.increment("counter", 1L);
          }
        } finally {
          latch.countDown();
        }
      });
    }
    latch.await(10, TimeUnit.SECONDS);
    pool.shutdown();
    long actual = Long.parseLong(store.get("counter").orElse("0"));
    assertEquals(expected, actual, "INCR must be perfectly atomic");
  }
}
