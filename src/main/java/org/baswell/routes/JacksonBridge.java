package org.baswell.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;

public class JacksonBridge
{
  static <RequestContentType extends Object> RequestContentType parseJackson(byte[] contentBytes, Type contentType) throws IOException
  {
    return (RequestContentType) new ObjectMapper().readValue(contentBytes, TypeFactory.defaultInstance().constructType(contentType));
  }

  static void sendJackson(Object response, HttpServletResponse servletResponse) throws IOException
  {
    new ObjectMapper().writeValue(servletResponse.getWriter(), response);
  }
}
