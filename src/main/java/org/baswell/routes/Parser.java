package org.baswell.routes;

import java.util.ArrayList;
import java.util.List;

class Parser
{
  ParsedRouteTree parse(String route) throws RoutesException
  {
    List<ParsedPathTerminal> pathTerminals = new ArrayList<ParsedPathTerminal>();
    List<ParsedParameterTerminal> parameterTerminals = new ArrayList<ParsedParameterTerminal>();

    
    int parameterIndex = findParameterIndex(route);
    String pathPart = parameterIndex == -1 ? route : route.substring(0, parameterIndex);
    String parameterPart = ((parameterIndex == -1) || (parameterIndex == (route.length() - 1))) ? null : route.substring(parameterIndex + 1, route.length());

    int routeIndex = 0;
    
    if (pathPart.startsWith("/")) pathPart = route.substring(1, pathPart.length());
    if (pathPart.endsWith("/")) pathPart = route.substring(0, pathPart.length() - 1);
    
    String[] pathSegments = pathPart.split("/");
    for (int i = 0; i < pathSegments.length; i++)
    {
      String pathSegment = pathSegments[i];
      if (pathSegment.equals("{}"))
      {
        pathTerminals.add(new ParsedMethodParameterPathTerminal(routeIndex++, i));
      }
      else if (pathSegment.startsWith("{") && pathSegment.endsWith("}"))
      {
        pathTerminals.add(new ParsedPatternPathTerminal(routeIndex++, i, pathSegment.substring(1, pathSegment.length() - 1)));
      }
      else if (pathSegment.equals("*"))
      {
        pathTerminals.add(new ParsedWildcardPathTerminal(routeIndex++, i));
      }
      else if (pathSegment.equals("**"))
      {
        pathTerminals.add(new ParsedDoubleWildcardPathTerminal(routeIndex++, i));
      }
      else if (isSymbol(pathSegment))
      {
        pathTerminals.add(new ParsedSymbolPathTerminal(routeIndex++, i, pathSegment.substring(1, pathSegment.length())));
      }
      else if (!pathSegment.isEmpty())
      {
        pathTerminals.add(new ParsedExactPathTerminal(routeIndex++, i, pathSegment));
      }
    }
    
    if (parameterPart != null)
    {
      String[] pairs = parameterPart.split("&");
      for (String pair : pairs)
      {
        boolean optional;
        if (pair.startsWith("(") && pair.endsWith(")?"))
        {
          optional = true;
          pair = pair.substring(1, pair.length() - 2);
        }
        else
        {
          optional = false;
        }
        
        int index = pair.indexOf('=');
        if (index != -1)
        {
          String name = pair.substring(0, index);
          String value = pair.substring(index + 1, pair.length());
          
          if (value.equals("{}"))
          {
            parameterTerminals.add(new ParsedMethodParameterParameterTerminal(routeIndex++, name, optional));
          }
          else if (value.startsWith("{") && value.endsWith("}"))
          {
            parameterTerminals.add(new ParsedPatternParameterTerminal(routeIndex++, name, value.substring(1, value.length() - 1), optional));
          }
          else if (value.equals("*"))
          {
            parameterTerminals.add(new ParsedWildcardParameterTerminal(routeIndex++, name, optional));
          }
          else if (isSymbol(value))
          {
            parameterTerminals.add(new ParsedSymbolParameterTerminal(routeIndex++, name, value.substring(1, value.length()), optional));
          }
          else
          {
            parameterTerminals.add(new ParsedExactParameterTerminal(routeIndex++, name, value, optional));
          }
        }
      }
    }
    
    
    return new ParsedRouteTree(pathTerminals, parameterTerminals);
  }
  
  static int findParameterIndex(String route)
  {
    int openPatternBrackets = 0;
    char[] chars = route.toCharArray();
    for (int i = 0; i < chars.length; i++)
    {
      char c = chars[i];
      if ((c == '?') && (openPatternBrackets == 0))
      {
        return i;
      }
      else if (c == '{')
      {
        ++openPatternBrackets;
      }
      else if (c == '}')
      {
        openPatternBrackets = Math.max(0, openPatternBrackets - 1);
      }
    }
    return -1;
  }
  
  static boolean isSymbol(String string)
  {
    return ((string != null) && string.startsWith(":") && (string.length() > 1) && (string.indexOf(' ') == -1));
  }

}
