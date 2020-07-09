/*
 * Copyright 2015 Corey Baswell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
