/*
 * Copyright 2015 Corey Baswell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.baswell.routes;

/**
 * The requested media type of an HTTP request. Taken from the either the file extension of the request path if present or the Accept header.
 */
public class RequestedMediaType
{
  public final String acceptType;

  public final String fileExtension;
  
  public final MediaType mediaType;

  RequestedMediaType(String acceptType)
  {
    this(acceptType, null);
  }

  RequestedMediaType(String acceptType, RequestPath requestPath)
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
        return mediaType.mimeType;
      }
    }
    else
    {
      return acceptType;
    }
  }
}
