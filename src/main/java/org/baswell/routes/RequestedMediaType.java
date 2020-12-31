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

import static org.baswell.routes.RoutesMethods.hasContent;
import static org.baswell.routes.RoutesMethods.nullEmpty;

/**
 * The requested media type of an HTTP request. Taken from the either the file extension of the request path if present or the Accept header.
 */
public class RequestedMediaType
{
  public final String acceptType;

  public final String fileExtension;
  
  public final String mimeType;

  RequestedMediaType(String acceptType)
  {
    this(acceptType, null, null);
  }

  RequestedMediaType(String acceptType, RequestPath requestPath, RequestParameters requestParameters)
  {
    this.acceptType = acceptType;
    fileExtension = requestPath == null ? null : requestPath.getFileExtension();

    String mimeType = null;
    if (requestParameters != null && requestParameters.contains("mediaType")) {
      mimeType = requestParameters.get("mediaType");
    }

    if (nullEmpty(mimeType) && hasContent(fileExtension)) {
      mimeType = MIMETypes.getMimeTypeFromFileExtension(fileExtension);
    }

    if (nullEmpty(mimeType) && hasContent(acceptType)) {
      mimeType = acceptType;
    }

    if (nullEmpty(mimeType)) {
      mimeType = MIMETypes.HTML;
    }

    this.mimeType = mimeType;
  }

  public boolean contains(String mimeTypePartial) {
    return mimeType != null && mimeType.toLowerCase().contains(mimeTypePartial.toLowerCase());
  }

  @Override
  public String toString()
  {
    if (mimeType == null)
    {
      return "*/*";
    }
    else
    {
      return mimeType;
    }
  }
}
