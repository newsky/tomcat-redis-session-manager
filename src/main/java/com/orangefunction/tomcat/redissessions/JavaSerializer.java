package com.orangefunction.tomcat.redissessions;

import com.orangefunction.tomcat.redissessions.util.MurmurHash3;
import org.apache.catalina.util.CustomObjectInputStream;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;

public class JavaSerializer implements Serializer {
  private ClassLoader loader;
  private final Log log = LogFactory.getLog(JavaSerializer.class);

  @Override
  public void setClassLoader(ClassLoader loader) {
    this.loader = loader;
  }

  public int attributesHashFrom(RedisSession session) throws IOException {
    HashMap<String,Object> attributes = new HashMap<String,Object>();
    for (Enumeration<String> enumerator = session.getAttributeNames(); enumerator.hasMoreElements();) {
      String key = enumerator.nextElement();
      attributes.put(key, session.getAttribute(key));
    }

    byte[] serialized = null;

    try (
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(bos));
    ) {
      oos.writeUnshared(attributes);
      oos.flush();
      serialized = bos.toByteArray();
    }

    return MurmurHash3.hash(serialized);
  }

  @Override
  public byte[] serializeFrom(RedisSession session) throws IOException {
    byte[] serialized = null;

    try (
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(bos));
    ) {
//      oos.writeObject(metadata);
      session.writeObjectData(oos);
      oos.flush();
      serialized = bos.toByteArray();
    }

    return serialized;
  }

  @Override
  public RedisSession deserializeInto(byte[] data, RedisSession session) throws IOException, ClassNotFoundException {
    try(
        BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(data));
        ObjectInputStream ois = new CustomObjectInputStream(bis, loader);
    ) {
//      SessionSerializationMetadata serializedMetadata = (SessionSerializationMetadata)ois.readObject();
//      metadata.copyFieldsFrom(serializedMetadata);
      session.readObjectData(ois);
    }
    return session;
  }
}
