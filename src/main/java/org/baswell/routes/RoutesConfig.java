package org.baswell.routes;


import org.baswell.routes.meta.MetaAuthenticator;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.baswell.routes.utils.RoutesMethods.typesToPatterns;

public class RoutesConfig
{
  public String rootPath;

  public String rootForwardPath = "/WEB-INF/jsps";

  public boolean caseInsensitive;

  public String defaultContentType;

  public boolean defaultResponseIsBody;

  public int streamBufferSize = 16 * 1024;

  public RouteInstanceFactory routeInstanceFactory = new DefaultRouteInstanceFactory();

  public RouteFromMethodScheme routeFromMethodScheme = new SimpleRouteFromMethodScheme();

  public String routesMetaPath;

  public MetaAuthenticator metaAuthenticator;

  public boolean routeUnannoatedPublicMethods;

  public final Map<String, Pattern> symbolsToPatterns = new HashMap<String, Pattern>();

  public boolean hasRoutesMetaPath()
  {
    return (routesMetaPath != null) && !routesMetaPath.isEmpty();
  }


  public RoutesConfig defineSymbol(String symbol, Class clazz) throws InvalidPatternException
  {
    if (typesToPatterns.containsKey(clazz))
    {
      return defineSymbol(symbol, typesToPatterns.get(clazz));
    }
    else
    {
      throw new InvalidPatternException("Invalid pattern class: " + clazz);
    }
  }

  public RoutesConfig defineSymbol(String symbol, String pattern) throws InvalidPatternException
  {
    try
    {
      symbolsToPatterns.put(symbol, Pattern.compile(pattern));
      return this;
    }
    catch (PatternSyntaxException e)
    {
      throw new InvalidPatternException("Invalid pattern: " + pattern);
    }
  }



}
