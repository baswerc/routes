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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.Set;

import static org.baswell.routes.TypeMapper.*;

public class RequestContent<ContentType extends Object>
{
  static public void main(String[] args) throws Exception
  {
    ParameterizedType pt = (ParameterizedType)RequestContent.class.getMethod("test", RequestContent.class).getGenericParameterTypes()[0];

    System.out.println(pt.getActualTypeArguments()[0]);
  }

  private final RoutesConfiguration configuration;

  private final HttpServletRequest request;

  private final Class contentClass;

  private final RequestedMediaType requestedMediaType;

  private final String definedContentType;

  private final AvailableLibraries availableLibraries;

  private ContentType content;

  private boolean contentLoaded;

  RequestContent(RoutesConfiguration configuration, HttpServletRequest request, Class<ContentType> contentClass, RequestedMediaType requestedMediaType, String definedContentType, AvailableLibraries availableLibraries)
  {
    this.configuration = configuration;
    this.request = request;
    this.contentClass = contentClass;
    this.requestedMediaType = requestedMediaType;
    this.definedContentType = definedContentType;
    this.availableLibraries = availableLibraries;
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

        Pair<ContentConversionType, String> conversionTypeStringPair = mapContentConversionType(contentClass, mediaTypes, definedContentType, availableLibraries);
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
              content = (ContentType) new Gson().fromJson(new String(contentBytes), contentClass);
              break;

            case JACKSON:
              content = (ContentType) new ObjectMapper().readValue(contentBytes, contentClass);
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
              try
              {
                Document document = new SAXBuilder().build(new StringReader(new String(contentBytes)));
                content = (ContentType) ((contentConversionType == ContentConversionType.JDOM2_DOCUMENT) ? document : document.getRootElement());
              }
              catch (JDOMException e)
              {
                throw new RoutesException("Unable to create RequestContent class: " + contentClass.getName(), e);
              }
              break;

            case W3C_NODE:
              try
              {
                org.w3c.dom.Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(contentBytes));
                content = (ContentType) (contentClass == org.w3c.dom.Document.class ? document : document.getDocumentElement());
              }
              catch (ParserConfigurationException e)
              {
                throw new RoutesException("Unable to create RequestContent class: " + contentClass.getName(), e);
              }
              catch (SAXException e)
              {
                throw new RoutesException("Unable to create RequestContent class: " + contentClass.getName(), e);
              }
              break;
          }
        }
      }

      return content;
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

}
