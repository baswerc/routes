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
