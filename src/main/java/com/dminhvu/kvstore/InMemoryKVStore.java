package com.dminhvu.kvstore;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryKVStore implements KVStore {
  private final Map<String, String> store;

  public InMemoryKVStore() {
    store = new ConcurrentHashMap<>();
  }

  @Override
  public void set(String key, String value) {
    if (key == null || value == null) {
      throw new IllegalArgumentException(
          "Key and value must not be null! Received: (key=" + String.valueOf(key) + ";value=" + String.valueOf(value)
              + ")");
    }
    store.put(key, value);
  }

  @Override
  public Optional<String> get(String key) {
    return Optional.ofNullable(store.get(key));
  }

  @Override
  public boolean delete(String key) {
    return store.remove(key) != null;
  }

  @Override
  public boolean exists(String key) {
    return store.containsKey(key);
  }

  @Override
  public int size() {
    return store.size();
  }

  @Override
  public long increment(String key, long delta) {
    long[] result = new long[1];
    store.compute(key, (k, currentValue) -> {
      long current = (currentValue == null) ? 0L : Long.parseLong(currentValue);
      result[0] = current + delta;
      return String.valueOf(result[0]);
    });
    return result[0];
  }

}
