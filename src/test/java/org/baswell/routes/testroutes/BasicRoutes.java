package org.baswell.routes.testroutes;

import org.baswell.routes.*;

import java.net.URL;

import static org.junit.Assert.*;

public class BasicRoutes extends BaseRoutes
{
  public boolean authenticationAllowed;
  
  @BeforeRoute(exceptTags="not_authenticated")
  public boolean requireAuthentication(RequestParameters parameters)
  {
    methodsCalled.add("requireAuthentication");
    return parameters.getBoolean("authenticationAllowed", false);
  }
  
  @BeforeRoute(onlyTags="never_called")
  public void notCalled()
  {
    fail("This before route method should never be called.");
  }

  @Route("/this/is/a/{}?one={}&two={}")
  public void getTest(String test, Integer one, Boolean two, RequestPath path, RequestParameters parameters)
  {
    methodsCalled.add("getTest");
    
    assertNotNull(test);
    assertNotNull(one);
    assertNotNull(two);
    assertNotNull(path);
    
    assertEquals("this", path.get(0));
    assertEquals("is", path.get(1));
    assertEquals("a", path.get(2));
    assertEquals(test, path.get(3));
    assertEquals(one, parameters.getInteger("one"));
    assertEquals(two, parameters.getBoolean("two"));
  }
  
  @Route(value="{}", tags="not_authenticated")
  public void getIntegerTest(int number, RequestPath path)
  {
    methodsCalled.add("getIntegerTest");
    assertEquals(number, path.getInteger(0));
  }
  
  @Route("/redirect")
  public void redirectTest()
  {
    methodsCalled.add("redirectTest");
    throw new RedirectTo("http://test.com/redirect");
  }


  @Route("/not_found")
  public void notFound()
  {
    methodsCalled.add("redirectTest");
    throw ReturnHttpResponseCode.NOT_FOUND;
  }

  @Route(value="/url", tags="not_authenticated")
  public void getUrl(URL url)
  {
    methodsCalled.add("getUrl");
    assertNotNull(url);
  }

}