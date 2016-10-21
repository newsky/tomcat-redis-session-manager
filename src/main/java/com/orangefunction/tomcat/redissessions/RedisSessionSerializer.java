package com.orangefunction.tomcat.redissessions;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.apache.catalina.SessionListener;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by oyj
 * on 16-7-15.
 */
public class RedisSessionSerializer extends Serializer<RedisSession> {
  @Override
  public void write(Kryo kryo, Output output, RedisSession redisSession) {
    // Write the scalar instance variables (except Manager)
    output.writeInt(redisSession.getSessionAttributesHash());
    output.writeLong(redisSession.getCreationTime());
    output.writeLong(redisSession.getLastAccessedTime());
    output.writeInt(redisSession.getMaxInactiveInterval());
    output.writeBoolean(redisSession.isNew());
    output.writeBoolean(redisSession.isValid());
    output.writeLong(redisSession.getThisAccessedTime());
    output.writeString(redisSession.getId());

    // Accumulate the names of serializable and non-serializable attributes
    Enumeration<String> keys = redisSession.getAttributeNames();
    ArrayList<String> saveNames = new ArrayList<>();
    ArrayList<Object> saveValues = new ArrayList<>();
    for (; keys.hasMoreElements();) {
      String key = keys.nextElement();
      Object value = redisSession.getAttribute(key);
      if (value == null)
        continue;
      else if (!redisSession.exclude(key)) {
        saveNames.add(key);
        saveValues.add(value);
      } else {
        redisSession.removeAttributeInternal(key, true);
      }
    }

    // Serialize the attribute count and the Serializable attributes
    int n = saveNames.size();
    output.writeInt(n);
    for (int i = 0; i < n; i++) {
      output.writeString(saveNames.get(i));
      kryo.writeClassAndObject(output, saveValues.get(i));
    }
  }

  @Override
  public RedisSession read(Kryo kryo, Input input, Class<RedisSession> type) {
    // Deserialize the scalar instance variables (except Manager)
    RedisSession session = new RedisSession();
    session.setAuthType(null);
    session.setSessionAttributesHash(input.readInt());
    session.setCreationTime(input.readLong());
    session.setLastAccessedTime(input.readLong());
    session.setMaxInactiveInterval(input.readInt());
    session.setIsNew(input.readBoolean());
    session.setIsValid(input.readBoolean());
    session.setThisAccessedTime(input.readLong());
    session.setPrincipal(null);
    // setId((String) stream.readObject());
    session.setId(input.readString());

    // Deserialize the attribute count and attribute values
    if (session.getAttrbutes() == null) {
      session.setAttrbutes(new ConcurrentHashMap<String, Object>());
    }
    int n = input.readInt();
    boolean isValidSave = session.isValid();
    session.setIsValid(true);
    for (int i = 0; i < n; i++) {
      String name = input.readString();
      Object value = kryo.readClassAndObject(input);
      session.getAttrbutes().put(name, value);
    }
    session.setIsValid(isValidSave);

    if (session.getListeners() == null) {
      ArrayList listeners = new ArrayList<SessionListener>();
      session.setListeners(listeners);
    }

    if (session.getNotes() == null) {
      session.setNotes(new Hashtable<String, Object>());
    }
    return session;
  }
}
