# my-kv-store

This toy project is a small in-memory key-value store in Java, like a mini Redis. The server speaks a small part of the Redis protocol (RESP), so it can be used with `redis-cli`.

I learned Java on Exercism, and I built this project to apply it to something bigger than an exercise.

## Features

- **Commands:** `SET`, `GET`, `DEL`, `EXISTS`, `INCR`, `INCRBY`, `DBSIZE`, `PING` and `QUIT`.
- **Storage:** the data is kept in a `ConcurrentHashMap`, so many clients can read and write at the same time.
- **Atomic increments:** `INCR` and `INCRBY` use `ConcurrentHashMap.compute`, so the read, the addition and the write happen as one step. Two clients that increase the same key at the same time don't lose an update.
- **Concurrent clients:** each client connection is handled by a fixed thread pool of 10 threads.

## How to run

You need Java 23 or newer and Maven.

```sh
mvn compile
java -cp target/classes com.dminhvu.kvstore.App
```

The server starts on port 6380. In another terminal:

```
$ redis-cli -p 6380
127.0.0.1:6380> PING
PONG
127.0.0.1:6380> SET name minh
OK
127.0.0.1:6380> GET name
"minh"
127.0.0.1:6380> INCR visits
(integer) 1
127.0.0.1:6380> INCRBY visits 10
(integer) 11
127.0.0.1:6380> DEL name
(integer) 1
```

## Tests

```sh
mvn test
```

There are unit tests for the store, and two concurrency tests: 100 threads writing different keys at the same time, and 50 threads each calling `INCR` 100 times on the same key (the result must be exactly 5,000).

`scripts/test_concurrency.sh` does the same check through the server: it starts 20 `redis-cli` workers that each send 10 `INCR` commands, and then prints the expected and the actual value. Start the server first.

## Limitations

This is a learning project, so some parts are kept simple:

- The RESP parser is minimal. It reads each value as one line, so a value that contains a line break is not supported.
- Only 10 clients can be connected at the same time. The 11th client waits until another one disconnects.
- The data is only in memory, so it is lost when the server stops.
- `INCR` on a value that is not a number is not handled yet, and it closes the connection.
