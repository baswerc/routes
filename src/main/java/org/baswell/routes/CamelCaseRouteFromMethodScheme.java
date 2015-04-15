package org.baswell.routes;

import java.lang.reflect.Method;

public class CamelCaseRouteFromMethodScheme extends BaseRouteFromMethodScheme
{
  @Override
  public String getHttpPath(Method method)
  {
    return camelCaseToPath(removeHttpMethodsFromName(method));
  }

  static String camelCaseToPath(String method)
  {
    if ((method == null) || method.trim().isEmpty()) return "";

    char[] chars = method.toCharArray();
    String path = Character.toString(Character.toLowerCase(chars[0]));
    for (int i = 1; i < chars.length; i++)
    {
      char c = chars[i];
      if (Character.isUpperCase(c))
      {
        path += "/";
      }
      path += Character.toLowerCase(c);
    }

    return path;
  }
}
