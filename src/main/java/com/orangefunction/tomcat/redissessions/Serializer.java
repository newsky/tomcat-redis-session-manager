package com.orangefunction.tomcat.redissessions;

import java.io.IOException;

public interface Serializer {
  void setClassLoader(ClassLoader loader);

  int attributesHashFrom(RedisSession session) throws IOException;
  byte[] serializeFrom(RedisSession session) throws IOException;
  RedisSession deserializeInto(byte[] data, RedisSession session) throws IOException, ClassNotFoundException;
}
