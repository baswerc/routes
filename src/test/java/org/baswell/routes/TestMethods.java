package org.baswell.routes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestMethods
{
  static public ParsedRouteTree routeTree(String route)
  {
    return new Parser().parse(route);
  }
  
  static public RequestParameters getRequestParameters(Object... values)
  {
    return new RequestParameters(toParameterMap(values));
  }
  
  static public Map<String, String[]> toParameterMap(Object... values)
  {
    Map<String, String[]> parameters = new HashMap<String, String[]>();
    if (values != null)
    {
      for (int i = 0; i < values.length; i+= 2)
      {
        String name = values[0].toString();
        Object value = values[1];
        String[] paramValues;
        if (value instanceof List)
        {
          List list = (List)value;
          paramValues = new String[list.size()];
          for (int j = 0; j < list.size(); j++) paramValues[j] = list.get(j).toString();
        }
        else
        {
          paramValues = new String[] {value.toString()};
        }
        parameters.put(name, paramValues);
      }
    }
    return parameters;
  }
}
