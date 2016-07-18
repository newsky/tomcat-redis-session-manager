package com.orangefunction.tomcat.redissessions.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by oyj
 * on 16-7-15.
 */
public class MurmurHash3Test {
  @Before
  public void setUp() throws Exception {

  }

  @After
  public void tearDown() throws Exception {

  }

  @Test
  public void hash() throws Exception {
    String tmp1="ssajlsjf";
    int hash=MurmurHash3.hash(tmp1.getBytes(),0,tmp1.getBytes().length,111);
    System.out.println(hash);
  }

}