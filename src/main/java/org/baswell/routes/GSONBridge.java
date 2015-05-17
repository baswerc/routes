package org.baswell.routes;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;

public class GSONBridge
{
  static <RequestContentType extends Object> RequestContentType parseGson(byte[] contentBytes, Type contentType)
  {
    return (RequestContentType) new Gson().fromJson(new String(contentBytes), contentType);
  }

  static void sendGson(Object response, HttpServletResponse servletResponse) throws IOException
  {
    servletResponse.getWriter().write(new Gson().toJson(response));
  }


}
