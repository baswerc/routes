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

import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import static org.baswell.routes.TypeMapper.*;
import static org.baswell.routes.RoutesMethods.*;

/**
 * Convenience class for accessing the content of a request.
 *
 *
 * @param <ContentType>
 */
public class RequestContent<ContentType extends Object>
{
  private final RoutesConfiguration configuration;

  private final HttpServletRequest request;

  private final Type contentType;

  private final Class contentClass;

  private final String requestContentType;

  private final RequestedMediaType requestedMediaType;

  private final String returnedContentType;

  private final AvailableLibraries availableLibraries;

  private ContentType content;

  private boolean contentLoaded;

  RequestContent(RoutesConfiguration configuration, HttpServletRequest request, Type contentType, String requestContentType, RequestedMediaType requestedMediaType, String returnedContentType, AvailableLibraries availableLibraries)
  {
    this.configuration = configuration;
    this.request = request;
    this.contentType = contentType;
    this.requestContentType = requestContentType;
    this.requestedMediaType = requestedMediaType;
    this.returnedContentType = returnedContentType;
    this.availableLibraries = availableLibraries;

    contentClass = getClassFromType(contentType);
  }

  public ContentType get() throws IOException, RoutesException
  {
    if (contentLoaded)
    {
      return content;
    }
    else
    {
      contentLoaded = true;
      byte[] contentBytes = getContent();

      if (contentBytes != null)
      {
        Set<MediaType> mediaTypes = null;
        if ((requestedMediaType != null) && (requestedMediaType.mediaType != null))
        {
          mediaTypes = new HashSet<MediaType>();
          mediaTypes.add(requestedMediaType.mediaType);
        }

        String mimeType = hasContent(requestContentType) ? requestContentType : returnedContentType;
        Pair<ContentConversionType, String> conversionTypeStringPair = mapContentConversionType(contentClass, mediaTypes, mimeType, availableLibraries);
        if (conversionTypeStringPair == null)
        {
          content = (ContentType) contentBytes;
        }
        else
        {
          ContentConversionType contentConversionType = conversionTypeStringPair.x;
          switch (contentConversionType)
          {
            case TO_STRING:
              content = (ContentType) new String(contentBytes);
              break;

            case GSON:
              content = GSONBridge.parseGson(contentBytes, contentType);
              break;

            case JACKSON:
              content = JacksonBridge.parseJackson(contentBytes, contentType);
              break;

            case JAXB:
              try
              {
                content = (ContentType) JAXBContext.newInstance(contentClass).createUnmarshaller().unmarshal(new StringReader(new String(contentBytes)));
              }
              catch (JAXBException e)
              {
                throw new RoutesException("Unable to create RequestContent class: " + contentClass.getName(), e);
              }
              break;

            case JDOM2_DOCUMENT:
            case JDOM2_ELEMENT:
              content = parseJDOM(contentBytes, contentConversionType);
              break;

            case W3C_NODE:
              content = parseNode(contentBytes);
              break;
          }
        }
      }

      return content;
    }
  }

  ContentType parseJDOM(byte[] contentBytes, ContentConversionType contentConversionType) throws IOException
  {
    return (ContentType)JDOMBridge.parseRequestContent(contentBytes, contentConversionType, contentClass);
  }

  ContentType parseNode(byte[] contentBytes) throws IOException
  {
    try
    {
      org.w3c.dom.Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(contentBytes));
      return (ContentType) (contentType == org.w3c.dom.Document.class ? document : document.getDocumentElement());
    }
    catch (ParserConfigurationException e)
    {
      throw new RoutesException("Unable to create RequestContent class: " + contentClass.getName(), e);
    }
    catch (SAXException e)
    {
      throw new RoutesException("Unable to create RequestContent class: " + contentClass.getName(), e);
    }
  }


  byte[] getContent() throws IOException
  {
    InputStream inputStream = request.getInputStream();
    if (inputStream == null)
    {
      return null;
    }
    else
    {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      byte[] buffer = new byte[configuration.streamBufferSize];
      int read;

      while ((read = inputStream.read(buffer)) != -1)
      {
        bytes.write(buffer, 0, read);
      }

      return bytes.size() == 0 ? null : bytes.toByteArray();
    }
  }

  static Class getClassFromType(Type type)
  {
    if (type instanceof Class)
    {
      return (Class)type;
    }
    else if (type instanceof ParameterizedType)
    {
      return getClassFromType(((ParameterizedType)type).getRawType());
    }
    else
    {
      return null;
    }
  }

}
