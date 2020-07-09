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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class RoutesMethods
{
  public static void main(String[] args) throws Exception
  {
    for (Map.Entry<Class, String> entry : typesToPatterns.entrySet())
    {
      System.out.println(entry.getKey().getSimpleName() + " " + entry.getValue());
    }
  }

  static int size(List list)
  {
    return list == null ? 0 : list.size();
  }

  static boolean hasEntries(Collection collection)
  {
    return (collection != null) && !collection.isEmpty();
  }

  static boolean hasContent(CharSequence string)
  {
    return ((string != null) && !string.toString().trim().isEmpty());
  }

  static boolean nullEmpty(CharSequence string)
  {
    return ((string == null) || string.toString().trim().isEmpty());
  }

  static Class typeToClass(Type type)
  {
    if (type instanceof Class)
    {
      return (Class)type;
    }
    else if (type instanceof ParameterizedType)
    {
      return (Class)((ParameterizedType)type).getRawType();
    }
    else
    {
      return null;
    }
  }

  static String repeat(String str, int times)
  {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < times; i++) builder.append(str);
    return builder.toString();
  }

  static Class getListSingleParameterType(Type listParameter)
  {
    if (listParameter instanceof ParameterizedType)
    {
      Type[] types = ((ParameterizedType)listParameter).getActualTypeArguments();
      return ((types.length == 1) && (types[0] instanceof Class)) ? (Class)types[0] : null;
    }
    if (listParameter instanceof Class)
    {
      return String.class;
    }
    else
    {
      return null;
    }
  }

  static boolean classImplementsInterface(Class clazz, Class interfaze)
  {
    if ((clazz == null) || (clazz == Object.class))
    {
      return false;
    }

    Class[] implementedInterfaces = clazz.getInterfaces();
    for (Class implementedInterface : implementedInterfaces)
    {
      if (classInstanceOf(implementedInterface, interfaze))
      {
        return true;
      }
    }

    return classImplementsInterface(clazz.getSuperclass(), interfaze);
  }

  static boolean classInstanceOf(Class clazz, Class instanceOf)
  {
    if ((clazz == null) || (clazz == Object.class))
    {
      return false;
    }
    else if (clazz == instanceOf)
    {
      return true;
    }
    else
    {
      return classImplementsInterface(clazz.getSuperclass(), instanceOf);
    }
  }

  static final String BYTE_PATTERN = "(-?)\\d" + repeat("\\d?", 2);

  static final String SHORT_PATTERN = "(-?)\\d" + repeat("\\d?", 4);

  static final String INTEGER_PATTERN = "(-?)\\d" + repeat("\\d?", 9);

  static final String LONG_PATTERN = "(-?)\\d" + repeat("\\d?", 18);

  static final String FLOAT_PATTERN = "(-?)\\d" + repeat("\\d?", 7) + "(\\.\\d" + repeat("\\d?", 22) + ")?" ;

  static final String DOUBLE_PATTERN = "(-?)\\d" + repeat("\\d?", 14) + "(\\.\\d" + repeat("\\d?", 45) + ")?" ;
  
  static final String BOOLEAN_PATTERN = "(((t|T)(r|R)(u|U)(e|E))|((f|F)(a|A)(l|L)(s|S)(e|E)))";
  
  static final String CHAR_PATTERN = ".";

  static final String STRING_PATTERN = ".*";
  
  static final String WILDCARD_PATTERN = STRING_PATTERN;

  static Map<Class, String> typesToPatterns = new HashMap<Class, String>();
  static
  {
    typesToPatterns.put(Byte.class, BYTE_PATTERN);
    typesToPatterns.put(byte.class, BYTE_PATTERN);
    typesToPatterns.put(Short.class, SHORT_PATTERN);
    typesToPatterns.put(short.class, SHORT_PATTERN);
    typesToPatterns.put(Integer.class, INTEGER_PATTERN);
    typesToPatterns.put(int.class, INTEGER_PATTERN);
    typesToPatterns.put(Long.class, LONG_PATTERN);
    typesToPatterns.put(long.class, LONG_PATTERN);
    typesToPatterns.put(Float.class, FLOAT_PATTERN);
    typesToPatterns.put(float.class, FLOAT_PATTERN);
    typesToPatterns.put(Double.class, DOUBLE_PATTERN);
    typesToPatterns.put(double.class, DOUBLE_PATTERN);
    typesToPatterns.put(Boolean.class, BOOLEAN_PATTERN);
    typesToPatterns.put(boolean.class, BOOLEAN_PATTERN);
    typesToPatterns.put(String.class, STRING_PATTERN);
  }
  
  static Set<Class> methodRouteParameterTypes = new HashSet<Class>();
  static
  {
    methodRouteParameterTypes.add(HttpServletRequest.class);
    methodRouteParameterTypes.add(HttpServletResponse.class);
    methodRouteParameterTypes.add(Map.class);
    methodRouteParameterTypes.add(RequestPath.class);
    methodRouteParameterTypes.add(RequestParameters.class);
    methodRouteParameterTypes.add(RequestedMediaType.class);
    methodRouteParameterTypes.add(URL.class);
  }

}
