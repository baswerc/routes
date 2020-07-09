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

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import static org.baswell.routes.ContentConversionType.*;
import static org.baswell.routes.RoutesMethods.*;

/**
 * <p>
 * Hub for all routes. Routes can be added to the {@code RoutingTable} in the following ways:
 * </p>
 *
 * <p>
 * 1. As class objects. Routes added as class objects are instantiated on each matched HTTP request. Once the HTTP request
 * is processed, the instantiated route object is discarded. Every HTTP request gets its own route object instance. The
 * route class does not have to be thread safe.
 * </p>
 *
 * <p>
 * 2. As instances of the route class. In this case the same route object will get used for every matched HTTP request. The
 * route class must be thread safe as it can process multiple HTTP requests concurrently.
 * </p>
 *
 * <p>
 * Normally {@code build} should be called during application startup after all routes have been added. If there are any
 * errors in route configurations an error will be thrown then. Otherwise this method will be called on the first request
 * that uses the Routes engine.
 * </p>
 *
 * <p>
 * If the {@code RoutesFilter} or {@code RoutesServlet} is used in web.xml then this class should be treated as a singleton (only
 * one instance created). If multiple instances of {@code RoutingTable} are created then {@code RoutesFilter} and {@code RoutesServlet}
 * will only have visibly to the last one created.
 * </p>
 *
 */
public class RoutingTable
{
  static RoutingTable theRoutingTable;

  final RoutesConfiguration routesConfiguration;

  volatile boolean built;

  private List<Object> addedObjects = new ArrayList<Object>();
  
  private List<RouteNode> routeNodes;

  private Thread developmentModeThread;

  /**
   * The default {@link org.baswell.routes.RoutesConfiguration} will be used.
   */
  public RoutingTable()
  {
    this(new RoutesConfiguration());
  }

  public RoutingTable(RoutesConfiguration routesConfiguration)
  {
    this.routesConfiguration = routesConfiguration == null ? new RoutesConfiguration() : routesConfiguration;
    RoutingTable.theRoutingTable = this;
  }

  /**
   * Calls {@link #add} for each object in the given list. Can be used in spring XML mappings as:
   * <code>
   * <bean id="routingTable" class="org.baswell.routes.RoutingTable" init-method="build">
   *   <property name="routes">
   *     <list>
   *       <ref bean="loginRoutes"/>
   *       <ref bean="homeRoutes"/>
   *       <ref bean="helpRoutes"/>
   *     </list>
   *   </property>
   * </bean>
   * </code>
   * @param routeObjects The list of route objects to add to the routing table.
   */
  public synchronized void setRoutes(List<Object> routeObjects)
  {
    for (Object routeObject : routeObjects)
    {
      add(routeObject);
    }
  }

  /**
   * Adds to the given objects to the routing table. Objects can be either the route class object or a route instance
   * object.
   *
   * @param instancesOrClasses Route class or instance objects.
   * @return This RoutingTable
   */
  public synchronized RoutingTable register(Object... instancesOrClasses)
  {
    built = false;
    for (Object obj : instancesOrClasses) if (!addedObjects.contains(obj)) addedObjects.add(obj);
    return this;
  }


  /**
   * Adds to the given objects to the routing table. Objects can be either the route class object or a route instance
   * object.
   *
   * @param instancesOrClasses Route class or instance objects.
   * @return This RoutingTable
   */
  public synchronized RoutingTable add(Object... instancesOrClasses)
  {
    built = false;
    for (Object obj : instancesOrClasses) if (!addedObjects.contains(obj)) addedObjects.add(obj);
    return this;
  }

  /**
   * Builds the routing table from the added route objects. This will typically only be called once after all route objects
   * have been added in your application's bootstrap.
   *
   * @throws RoutesException If an added route is configured incorrectly.
   */
  public void build() throws RoutesException
  {
    Parser parser = new Parser();
    CriteriaBuilder criteriaBuilder = new CriteriaBuilder();
    MethodParametersBuilder parametersBuilder = new MethodParametersBuilder();
    AvailableLibraries availableLibraries = new AvailableLibraries(routesConfiguration);

    List<RouteNode> routeNodes = new ArrayList<RouteNode>();
    for (Object addedObject : addedObjects)
    {
      boolean instanceIsClass = (addedObject instanceof Class);
      Class routesClass = instanceIsClass ? (Class) addedObject : addedObject.getClass();

      List<BeforeRouteNode> classBeforeNodes = getBeforeRouteNodes(routesClass);
      List<AfterRouteNode> classAfterNodes = getAfterRouteNodes(routesClass);

      Routes routesAnnotation = new RoutesAggregate(routesClass);

      int numRoutesPaths;
      boolean routeUnannotatedPublicMethods;

      if (routesAnnotation == null)
      {
        numRoutesPaths = 1;
        if (routesConfiguration.routeUnannotatedPublicMethods)
        {
          routeUnannotatedPublicMethods = true;
        }
        else
        {
          /*
           * If this an empty class with no Route annotations then we're going to route the public methods since it
           * was added to the RoutingTable
           */
          boolean routeAnnotationFound = false;
          for (Method method : routesClass.getMethods())
          {
            if (method.getAnnotation(Route.class) != null)
            {
              routeAnnotationFound = true;
              break;
            }
          }
          routeUnannotatedPublicMethods = !routeAnnotationFound;
        }
      }
      else
      {
        numRoutesPaths = Math.max(1, routesAnnotation.value().length);
        routeUnannotatedPublicMethods = routesAnnotation.routeUnannotatedPublicMethods().length == 0 ? routesConfiguration.routeUnannotatedPublicMethods : routesAnnotation.routeUnannotatedPublicMethods()[0];
      }

      List<RouteNode> classRoutes = new ArrayList<RouteNode>();

      for (Method method : routesClass.getMethods())
      {
        if (isMain(method)) continue;

        Route routeAnnotation = method.getAnnotation(Route.class);
        if ((routeAnnotation != null) || (routeUnannotatedPublicMethods && Modifier.isPublic(method.getModifiers()) && (method.getDeclaringClass() == routesClass)))
        {

          for (int i = 0; i < numRoutesPaths; i++)
          {
            RouteConfiguration routeConfiguration = new RouteConfiguration(routesClass, method, routesConfiguration, routesAnnotation, routeAnnotation, i);
            ParsedRouteTree tree = parser.parse(routeConfiguration.route);
            RouteInstance routeInstance = instanceIsClass ? new RouteInstance(routesClass, routesConfiguration.routeInstancePool) : new RouteInstance(addedObject);
            Criteria criteria = criteriaBuilder.buildCriteria(method, tree, routeConfiguration, routesConfiguration);
            List<MethodParameter> parameters = parametersBuilder.buildParameters(method, criteria);
            ResponseType responseType = mapResponseType(method, routeConfiguration);
            ContentConversionType contentConversionType;

            /*
             * Try to map the content version type statically. If the method is strongly typed this should work. If it's more dynamic (for example returns a java.lang.Object) then
             * we won't be able to map it here and will have to map it dyanmically on each request.
             */
            if (responseType == ResponseType.STRING_CONTENT)
            {
              MediaType mediaType = null;
              if (hasContent(routeConfiguration.contentType))
              {
                mediaType = MediaType.findFromMimeType(routeConfiguration.contentType);
              }

              if ((mediaType == null) && (routeConfiguration.respondsToMedia.size() == 1))
              {
                mediaType = routeConfiguration.respondsToMedia.get(0);
              }

              contentConversionType = mapContentConversionType(method.getReturnType(), mediaType, availableLibraries);
              if ((contentConversionType != null) && nullEmpty(routeConfiguration.contentType))
              {
                routeConfiguration.contentType = contentConversionType.mimeType;
              }
            }
            else
            {
              contentConversionType = null;
            }

            List<BeforeRouteNode> beforeNodes = new ArrayList<BeforeRouteNode>();
            for (BeforeRouteNode beforeNode : classBeforeNodes)
            {
              if ((beforeNode.onlyTags.isEmpty() || containsOne(beforeNode.onlyTags, routeConfiguration.tags)) && (beforeNode.exceptTags.isEmpty() || !containsOne(beforeNode.exceptTags, routeConfiguration.tags)))
              {
                beforeNodes.add(beforeNode);
              }
            }

            List<AfterRouteNode> afterNodes = new ArrayList<AfterRouteNode>();
            for (AfterRouteNode afterNode : classAfterNodes)
            {
              if ((afterNode.onlyTags.isEmpty() || containsOne(afterNode.onlyTags, routeConfiguration.tags)) && (afterNode.exceptTags.isEmpty() || !containsOne(afterNode.exceptTags, routeConfiguration.tags)))
              {
                afterNodes.add(afterNode);
              }
            }

            classRoutes.add(new RouteNode(routeNodes.size(), method, routeConfiguration, routeInstance, criteria, parameters, responseType, contentConversionType, beforeNodes, afterNodes));
          }
        }
      }

      if (!classRoutes.isEmpty())
      {
        routeNodes.addAll(classRoutes);
      }
      else
      {
        throw new RoutesException("Route class: " + routesClass + " has no routes.");
      }

    }
    Collections.sort(routeNodes);

    this.routeNodes = routeNodes;
    if (!built)
    {
      built = true;
      if (routesConfiguration.developmentMode)
      {
        if (routesConfiguration.logger != null)
        {
          routesConfiguration.logger.logError("Routes is running in development mode.");
        }
        developmentModeThread = new Thread(new Runnable()
        {
          @Override
          public void run()
          {
            try
            {
              while (developmentModeThread == Thread.currentThread())
              {
                Thread.sleep(routesConfiguration.developmentReloadCycleSeconds * 1000);

                build();
              }
            }
            catch (Exception e)
            {
              if (routesConfiguration.logger != null)
              {
                routesConfiguration.logger.logError("Routes development mode thread crashed.", e);
              }
            }
          }
        }, "Routes Development Reloading");

        developmentModeThread.setDaemon(true);
        developmentModeThread.start();
      }
    }
  }

  public void shutdown()
  {
    developmentModeThread = null;
    built = false;
    routeNodes.clear();;
  }

  MatchedRoute find(RequestPath path, RequestParameters parameters, HttpMethod httpMethod, RequestedMediaType requestedMediaType)
  {
    List<Matcher> pathMatchers = new ArrayList<Matcher>();
    Map<String, Matcher> parameterMatchers = new HashMap<String, Matcher>();
    for (RouteNode routeNode : routeNodes)
    {
      pathMatchers.clear();
      parameterMatchers.clear();
      if (routeNode.criteria.matches(httpMethod, requestedMediaType, path, parameters, pathMatchers, parameterMatchers))
      {
        return new MatchedRoute(routeNode, pathMatchers, parameterMatchers);
      }
    }
    return null;
  }

  List<RouteNode> getRouteNodes()
  {
    return new ArrayList<RouteNode>(routeNodes);
  }

  static List<BeforeRouteNode> getBeforeRouteNodes(Class clazz) throws RoutesException
  {
    List<Class> classHierarchy = new ArrayList<Class>();
    classHierarchy.add(clazz);
    Class c = clazz;
    while ((c = c.getSuperclass()) != null)
    {
      classHierarchy.add(0, c);
    }

    List<BeforeRouteNode> nodes = new ArrayList<BeforeRouteNode>();
    for (Method method : clazz.getMethods())
    {
      BeforeRoute beforeRoute = method.getAnnotation(BeforeRoute.class);
      if (beforeRoute != null)
      {
        Class returnType = method.getReturnType();
        int hierarchyOrder = classHierarchy.indexOf(method.getDeclaringClass());
        if ((returnType == boolean.class) || (returnType == Boolean.class) || (returnType == void.class))
        {
          boolean returnsBoolean = returnType != void.class;
          List<MethodParameter> routeParameters = new MethodParametersBuilder().buildParameters(method);
          Integer explicitOrder = beforeRoute.order().length == 0 ? null : beforeRoute.order()[0];

          nodes.add(new BeforeRouteNode(method, routeParameters, returnsBoolean, new HashSet<String>(Arrays.asList(beforeRoute.onlyTags())), new HashSet<String>(Arrays.asList(beforeRoute.exceptTags())), explicitOrder, hierarchyOrder));
        }
        else
        {
          throw new RoutesException("Before method: " + method + " must have return type of boolean or void.");
        }
      }
    }

    Collections.sort(nodes);
    return nodes;
  }

  static List<AfterRouteNode> getAfterRouteNodes(Class clazz) throws RoutesException
  {
    List<Class> classHierarchy = new ArrayList<Class>();
    classHierarchy.add(clazz);
    Class c = clazz;
    while ((c = c.getSuperclass()) != null)
    {
      classHierarchy.add(0, c);
    }

    List<AfterRouteNode> nodes = new ArrayList<AfterRouteNode>();
    for (Method method : clazz.getMethods())
    {
      AfterRoute afterRoute = method.getAnnotation(AfterRoute.class);
      if (afterRoute != null)
      {
        Class returnType = method.getReturnType();
        int hierarchyOrder = classHierarchy.indexOf(method.getDeclaringClass());
        if (returnType == void.class)
        {
          List<MethodParameter> routeParameters = new MethodParametersBuilder().buildParameters(method);
          boolean onlyOnSuccess = afterRoute.onlyOnSuccess().length == 0 ? false : afterRoute.onlyOnSuccess()[0];
          boolean onlyOnError = afterRoute.onlyOnError().length == 0 ? false : afterRoute.onlyOnError()[0];
          Integer explicitOrder = afterRoute.order().length == 0 ? null : afterRoute.order()[0];
          nodes.add(new AfterRouteNode(method, routeParameters, new HashSet<String>(Arrays.asList(afterRoute.onlyTags())), new HashSet<String>(Arrays.asList(afterRoute.exceptTags())), onlyOnSuccess, onlyOnError, explicitOrder, hierarchyOrder));
        }
        else
        {
          throw new RoutesException("After method: " + method + " must have void return type.");
        }
      }
    }
    
    Collections.sort(nodes);
    return nodes;
  }
  
  static boolean containsOne(Set<String> set, Set<String> contains)
  {
    for (String string : contains)
    {
      if (set.contains(string)) return true;
    }
    return false;
  }

  static boolean isMain(Method method)
  {
    if (method.getName().equals("main") && Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers()))
    {
      if (method.getReturnType() == void.class)
      {
        Class[] parameters = method.getParameterTypes();
        return (parameters.length == 1) && (parameters[0] == String[].class);
      }
      else
      {
        return false;
      }
    }
    else
    {
      return false;
    }
  }

  static ResponseType mapResponseType(Method method, RouteConfiguration routeConfiguration)
  {
    Class returnType = method.getReturnType();
    if ((returnType == void.class) || (returnType == Void.class))
    {
      return ResponseType.VOID;
    }
    else if (returnType.isArray() && (returnType.getComponentType() == byte.class))
    {
      return ResponseType.BYTES_CONTENT;
    }
    else if (returnType == InputStream.class)
    {
      return ResponseType.STREAM_CONTENT;
    }
    else if ((returnType == String.class) && !routeConfiguration.returnedStringIsContent)
    {
      return ResponseType.FORWARD_DISPATCH;
    }
    else
    {
      return ResponseType.STRING_CONTENT;
    }
  }
}
