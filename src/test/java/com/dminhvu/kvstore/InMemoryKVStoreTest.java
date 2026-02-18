package com.dminhvu.kvstore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryKVStoreTest {
  private KVStore store;

  @BeforeEach
  void setUp() {
    store = new InMemoryKVStore();
  }

  @Test
  void testSetAndGet() {
    store.set("k1", "v1");
    Optional<String> result = store.get("k1");
    assertTrue(result.isPresent());
    assertEquals("v1", result.get());
  }

  @Test
  void testGetMissingKey() {
    Optional<String> result = store.get("missing");
    assertFalse(result.isPresent());
  }

  @Test
  void testOverwrite() {
    store.set("k1", "v1");
    store.set("k1", "v2");
    assertEquals("v2", store.get("k1").get());
  }

  @Test
  void testDelete() {
    store.set("k1", "v1");
    assertTrue(store.delete("k1"));
    assertFalse(store.exists("k1"));
  }

  @Test
  void testDeleteNonexistent() {
    assertFalse(store.delete("ghost"));
  }

  @Test
  void testSize() {
    assertEquals(0, store.size());
    store.set("a", "1");
    store.set("b", "2");
    assertEquals(2, store.size());
    store.delete("a");
    assertEquals(1, store.size());
  }

  @Test
  void testNullKeyThrows() {
    assertThrows(IllegalArgumentException.class, () -> store.set(null, "v"));
    // assertThrows(IllegalArgumentException.class, () -> store.set("k", null));
  }
}
