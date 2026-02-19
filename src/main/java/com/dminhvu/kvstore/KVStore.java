package com.dminhvu.kvstore;

import java.util.Optional;

public interface KVStore {
  void set(String key, String value);

  Optional<String> get(String key);

  boolean delete(String key);

  boolean exists(String key);

  int size();

  long increment(String key, long delta);
}
