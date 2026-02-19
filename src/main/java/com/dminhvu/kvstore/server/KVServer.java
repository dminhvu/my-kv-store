package com.dminhvu.kvstore.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.dminhvu.kvstore.KVStore;

public class KVServer {
  private final int port;
  private final KVStore store;

  public KVServer(int port, KVStore store) {
    this.port = port;
    this.store = store;
  }

  public void start() throws IOException {
    try (ServerSocket serverSocket = new ServerSocket(port)) {
      System.out.println("KV Store server started on port " + port);
      System.out.println("Connect with: redis-cli -p " + port);

      while (true) {
        Socket clientSocket = serverSocket.accept();

        Thread clientThread = new Thread(new ClientHandler(clientSocket, store));
        clientThread.start();
      }
    }
  }
}
