package org.baswell.routes;

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

import static org.baswell.routes.ResponseTypeMapper.*;
import static org.baswell.routes.RoutesMethods.*;

/**
 * Hub for all routes. The RoutingTable should be treated as a singleton and only constructed once in your application.
 *
 * Routes can be added to the RoutingTable in the following ways:
 *
 * 1. As class objects. Routes added as class objects are instantiated on each matched HTTP request. Once the HTTP request
 * is processed, the instantiated route object is discarded. Every HTTP request gets its own route object instance. The
 * route class does not have to be thread safe.
 *
 * 2. As instances of the route class. In this case the same route object will get used for every matched HTTP request. The
 * route class must be thread safe as it can process multiple HTTP requests concurrently.
 *
 * After
 */
public class RoutingTable
{
  static RoutingTable theRoutingTable;

  final RoutesConfiguration routesConfiguration;

  volatile boolean built;

  private List<Object> addedObjects = new ArrayList<Object>();
  
  private List<RouteNode> routeNodes;

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
   * Adds to the given objects to the routing table. Objects can be either the route class object or a route instance
   * object.
   *
   * @param instancesOrClasses Route class or instance objects.
   * @return This RoutingTable
   */
  public synchronized RoutingTable add(Object... instancesOrClasses)
  {
    built = false;
    for (Object obj : instancesOrClasses) addedObjects.add(obj);
    return this;
  }

  /**
   * Builds the routing table from the added route objects. This will typically only be called once after all route objects
   * have been added in your application's bootstrap.
   *
   * @throws RoutesException If an added route class cannot be parsed.
   */
  public synchronized void build() throws RoutesException
  {
    Parser parser = new Parser();
    CriteriaBuilder criteriaBuilder = new CriteriaBuilder();
    MethodParametersBuilder parametersBuilder = new MethodParametersBuilder();
    AvailableLibraries availableLibraries = new AvailableLibraries();

    routeNodes = new ArrayList<RouteNode>();
    for (Object addedObject : addedObjects)
    {
      boolean instanceIsClass = (addedObject instanceof Class);
      Class routesClass = instanceIsClass ? (Class) addedObject : addedObject.getClass();

      List<BeforeRouteNode> classBeforeNodes = getBeforeRouteNodes(routesClass);
      List<AfterRouteNode> classAfterNodes = getAfterRouteNodes(routesClass);

      Routes routesAnnotation = (Routes) routesClass.getAnnotation(Routes.class);

      int numRoutesPaths;
      boolean routeUnannotatedPublicMethods;

      if (routesAnnotation == null)
      {
        numRoutesPaths = 1;
        routeUnannotatedPublicMethods = routesConfiguration.routeUnannotatedPublicMethods;
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
            RouteConfiguration routeConfiguration = new RouteConfiguration(method, routesConfiguration, routesAnnotation, routeAnnotation, i);
            ParsedRouteTree tree = parser.parse(routeConfiguration.route);
            RouteInstance routeInstance = instanceIsClass ? new RouteInstance(routesClass, routesConfiguration.routeInstanceFactory) : new RouteInstance(addedObject);
            Criteria criteria = criteriaBuilder.buildCriteria(method, tree, routeConfiguration, routesConfiguration);
            List<MethodParameter> parameters = parametersBuilder.buildParameters(method, criteria);
            ResponseType responseType = mapResponseType(method, routeConfiguration);
            ResponseStringWriteStrategy responseStringWriteStrategy;
            if (responseType == ResponseType.STRING_CONTENT)
            {
              Pair<ResponseStringWriteStrategy, String> stringWriteStrategyStringPair = mapResponseStringWriteStrategy(method, routeConfiguration.respondsToMedia, routeConfiguration.contentType, availableLibraries);
              if (stringWriteStrategyStringPair == null)
              {
                responseStringWriteStrategy = null;
              }
              else
              {
                responseStringWriteStrategy = stringWriteStrategyStringPair.x;
                if (nullEmpty(routeConfiguration.contentType))
                {
                  routeConfiguration.contentType = stringWriteStrategyStringPair.y;
                }
              }
            }
            else
            {
              responseStringWriteStrategy = null;
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

            classRoutes.add(new RouteNode(routeNodes.size(), method, routeConfiguration, routeInstance, criteria, parameters, responseType, responseStringWriteStrategy, beforeNodes, afterNodes));
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
    built = true;
  }

  MatchedRoute find(RequestPath path, RequestParameters parameters, HttpMethod httpMethod, RequestFormat requestFormat)
  {
    List<Matcher> pathMatchers = new ArrayList<Matcher>();
    Map<String, Matcher> parameterMatchers = new HashMap<String, Matcher>();
    for (RouteNode routeNode : routeNodes)
    {
      pathMatchers.clear();
      parameterMatchers.clear();
      if (routeNode.criteria.matches(httpMethod, requestFormat, path, parameters, pathMatchers, parameterMatchers))
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
    List<BeforeRouteNode> nodes = new ArrayList<BeforeRouteNode>();
    for (Method method : clazz.getMethods())
    {
      BeforeRoute beforeRoute = method.getAnnotation(BeforeRoute.class);
      if (beforeRoute != null)
      {
        Class returnType = method.getReturnType();
        if ((returnType == boolean.class) || (returnType == Boolean.class) || (returnType == void.class))
        {
          boolean returnsBoolean = returnType != void.class;
          List<MethodParameter> routeParameters = new MethodParametersBuilder().buildParameters(method);
          Integer order = beforeRoute.order().length == 0 ? null : beforeRoute.order()[0];
          nodes.add(new BeforeRouteNode(method, routeParameters, returnsBoolean, new HashSet<String>(Arrays.asList(beforeRoute.onlyTags())), new HashSet<String>(Arrays.asList(beforeRoute.exceptTags())), order));
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
    List<AfterRouteNode> nodes = new ArrayList<AfterRouteNode>();
    for (Method method : clazz.getMethods())
    {
      AfterRoute afterRoute = method.getAnnotation(AfterRoute.class);
      if (afterRoute != null)
      {
        Class returnType = method.getReturnType();
        if (returnType == void.class)
        {
          List<MethodParameter> routeParameters = new MethodParametersBuilder().buildParameters(method);
          Integer order = afterRoute.order().length == 0 ? null : afterRoute.order()[0];
          nodes.add(new AfterRouteNode(method, routeParameters, new HashSet<String>(Arrays.asList(afterRoute.onlyTags())), new HashSet<String>(Arrays.asList(afterRoute.exceptTags())), order));
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
}
