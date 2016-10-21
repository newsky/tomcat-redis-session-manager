package org.apache.catalina.filters;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.orangefunction.tomcat.redissessions.KryoSerializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import static com.orangefunction.tomcat.redissessions.KryoSerializer.INIT_BUFFER_LENGTH;
import static com.orangefunction.tomcat.redissessions.KryoSerializer.MAX_BUFFER_LENGTH;
import static org.junit.Assert.*;

/**
 * Created by oyj
 * on 16-10-20.
 */
public class CsrfPreventionFilterLruCacheSerializerTest {
  private KryoSerializer kryoSerializer;

  @Before
  public void setUp() throws Exception {
    kryoSerializer=new KryoSerializer();
    kryoSerializer.setClassLoader(this.getClass().getClassLoader());
  }

  @After
  public void tearDown() throws Exception {

  }

  @Test
  public void write() throws Exception {
    Kryo kryo = kryoSerializer.borrow();
    CsrfPreventionFilter.LruCache lruCache=new CsrfPreventionFilter.LruCache<>(10);
    lruCache.add("ddd");
    lruCache.add("ccc");
    lruCache.add("eee");

    Output out = new Output(new byte[INIT_BUFFER_LENGTH], MAX_BUFFER_LENGTH);
    out.clear();
    kryo.writeObject(out, lruCache);

    kryoSerializer.release(kryo);

    byte[] data = out.toBytes();

    BASE64Encoder encoder=new BASE64Encoder();
    System.out.println("data:"+ encoder.encode(data));
  }

  @Test
  public void read() throws Exception {
    String string = "AQEDAwFkZOQDAWNj4wMBZWXl";

    BASE64Decoder decoder=new BASE64Decoder();
    byte[] data=decoder.decodeBuffer(string);

    Kryo kryo = kryoSerializer.borrow();
    Input input = new Input(data);
    kryo.readObject(input,CsrfPreventionFilterLruCacheSerializer.clazz);
  }

}