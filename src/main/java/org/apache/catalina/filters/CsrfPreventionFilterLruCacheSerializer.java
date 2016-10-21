package org.apache.catalina.filters;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by oyj
 * on 16-10-20.
 */
public class CsrfPreventionFilterLruCacheSerializer extends Serializer<CsrfPreventionFilter.LruCache> {
  public static Class clazz = CsrfPreventionFilter.LruCache.class;

  private static final Field SOURCE_LRUCACHE_FIELD;

  static {
    try {
      SOURCE_LRUCACHE_FIELD = Class.forName("org.apache.catalina.filters.CsrfPreventionFilter$LruCache" )
          .getDeclaredField( "cache" );
      SOURCE_LRUCACHE_FIELD.setAccessible( true );

    } catch ( final Exception e ) {
      throw new RuntimeException( "Could not access source cache" +
          " field in org.apache.catalina.filters.CsrfPreventionFilter$LruCache.", e );
    }
  }

  @Override
  public void write(Kryo kryo, Output output, CsrfPreventionFilter.LruCache object) {
    try {
      Map map=(Map)SOURCE_LRUCACHE_FIELD.get(object);
      kryo.writeObject( output, map.keySet());
    } catch ( final RuntimeException e ) {
      throw e;
    } catch ( final Exception e ) {
      throw new RuntimeException( e );
    }
  }

  @Override
  public CsrfPreventionFilter.LruCache read(Kryo kryo, Input input, Class<CsrfPreventionFilter.LruCache> type) {
    final Set keySet = (Set)kryo.readObject( input , HashSet.class);
    CsrfPreventionFilter.LruCache lruCache=new CsrfPreventionFilter.LruCache(keySet.size());
    for (Object o : keySet) {
      lruCache.add(o);
    }
    return lruCache;
  }
}
