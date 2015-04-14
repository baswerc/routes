package org.baswell.routes;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class RequestPathTest
{
  @Test
  public void testEquals()
  {
    RequestPath path = new RequestPath("/this/is/a/test");

    assertTrue(path.equals(Arrays.asList("this", "is", "a", "test")));
    assertTrue(path.equals("this/is/a/test"));
    assertTrue(path.equals("/this/is/a/test"));
    assertTrue(path.equals("/this/is/a/test/"));
    assertTrue(path.equals("this/is/a/test/"));

    assertFalse(path.equals("this/is/a/"));
  }

  @Test
  public void testStartsWith()
  {
    RequestPath path = new RequestPath("/this/is/a/test");

    assertTrue(path.startsWith("this/is/a/test"));
    assertTrue(path.startsWith("/this/is/a/"));
    assertTrue(path.startsWith("/this/is/a"));
    assertTrue(path.startsWith("/this"));

    assertFalse(path.startsWith("/this/i"));
  }

  @Test
  public void testPop()
  {
    RequestPath path = new RequestPath("/this/is/a/test").pop();
    assertTrue(path.startsWith("is/a/test"));

    path = new RequestPath("/this").pop();
    assertTrue(path.startsWith(""));

  }
}
