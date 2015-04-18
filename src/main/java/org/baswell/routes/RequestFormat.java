package org.baswell.routes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The format type of an HTTP request. Taken from the either the Accept-Type header of the request (if present) or the file
 * extension (if present) of the URL.
 */
public class RequestFormat
{
  public final String acceptType;

  public final String fileExtension;
  
  public final MediaType mediaType;

  public RequestFormat(String acceptType)
  {
    this(acceptType, null);
  }

  public RequestFormat(String acceptType, RequestPath requestPath)
  {
    this.acceptType = acceptType;
    fileExtension = requestPath == null ? null : requestPath.getFileExtension();

    MediaType mediaType = null;

    if (fileExtension != null)
    {
      mediaType = MediaType.findFromExtension(fileExtension);
    }

    if ((mediaType == null) && (acceptType != null))
    {
      mediaType = MediaType.findFromMimeType(acceptType);
    }

    this.mediaType = mediaType;
  }

  @Override
  public String toString()
  {
    if (acceptType == null)
    {
      if (mediaType == null)
      {
        return "*/*";
      }
      else
      {
        return mediaType.mimeTypes.get(0);
      }
    }
    else
    {
      return acceptType;
    }
  }
}
