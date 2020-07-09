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

import com.google.gson.Gson;
import org.baswell.routes.testroutes.ReturnTypes;
import org.baswell.routes.utils.http.TestHttpServletRequest;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.Assert.*;

public class ReturnTypesTest extends EndToEndTest
{
  @Before
  public void setupRoutingTable()
  {
    buildRoutingTable(ReturnTypes.class);
  }

  @Test
  public void testReturnString() throws IOException, ServletException
  {
    invoke(new TestHttpServletRequest("GET", "/test", "/test/helloworld"), "getHelloWorld");
    String response = new String(servletResponse.outputStream.toByteArray());
    assertEquals(response, "Hello World");
  }

  @Test
  public void testGson() throws IOException, ServletException
  {
    TestHttpServletRequest request = new TestHttpServletRequest("GET", "/", "/gson");
    request.contentType = "application/json";
    invoke(request, "gson");

    Gson gson = new Gson();
    ReturnTypes.GsonResponse gsonResponse = gson.fromJson(servletResponse.getContentAsString(), ReturnTypes.GsonResponse.class);
    assertNotNull(gsonResponse);
    assertEquals(gsonResponse.one, "one");
    assertEquals(gsonResponse.two, 2);
    assertTrue(gsonResponse.three);
  }

}
