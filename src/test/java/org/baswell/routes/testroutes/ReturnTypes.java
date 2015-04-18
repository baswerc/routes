package org.baswell.routes.testroutes;

import org.baswell.routes.MediaType;
import org.baswell.routes.RequestFormat;
import org.baswell.routes.Route;
import org.baswell.routes.Routes;

@Routes(defaultReturnedStringIsContent = true)
public class ReturnTypes extends BaseRoutes
{
  @Route("/helloworld")
  public String getHelloWorld()
  {
    methodsCalled.add("getHelloWorld");
    return "Hello World";
  }

  @Route(value = "/gson", respondsToMedia = MediaType.JSON)
  public GsonResponse gson()
  {
    methodsCalled.add("gson");
    GsonResponse gsonResponse = new GsonResponse();
    gsonResponse.one = "one";
    gsonResponse.two = 2;
    gsonResponse.three = true;
    return gsonResponse;
  }

  static public class GsonResponse
  {
    public String one;

    public int two;

    public boolean three;
  }
}
