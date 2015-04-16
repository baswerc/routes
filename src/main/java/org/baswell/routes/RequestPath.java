package org.baswell.routes;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Details of the HTTP request path (minus the application's context path). The request path is broken up into segments
 * where a segment is the text between slashes.
 *
 * For example there are three path segments ("one", "two", and "three") in the URL:
 *
 * <code>
 * http://localhost:8080/context_path/one/two/three
 * </code>
 *
 * This class is immutable. All changing operations (ex. {@link #pop()} return new RequestPath objects.
 *
 */
public class RequestPath
{
  private final List<String> segments;

  private final String path;

  RequestPath(HttpServletRequest servletRequest)
  {
    this(parseUrlSegments(servletRequest.getRequestURI(), servletRequest.getContextPath()));
  }

  RequestPath(String path)
  {
    this(parseUrlSegments(path, ""));
  }

  RequestPath(List<String> segments)
  {
    this.segments = segments == null ? new ArrayList<String>() : segments;
    StringBuilder pathBuilder = new StringBuilder();
    for (int i = 0; i < segments.size(); i++)
    {
      pathBuilder.append('/').append(segments.get(i));
    }
    path = pathBuilder.toString();
  }

  /**
   *
   * @return The number of segments in this path.
   */
  public int size()
  {
    return segments.size();
  }

  /**
   *
   * @param index
   * @return The segment at the given index.
   * @throws IndexOutOfBoundsException If index < 0 or index >= {@link #size()}
   */
  public String get(int index) throws IndexOutOfBoundsException
  {
    return segments.get(index);
  }

  /**
   *
   * @param index
   * @return The first character of the segment at the given index.
   * @throws IndexOutOfBoundsException If index < 0 or index >= {@link #size()}
   */
  public char getCharacter(int index) throws IndexOutOfBoundsException
  {
    return segments.get(index).charAt(0);
  }

  /**
   *
   * @param index
   * @return The parsed boolean segment at the given index.
   * @throws IndexOutOfBoundsException If index < 0 or index >= {@link #size()}
   */
  public boolean getBoolean(int index) throws IndexOutOfBoundsException
  {
    return Boolean.parseBoolean(get(index));
  }

  /**
   *
   * @param index
   * @return The parsed byte segment at the given index
   * @throws IndexOutOfBoundsException If index < 0 or index >= {@link #size()}
   * @throws NumberFormatException If the segment at the given index cannot be parsed into a byte.
   */
  public byte getByte(int index) throws IndexOutOfBoundsException, NumberFormatException
  {
    return Byte.parseByte(get(index));
  }

  /**
   *
   * @param index
   * @return The parsed short segment at the given index
   * @throws IndexOutOfBoundsException If index < 0 or index >= {@link #size()}
   * @throws NumberFormatException If the segment at the given index cannot be parsed into a short.
   */
  public short getShort(int index) throws IndexOutOfBoundsException, NumberFormatException
  {
    return Short.parseShort(get(index));
  }

  /**
   *
   * @param index
   * @return The parsed int segment at the given index
   * @throws IndexOutOfBoundsException If index < 0 or index >= {@link #size()}
   * @throws NumberFormatException If the segment at the given index cannot be parsed into a int.
   */
  public int getInteger(int index) throws IndexOutOfBoundsException, NumberFormatException
  {
    return Integer.parseInt(get(index));
  }

  /**
   *
   * @param index
   * @return The parsed long segment at the given index
   * @throws IndexOutOfBoundsException If index < 0 or index >= {@link #size()}
   * @throws NumberFormatException If the segment at the given index cannot be parsed into a long.
   */
  public long getLong(int index) throws IndexOutOfBoundsException, NumberFormatException
  {
    return Long.parseLong(get(index));
  }

  /**
   *
   * @param index
   * @return The parsed float segment at the given index
   * @throws IndexOutOfBoundsException If index < 0 or index >= {@link #size()}
   * @throws NumberFormatException If the segment at the given index cannot be parsed into a float.
   */
  public float getFloat(int index) throws IndexOutOfBoundsException, NumberFormatException
  {
    return Float.parseFloat(get(index));
  }

  /**
   *
   * @param index
   * @return The parsed double segment at the given index
   * @throws IndexOutOfBoundsException If index < 0 or index >= {@link #size()}
   * @throws NumberFormatException If the segment at the given index cannot be parsed into a double.
   */
  public double getDouble(int index) throws IndexOutOfBoundsException, NumberFormatException
  {
    return Double.parseDouble(get(index));
  }

  /**
   *
   * @param path
   * @return True if this request path starts with the given path, false otherwise.
   */
  public boolean startsWith(String path)
  {
    return startsWith(parseUrlSegments(path));
  }


  /**
   *
   * @param segments
   * @return True if this request path starts with the given path segments, false otherwise.
   */
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

  /**
   *
   * Returns a new RequestPath with the given number of segments removed from the start of the path. The RequestPath
   * this method is called is unaltered.
   *
   * <code>requestPath.pop(2)</code> performed on:
   *
   * <code>/one/two/three</code>
   *
   * results in:
   *
   * <code>/three</code>
   *
   * @param numberSegments The number of segments to pop from this path.
   * @return A new RequestPath with the given number of segments removed from the head of the path.
   * @throws IndexOutOfBoundsException If numberSegments < 0 or numberSegments >= {@link #size()}.
   */
  public RequestPath pop(int numberSegments) throws IndexOutOfBoundsException
  {
    return new RequestPath(segments.subList(numberSegments, segments.size()));
  }

  /**
   * Same as <code>pop(1)</code>.
   *
   * @see #pop(int)
   * @return A new RequestPath with the 1 segment removed from the head of the path.
   * @throws IndexOutOfBoundsException if {@link #size()} == 0.
   */
  public RequestPath pop() throws IndexOutOfBoundsException
  {
    return pop(1);
  }

  /**
   * If the last segment in this path has a '.' then the full (last) segment will be returned. If the last segment does not
   * have a '.' then <code>null</code> is returned.
   *
   * @return The file name of the path or <code>null</code> if this path does not contain a file name at the end.
   */
  public String getFileName()
  {
    if (segments.isEmpty())
    {
      return null;
    }
    else
    {
      String fileNameCandidate = segments.get(segments.size() - 1);
      int lastIndex = fileNameCandidate.lastIndexOf('.');
      return lastIndex > 0 && lastIndex < fileNameCandidate.length() ? fileNameCandidate : null;
    }
  }

  /**
   * If this path contains a file name this method returns the text to the right of the last '.' in the file name. If the
   * path does not contain a file name then <code>null</code> is returned.
   * @return The file name extension or null if the path does contain a file name.
   */
  public String getFileExtension()
  {
    String fileName = getFileName();
    if (fileName != null)
    {
      return fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
    }
    else
    {
      return null;
    }
  }

  /**
   *
   * @param index
   * @return A new RequestPath from segment 0 to the given index.
   * @throws IndexOutOfBoundsException If index < 0 or index >= {@link #size()}.
   */
  public RequestPath substring(int index) throws IndexOutOfBoundsException
  {
    return new RequestPath(path.substring(index));
  }

  /**
   *
   * @param path
   * @return True if the segments of this request path match the segments of the given path, false otherwise.
   */
  public boolean equals(String path)
  {
    return equals(parseUrlSegments(path));
  }

  /**
   *
   * @param segments
   * @return True if the segments of this request path match the given segments, false otherwise.
   */
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
