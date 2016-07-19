package com.orangefunction.tomcat.redissessions;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Kryo.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.orangefunction.tomcat.redissessions.util.MurmurHash3;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.IOException;

/**
 * Created by oyj
 * on 16-7-15.
 */
public class KryoSerializer implements Serializer{
  private static final int INIT_BUFFER_LENGTH = 4096;
  private static final int MAX_BUFFER_LENGTH = 1024 * 200;
  private ClassLoader loader;

  private KryoFactory factory = new KryoFactory() {
    public Kryo create () {
      Kryo kryo = new Kryo();
      kryo.setClassLoader(loader);
      kryo.register(RedisSession.class, new RedisSessionSerializer());
      kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
      // configure kryo instance, customize settings
      return kryo;
    }
  };
  private KryoPool pool = new KryoPool.Builder(factory).softReferences().build();

  @Override
  public void setClassLoader(ClassLoader loader) {
    this.loader=loader;
  }

  @Override
  public int attributesHashFrom(RedisSession session) throws IOException {
    Kryo kryo = pool.borrow();

    Output out = new Output(new byte[INIT_BUFFER_LENGTH],MAX_BUFFER_LENGTH);
    out.clear();
    kryo.writeObject(out,session.getAttrbutes());

    pool.release(kryo);
    return MurmurHash3.hash(out.getBuffer());
  }

  @Override
  public byte[] serializeFrom(RedisSession session) throws IOException {
    Kryo kryo = pool.borrow();

    Output out = new Output(new byte[INIT_BUFFER_LENGTH],MAX_BUFFER_LENGTH);
    out.clear();
    kryo.writeObject(out,session);

    pool.release(kryo);
    return out.toBytes();
  }

  @Override
  public RedisSession deserializeInto(byte[] data, RedisSession session) throws IOException, ClassNotFoundException {
    Kryo kryo = pool.borrow();

    Input input=new Input(data);
    RedisSession redisSession = kryo.readObject(input, RedisSession.class);
    redisSession.setManager(session.getManager());

    pool.release(kryo);
    return redisSession;
  }
}
