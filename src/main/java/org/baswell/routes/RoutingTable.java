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

import org.baswell.routes.criteria.RouteCriteria;
import org.baswell.routes.criteria.RouteCriteriaBuilder;
import org.baswell.routes.invoking.RouteMethodParameter;
import org.baswell.routes.invoking.RouteMethodParametersBuilder;
import org.baswell.routes.parsing.RouteParser;
import org.baswell.routes.parsing.RouteTree;
import org.baswell.routes.response.RouteResponseType;
import org.baswell.routes.response.RouteResponseTypeMapper;

public class RoutingTable
{
  public static RoutingTable routingTable;

  final RoutesConfig routesConfig;
  
  private List<Object> addedObjects = new ArrayList<Object>();
  
  private List<RouteNode> routeNodes;

  public RoutingTable()
  {
    this(new RoutesConfig());
  }

  public RoutingTable(RoutesConfig routesConfig)
  {
    this.routesConfig = routesConfig == null ? new RoutesConfig() : routesConfig;
    RoutingTable.routingTable = this;
  }

  public List<RouteNode> getRouteNodes()
  {
    return new ArrayList<RouteNode>(routeNodes);
  }

  public RoutingTable add(Object... instancesOrClasses)
  {
    for (Object obj : instancesOrClasses) addedObjects.add(obj);
    return this;
  }
  
  public void build() throws InvalidRouteException
  {
    RouteParser parser = new RouteParser();
    RouteCriteriaBuilder criteriaBuilder = new RouteCriteriaBuilder();
    RouteMethodParametersBuilder parametersBuilder = new RouteMethodParametersBuilder();
    RouteResponseTypeMapper returnTypeMapper = new RouteResponseTypeMapper();

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
        routeUnannotatedPublicMethods = routesConfig.routeUnannoatedPublicMethods;
      }
      else
      {
        numRoutesPaths = Math.max(1, routesAnnotation.value().length);
        routeUnannotatedPublicMethods = routesAnnotation.routeUnannoatedPublicMethods().length == 0 ? routesConfig.routeUnannoatedPublicMethods : routesAnnotation.routeUnannoatedPublicMethods()[0];
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
            RouteConfig routeConfig = new RouteConfig(method, routesConfig, routesAnnotation, routeAnnotation, i);
            RouteTree tree = parser.parse(routeConfig.route);
            RouteInstance routeInstance = instanceIsClass ? new RouteInstance(routesClass, routesConfig.routeInstanceFactory) : new RouteInstance(addedObject);
            RouteCriteria criteria = criteriaBuilder.buildCriteria(method, tree, routeConfig, routesConfig);
            List<RouteMethodParameter> parameters = parametersBuilder.buildParameters(method, criteria);
            RouteResponseType responseType = returnTypeMapper.mapResponseType(method, routeConfig);

            List<BeforeRouteNode> beforeNodes = new ArrayList<BeforeRouteNode>();
            for (BeforeRouteNode beforeNode : classBeforeNodes)
            {
              if ((beforeNode.onlyTags.isEmpty() || containsOne(beforeNode.onlyTags, routeConfig.tags)) && (beforeNode.exceptTags.isEmpty() || !containsOne(beforeNode.exceptTags, routeConfig.tags)))
              {
                beforeNodes.add(beforeNode);
              }
            }

            List<AfterRouteNode> afterNodes = new ArrayList<AfterRouteNode>();
            for (AfterRouteNode afterNode : classAfterNodes)
            {
              if ((afterNode.onlyTags.isEmpty() || containsOne(afterNode.onlyTags, routeConfig.tags)) && (afterNode.exceptTags.isEmpty() || !containsOne(afterNode.exceptTags, routeConfig.tags)))
              {
                afterNodes.add(afterNode);
              }
            }

            classRoutes.add(new RouteNode(routeNodes.size(), method, routeConfig, routeInstance, criteria, parameters, responseType, beforeNodes, afterNodes));
          }
        }
      }

      if (!classRoutes.isEmpty())
      {
        routeNodes.addAll(classRoutes);
      }
      else
      {
        throw new InvalidRouteException("Route class: " + routesClass + " has no routes.");
      }

    }
    Collections.sort(routeNodes);
  }

  MatchedRoute find(RequestPath path, RequestParameters parameters, HttpMethod httpMethod, Format format)
  {
    List<Matcher> pathMatchers = new ArrayList<Matcher>();
    Map<String, Matcher> parameterMatchers = new HashMap<String, Matcher>();
    for (RouteNode routeNode : routeNodes)
    {
      pathMatchers.clear();
      parameterMatchers.clear();
      if (routeNode.criteria.matches(httpMethod, format, path, parameters, pathMatchers, parameterMatchers))
      {
        return new MatchedRoute(routeNode, pathMatchers, parameterMatchers);
      }
    }
    return null;
  }
  
  
  
  static List<BeforeRouteNode> getBeforeRouteNodes(Class clazz) throws InvalidRoutesMethodDeclaration
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
          List<RouteMethodParameter> routeParameters = new RouteMethodParametersBuilder().buildParameters(method);
          Integer order = beforeRoute.order().length == 0 ? null : beforeRoute.order()[0];
          nodes.add(new BeforeRouteNode(method, routeParameters, returnsBoolean, new HashSet<String>(Arrays.asList(beforeRoute.onlyTags())), new HashSet<String>(Arrays.asList(beforeRoute.exceptTags())), order));
        }
        else
        {
          throw new InvalidRoutesMethodDeclaration("Before method: " + method + " must have return type of boolean or void.");
        } 
      }
    }

    Collections.sort(nodes);
    return nodes;
  }

  static List<AfterRouteNode> getAfterRouteNodes(Class clazz) throws InvalidRoutesMethodDeclaration
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
          List<RouteMethodParameter> routeParameters = new RouteMethodParametersBuilder().buildParameters(method);
          Integer order = afterRoute.order().length == 0 ? null : afterRoute.order()[0];
          nodes.add(new AfterRouteNode(method, routeParameters, new HashSet<String>(Arrays.asList(afterRoute.onlyTags())), new HashSet<String>(Arrays.asList(afterRoute.exceptTags())), order));
        }
        else
        {
          throw new InvalidRoutesMethodDeclaration("After method: " + method + " must have void return type.");
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
