package com.orangefunction.tomcat.redissessions;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sun.misc.BASE64Decoder;

import static org.junit.Assert.*;

/**
 * Created by oyj
 * on 16-10-20.
 */
public class KryoSerializerTest {
  KryoSerializer kryoSerializer;
  @Before
  public void setUp() throws Exception {
    kryoSerializer=new KryoSerializer();
    kryoSerializer.setClassLoader(this.getClass().getClassLoader());
  }

  @After
  public void tearDown() throws Exception {

  }

  @Test
  public void deserializeInto() throws Exception {
//    String string = "ARoegFQAAAFX4VCtWQAAAVfhUK1ZAAAHCAEBAAABV+FQrbEwQTA3MTdEQTk5MUJGQjUyMDA3RkU4" +
//        "MjBDN0E0OTYysAAAAAFvcmcuYXBhY2hlLmNhdGFsaW5hLmZpbHRlcnMuQ1NSRl9OT05DxQEAb3Jn" +
//        "LmFwYWNoZS5jYXRhbGluYS5maWx0ZXJzLkNzcmZQcmV2ZW50aW9uRmlsdGVyJExydUNhY2jlAQEB" +
//        "b3JnLmFwYWNoZS5jYXRhbGluYS5maWx0ZXJzLkNzcmZQcmV2ZW50aW9uRmlsdGVyJExydUNhY2hl";

//    BASE64Decoder decoder=new BASE64Decoder();
//    byte[] data=decoder.decodeBuffer(string);

//    Kryo kryo = kryoSerializer.borrow();
//    Input input = new Input(data);
//    kryo.readObject(input, RedisSession.class);
//    kryoSerializer.release(kryo);
  }

}