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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.baswell.routes.RoutesMethods.typesToPatterns;

/**
 * Global route configuration.
 */
public class RoutesConfiguration
{
  /**
   * If true the {@link org.baswell.routes.RoutingTable} will spawn a thread that rebuilds the routing table every
   * {@link #developmentReloadCycleSeconds}.
   *
   * Default value: {@code false}
   */
  public boolean developmentMode;

  /**
   * The number of seconds between development mode reloads of the {@link org.baswell.routes.RoutingTable}. If {@link #developmentMode}
   * is false this property is not used.
   *
   * Default value: 5
   */
  public int developmentReloadCycleSeconds = 5;

  /**
   * The global path prepended to all route paths. For example if you want your route objects to process request that
   * start with "/api" set this variable to "/api" and then all route class paths will get prepended with this value.
   *
   * Default value: <status>null</status>
   */
  public String rootPath;

  /**
   * The root directory prepended to all file resource forward paths. If you want each Route to specify the full forward
   * path set this variable to <status>null</status>.
   *
   * Default value: <status>"/WEB-INF/jsps"</status>
   */
  public String rootForwardPath = "/WEB-INF/jsps";

  /**
   * Is route path and parameter matching case insensitive?
   *
   * Default value: <status>false</status>
   */
  public boolean caseInsensitive;


  /**
   * Default content type returned by all routes if not overridden. This can be overridden in {@link Routes#defaultContentType()},
   * {@link Route#contentType()}, or by explicitly calling {@link javax.servlet.http.HttpServletResponse#setContentType(String)} in the
   * route method.
   *
   * Default value: <status>null</status>
   */
  public String defaultContentType;

  /**
   * If true by default strings returned from route methods are sent back as the content. If false by default returned strings are interpreted as
   * file paths that the request is forwarded to. This can be overridden in {@link Routes#defaultReturnedStringIsContent()} or {@link Route#returnedStringIsContent()}.
   *
   * Default value: <status>false</status>
   */
  public boolean defaultReturnedStringIsContent;

  /**
   * The buffer size for streaming back content.
   *
   * Default value: <status>16 * 1024</status>
   */
  public int streamBufferSize = 16 * 1024;

  /**
   * The factory for creating new route instances to process HTTP requests.
   *
   * Default value: {@link DefaultRouteInstancePool}
   */
  public RouteInstancePool routeInstancePool = new DefaultRouteInstancePool();

  /**
   * The scheme for mapping route methods to HTTP methods and HTTP paths when not explicitly set by the {@link org.baswell.routes.Route}
   * annotation.
   *
   * Default value: {@link RouteByLowercaseConvention}
   */
  public RouteByConvention routeByConvention = new RouteByLowercaseConvention();

  /**
   * The routes cache implementation. No cache will be used if null.
   *
   * Default value: <status>null</status>
   */
  public RoutesCache routesCache;

  /**
   * The path (relative to context path) to access the Routes meta page. If this value is null then the Routes meta
   * page will not be accessible. If there is a collision between this path and a routes path, the routes path will
   * win.
   *
   * Default value: <status>null</status>
   */
  public String routesMetaPath;

  /**
   * The authentication mechanism for the Routes meta page. If null, no authentication is required.
   *
   * Default value: <status>null</status>
   */
  public MetaAuthenticator metaAuthenticator;

  /**
   * Default value for determining if public methods be candidates for HTTP routes if they aren't annotated with {@link org.baswell.routes.Route}?
   * If true only unannotated, public methods of the immediate class will be used. Public, unannotated methods of extended
   * classes will not be candidates (ex. {@link java.lang.Object#equals(Object)}. This can be overriden by {@link Routes#routeUnannotatedPublicMethods()}.
   *
   *
   * Default value: <status>false</status>
   */
  public boolean routeUnannotatedPublicMethods;

  /**
   * Logger Routes uses for error messages. Set to {@code null} to suppress all logging. By default {@link org.baswell.routes.SystemErrLogger}.
   */
  public RoutesLogger logger = new SystemErrLogger();

  final Map<String, Pattern> symbolsToPatterns = new HashMap<String, Pattern>();

  /**
   * Defines a new regular expression whose identity is the given symbol and value is the regular expression for one of the
   * following classes:
   *
   * <ul>
   *   <li>Byte</li>
   *   <li>byte</li>
   *   <li>Short</li>
   *   <li>short</li>
   *   <li>Integer</li>
   *   <li>int</li>
   *   <li>Long</li>
   *   <li>long</li>
   *   <li>Float</li>
   *   <li>float</li>
   *   <li>Double</li>
   *   <li>double</li>
   *   <li>Boolean</li>
   *   <li>boolean</li>
   *   <li>String</li>
   * </ul>
   *
   * @param symbol
   * @param clazz
   * @return
   * @throws IllegalArgumentException If the given clazz is not one of the above classes.
   */
  public RoutesConfiguration defineSymbol(String symbol, Class clazz) throws IllegalArgumentException
  {
    if (typesToPatterns.containsKey(clazz))
    {
      return defineSymbol(symbol, typesToPatterns.get(clazz));
    }
    else
    {
      throw new IllegalArgumentException("Invalid pattern class: " + clazz);
    }
  }

  /**
   * Defines a new regular expression whose id is the given symbol and value is the given pattern.
   *
   * @param symbol
   * @param pattern
   * @return
   * @throws IllegalArgumentException If the given pattern is not a valid regular expression.
   */
  public RoutesConfiguration defineSymbol(String symbol, String pattern) throws IllegalArgumentException
  {
    try
    {
      symbolsToPatterns.put(symbol, Pattern.compile(pattern));
      return this;
    }
    catch (PatternSyntaxException e)
    {
      throw new IllegalArgumentException("Invalid pattern: " + pattern);
    }
  }

  boolean hasRoutesMetaPath()
  {
    return (routesMetaPath != null) && !routesMetaPath.isEmpty();
  }
}
