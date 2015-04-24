package org.baswell.routes;

import org.junit.Test;

import static org.baswell.routes.RouteByCamelCaseConvention.*;
import static org.junit.Assert.*;

public class CameCaseRouteByConventionTest
{
  @Test
  public void testCamelCase()
  {
    assertEquals(camelCaseToPath("Test"), "test");
    assertEquals(camelCaseToPath("HelloWorld"), "hello/world");
    assertEquals(camelCaseToPath("ThisIsATest"), "this/is/a/test");
    assertEquals(camelCaseToPath(null), "");
    assertEquals(camelCaseToPath(""), "");
  }
}
