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
