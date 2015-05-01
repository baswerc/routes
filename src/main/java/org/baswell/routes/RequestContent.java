package org.baswell.routes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.ParameterizedType;

public class RequestContent<ContentType extends Object>
{
  static public void main(String[] args) throws Exception
  {
    ParameterizedType pt = (ParameterizedType)RequestContent.class.getMethod("test", RequestContent.class).getGenericParameterTypes()[0];

    System.out.println(pt.getActualTypeArguments()[0]);
  }

  private final MediaType mediaType;

  private final HttpServletRequest request;

  private final Class<ContentType> contentClass;

  private ContentType content;

  RequestContent(HttpServletRequest request, MediaType mediaType, Class<ContentType> contentClass, AvailableLibraries libraries)
  {
    this.request = request;
    this.mediaType = mediaType;
    this.contentClass = contentClass;
  }

  public void test(RequestContent<String> myparameter)
  {

  }

  public ContentType get()
  {
    if (contentLoaded)
    {
      return content;
    }
    else
    {

    }
    return content;
  }
}
