package org.baswell.routes;

import org.testng.annotations.Test;

import static org.testng.Assert.*;
import static org.baswell.routes.CamelCaseRouteFromMethodScheme.*;

public class CameCaseRouteFromMethodSchemeTest
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
