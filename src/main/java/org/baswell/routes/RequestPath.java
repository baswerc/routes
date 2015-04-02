package org.baswell.routes;

import org.omg.CORBA.Request;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class RequestPath
{
  private final List<String> segments;

  private final String path;

  public RequestPath(HttpServletRequest servletRequest)
  {
    this(parseUrlSegments(servletRequest.getRequestURI(), servletRequest.getContextPath()));
  }

  public RequestPath(String path)
  {
    this(parseUrlSegments(path, ""));
  }

  public RequestPath(List<String> segments)
  {
    this.segments = segments == null ? new ArrayList<String>() : segments;
    StringBuilder pathBuilder = new StringBuilder();
    for (int i = 0; i < segments.size(); i++)
    {
      pathBuilder.append('/').append(segments.get(i));
    }
    path = pathBuilder.toString();
  }

  public int size()
  {
    return segments.size();
  }
  
  public String get(int index) throws IndexOutOfBoundsException
  {
    return segments.get(index);
  }
  
  public boolean getBoolean(int index) throws IndexOutOfBoundsException
  {
    return Boolean.parseBoolean(get(index));
  }

  public byte getByte(int index) throws IndexOutOfBoundsException, NumberFormatException
  {
    return Byte.parseByte(get(index));
  }

  public short getShort(int index) throws IndexOutOfBoundsException, NumberFormatException
  {
    return Short.parseShort(get(index));
  }
  
  public int getInteger(int index) throws IndexOutOfBoundsException, NumberFormatException
  {
    return Integer.parseInt(get(index));
  }

  public long getLong(int index) throws IndexOutOfBoundsException, NumberFormatException
  {
    return Long.parseLong(get(index));
  }

  public float getFloat(int index) throws IndexOutOfBoundsException, NumberFormatException
  {
    return Float.parseFloat(get(index));
  }

  public double getDouble(int index) throws IndexOutOfBoundsException, NumberFormatException
  {
    return Double.parseDouble(get(index));
  }

  public boolean startsWith(String path)
  {
    return startsWith(parseUrlSegments(path));
  }

  public RequestPath pop(int numberSegments)
  {
    return new RequestPath(segments.subList(numberSegments, segments.size()));
  }

  public RequestPath pop()
  {
    return new RequestPath(segments.subList(1, segments.size()));
  }

  public boolean startsWith(List<String> segments)
  {
    if (segments.size() > this.segments.size())
    {
      return false;
    }
    else
    {
      for (int i = 0; i < segments.size(); i++)
      {
        if (!segments.get(i).equals(this.segments.get(i)))
        {
          return false;
        }
      }
      return true;
    }
  }

  public RequestPath substring(int index)
  {
    return new RequestPath(path.substring(index));
  }

  public boolean equals(String path)
  {
    return equals(parseUrlSegments(path));
  }

  public boolean equals(List<String> segments)
  {
    return this.segments.equals(segments);
  }

  @Override
  public boolean equals(Object object)
  {
    return (object instanceof RequestPath) && ((RequestPath)object).segments.equals(segments);
  }

  @Override
  public String toString()
  {
    return path;
  }

  static List<String> parseUrlSegments(String url)
  {
    return parseUrlSegments(url, "");
  }

  static List<String> parseUrlSegments(String url, String contextPath)
  {
    if (url.startsWith(contextPath))
    {
      url = url.substring(contextPath.length(), url.length());
    }
    
    url = URLDecoder.decode(url).trim();
    List<String> urlSegments = new ArrayList<String>();
    if (url.startsWith("/"))
    {
      url = url.substring(1, url.length());
    }
    
    int index = url.indexOf('/');
    while (index != -1)
    {
      urlSegments.add(url.substring(0, index));
      url = url.substring((index + 1), url.length());
      index = url.indexOf('/');
    }
    
    if (url.length() > 0)
    {
      urlSegments.add(url);
    }
    
    return urlSegments;
  }

}
