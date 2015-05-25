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

import javax.xml.bind.annotation.XmlRootElement;

import static org.baswell.routes.MIMETypes.*;
import static org.baswell.routes.RoutesMethods.classImplementsInterface;

enum ContentConversionType
{
  JSON_SIMPLE(JSON),
  GSON(JSON),
  JACKSON(JSON),
  W3C_NODE(XML),
  JAXB(XML),
  JDOM2_DOCUMENT(XML),
  JDOM2_ELEMENT(XML),
  TO_STRING(null);

  public final String mimeType;

  ContentConversionType(String mimeType)
  {
    this.mimeType = mimeType;
  }

  static ContentConversionType mapContentConversionType(Class conversionType, MediaType mediaType, AvailableLibraries availableLibraries)
  {
    String returnClassName = conversionType.getCanonicalName();
    String returnTypePackage = conversionType.getPackage().getName();

    if (returnTypePackage.equals("org.json.simple"))
    {
      return ContentConversionType.JSON_SIMPLE;
    }
    else if (returnTypePackage.startsWith("org.w3c.dom"))
    {
      return ContentConversionType.W3C_NODE;
    }
    else if (returnClassName.equals("org.jdom2.Document"))
    {
      return ContentConversionType.JDOM2_DOCUMENT;
    }
    else if (returnClassName.equals("org.jdom2.Element"))
    {
      return ContentConversionType.JDOM2_ELEMENT;
    }
    else if (conversionType.getAnnotation(XmlRootElement.class) != null)
    {
      return ContentConversionType.JAXB;
    }
    else if ((((mediaType != null) && (mediaType == MediaType.JSON))) && availableLibraries.jacksonAvailable())
    {
      return ContentConversionType.JACKSON;
    }
    else if ((((mediaType != null) && (mediaType == MediaType.JSON))) && availableLibraries.gsonAvailable())
    {
      return ContentConversionType.GSON;
    }
    else if (classImplementsInterface(conversionType, CharSequence.class))
    {
      return ContentConversionType.TO_STRING;
    }
    else
    {
      return null;
    }
  }
}
