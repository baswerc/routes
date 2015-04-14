package org.baswell.routes;

import org.junit.Test;

import static org.baswell.routes.BaseRouteFromMethodScheme.*;
import static org.junit.Assert.*;

public class BaseRouteFromMethodSchemeTest
{
  @Test
  public void testRemoveHttpMethods()
  {
    assertNull(removeHttpMethods(null));
    assertEquals(removeHttpMethods("Test"), "Test");
    assertEquals(removeHttpMethods("getTest"), "Test");
    assertEquals(removeHttpMethods("getPostTest"), "Test");
    assertEquals(removeHttpMethods("getPostDeleteTest"), "Test");
  }
}
