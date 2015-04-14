package org.baswell.routes.testroutes;

import org.baswell.routes.BeforeRoute;
import org.baswell.routes.RedirectTo;
import org.baswell.routes.RequestContext;
import org.baswell.routes.RequestPath;
import org.baswell.routes.ReturnHttpResponseCode;
import org.baswell.routes.Route;

import static org.junit.Assert.*;

public class BasicRoutes extends BaseRoutes
{
  public boolean authenticationAllowed;
  
  @BeforeRoute(exceptTags="not_authenticated")
  public boolean requireAuthentication(RequestContext c)
  {
    methodsCalled.add("requireAuthentication");
    return c.parameters.getBoolean("authenticationAllowed", false);
  }
  
  @BeforeRoute(onlyTags="never_called")
  public void notCalled()
  {
    fail("This before route method should never be called.");
  }
  
  @Route("/this/is/a/{}?one={}&two={}")
  public void getTest(String test, Integer one, Boolean two, RequestContext c)
  {
    methodsCalled.add("getTest");
    
    assertNotNull(test);
    assertNotNull(one);
    assertNotNull(two);
    assertNotNull(c);
    
    assertEquals("this", c.path.get(0));
    assertEquals("is", c.path.get(1));
    assertEquals("a", c.path.get(2));
    assertEquals(test, c.path.get(3));
    assertEquals(one, c.parameters.getInteger("one"));
    assertEquals(two, c.parameters.getBoolean("two"));
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

}