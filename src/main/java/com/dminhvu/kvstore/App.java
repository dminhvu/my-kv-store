package com.dminhvu.kvstore;

import java.io.IOException;

import com.dminhvu.kvstore.server.KVServer;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        int port = 6380;
        KVStore store = new InMemoryKVStore();
        KVServer server = new KVServer(port, store);
        server.start();
    }
}
