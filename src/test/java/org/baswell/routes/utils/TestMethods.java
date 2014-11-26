package org.baswell.routes.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import javax.servlet.http.Part;

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
