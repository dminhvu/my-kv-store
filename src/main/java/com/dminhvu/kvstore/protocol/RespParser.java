package com.dminhvu.kvstore.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RespParser {
  public static List<String> parse(BufferedReader reader) throws IOException {
    String line = reader.readLine();
    if (line == null)
      return null;

    int numElements = Integer.parseInt(line.substring(1));
    List<String> parts = new ArrayList<>();

    for (int i = 0; i < numElements; i++) {
      String lengthLine = reader.readLine();
      if (lengthLine == null)
        return null;

      String value = reader.readLine();
      if (value == null)
        return null;
      parts.add(value);
    }

    return parts;
  }
}
