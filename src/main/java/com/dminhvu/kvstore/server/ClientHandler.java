package com.dminhvu.kvstore.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Optional;

import com.dminhvu.kvstore.KVStore;
import com.dminhvu.kvstore.protocol.RespParser;

public class ClientHandler implements Runnable {
  private final Socket clientSocket;
  private final KVStore store;

  public ClientHandler(Socket clientSocket, KVStore store) {
    this.clientSocket = clientSocket;
    this.store = store;
  }

  @Override
  public void run() {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

      System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());

      List<String> command;
      while ((command = RespParser.parse(reader)) != null) {
        String response = handleCommand(command);
        writer.print(response);
        writer.flush();
      }
    } catch (IOException e) {
      System.out.println("Client error: " + e.getMessage());
    } finally {
      try {
        clientSocket.close();
      } catch (IOException ignored) {
      }
      System.out.println("Client disconnected: " + clientSocket.getRemoteSocketAddress());
    }
  }

  private String handleCommand(List<String> parts) {
    if (parts.isEmpty())
      return "-ERR empty command\r\n";

    String cmd = parts.get(0).toUpperCase();

    return switch (cmd) {
      case "SET" -> {
        if (parts.size() < 3) {

          yield "-ERR wrong number of arguments for SET\r\n";
        }
        store.set(parts.get(1), parts.get(2));
        yield "+OK\r\n";
      }
      case "GET" -> {
        if (parts.size() < 2) {
          yield "-ERR wrong number of arguments for GET\r\n";
        }
        Optional<String> val = store.get(parts.get(1));

        if (val.isPresent()) {
          String v = val.get();
          yield "$" + v.length() + "\r\n" + v + "\r\n";
        } else {
          yield "$-1\r\n";
        }
      }
      case "DEL" -> {
        if (parts.size() < 2) {
          yield "-ERR wrong number of arguments for DEL\r\n";
        }

        boolean deleted = store.delete(parts.get(1));
        yield deleted ? ":1\r\n" : ":0\r\n";
      }
      case "EXISTS" -> {
        if (parts.size() < 2) {
          yield "-ERR wrong number of arguments for EXISTS\r\n";
        }

        boolean exists = store.exists(parts.get(1));
        yield exists ? ":1\r\n" : ":0\r\n";
      }
      case "INCR" -> {
        if (parts.size() < 2) {
          yield "-ERR wrong number of arguments for INCR\r\n";

        }

        long val = store.increment(parts.get(1), 1);
        yield ":" + String.valueOf(val) + "\r\n";
      }
      case "INCRBY" -> {
        if (parts.size() < 3) {
          yield "-ERR wrong number of arguments for INCRBY\r\n";
        }
        try {
          long delta = Long.parseLong(parts.get(2));
          long val = store.increment(parts.get(1), delta);
          yield ":" + String.valueOf(val) + "\r\n";
        } catch (NumberFormatException e) {
          yield "-ERR invalid delta value\r\n";
        }
      }
      case "DBSIZE" -> ":" + store.size() + "\r\n";
      case "PING" -> "+PONG\r\n";
      case "QUIT" -> "+OK\r\n";
      default -> "-ERR unknown command '" + cmd + "'\r\n";
    };
  }
}
