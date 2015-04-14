package org.baswell.routes.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.baswell.routes.RequestParameters;
import org.baswell.routes.parsing.RouteParser;
import org.baswell.routes.parsing.RouteTree;

public class TestMethods
{
  static public RouteTree routeTree(String route)
  {
    return new RouteParser().parse(route);
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
