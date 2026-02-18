package com.dminhvu.kvstore;

import java.util.Optional;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        KVStore store = new InMemoryKVStore();

        store.set("name", "Minton");
        store.set("university", "Uni Passau");
        store.set("language", "Java");

        Optional<String> name = store.get("name");
        name.ifPresent(v -> System.out.println("name=" + v));

        Optional<String> missing = store.get("missing");
        System.out.println("missing key present? " + missing.isPresent());

        boolean deleted = store.delete("language");
        System.out.println("delete 'language'? " + deleted);
        System.out.println("'language' still exists? " + store.exists("language"));

        System.out.println("store size: " + store.size());
    }
}
