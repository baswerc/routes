package org.baswell.routes.criteria;

import static org.baswell.routes.utils.RoutesMethods.*;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.baswell.routes.RouteConfig;
import org.baswell.routes.RoutesConfig;
import org.baswell.routes.criteria.RequestParameterCriterion.RequestParameterType;
import org.baswell.routes.criteria.RequestPathSegmentCriterion.RequestPathSegmentCrierionType;
import org.baswell.routes.parsing.DoubleWildcardPathTerminal;
import org.baswell.routes.parsing.ExactParameterTerminal;
import org.baswell.routes.parsing.ExactPathTerminal;
import org.baswell.routes.parsing.MethodParameterParameterTerminal;
import org.baswell.routes.parsing.MethodParameterPathTerminal;
import org.baswell.routes.parsing.ParameterTerminal;
import org.baswell.routes.parsing.PathTerminal;
import org.baswell.routes.parsing.PatternParameterTerminal;
import org.baswell.routes.parsing.PatternPathTerminal;
import org.baswell.routes.parsing.RouteTree;
import org.baswell.routes.parsing.SymbolParameterTerminal;
import org.baswell.routes.parsing.SymbolPathTerminal;
import org.baswell.routes.parsing.WildcardParameterTerminal;
import org.baswell.routes.parsing.WildcardPathTerminal;
import org.baswell.routes.utils.Pair;

public class RouteCriteriaBuilder
{
  public RouteCriteria buildCriteria(Method method, RouteTree routeTree, RouteConfig routeConfig, RoutesConfig routesConfig) throws InvalidRouteException
  {
    List<RequestPathSegmentCriterion> pathCriteria = new ArrayList<RequestPathSegmentCriterion>();
    int urlParameterIndex = 0;
    for (int i = 0; i < routeTree.pathTerminals.size(); i++)
    {
      PathTerminal pathTerminal = routeTree.pathTerminals.get(i);
      if (pathTerminal instanceof ExactPathTerminal)
      {
        ExactPathTerminal exactTerminal = (ExactPathTerminal)pathTerminal;
        pathCriteria.add(new RequestPathSegmentCriterion(i, exactTerminal.segment, RequestPathSegmentCrierionType.FIXED, null));
      }
      else if (pathTerminal instanceof PatternPathTerminal)
      {
        PatternPathTerminal patternTerminal = (PatternPathTerminal)pathTerminal;
        RequestPathSegmentCriterion patternCriteria = new RequestPathSegmentCriterion(i, patternTerminal.pattern, RequestPathSegmentCrierionType.PATTERN, compile(patternTerminal.pattern, method));
        urlParameterIndex += Math.min(1, patternCriteria.numberPatternGroups);
        pathCriteria.add(patternCriteria);
      }
      else if (pathTerminal instanceof WildcardPathTerminal)
      {
        ++urlParameterIndex;
        pathCriteria.add(new RequestPathSegmentCriterion(i, "*", RequestPathSegmentCrierionType.PATTERN, compile(WILDCARD_PATTERN, method)));
      }
      else if (pathTerminal instanceof DoubleWildcardPathTerminal)
      {
        ++urlParameterIndex;
        pathCriteria.add(new RequestPathSegmentCriterion(i, "**", RequestPathSegmentCrierionType.MULTI, null));
      }
      else if (pathTerminal instanceof MethodParameterPathTerminal)
      {
        Pair<Type, Integer> parameterIndex = findDynamicParameterAndIndex(method, urlParameterIndex++);
        if (parameterIndex == null)
        {
          throw new InvalidRouteException("Route path pattern {} at index: " + (urlParameterIndex - 1) + " has no matching method parameter for method: " + method);
        }
        else
        {
          Type parameter = parameterIndex.x;
          if (typesToPatterns.containsKey(parameter))
          {
            String pattern = typesToPatterns.get(parameter);
            pathCriteria.add(new RequestPathSegmentCriterion(i, pattern, RequestPathSegmentCrierionType.PATTERN, compile(pattern, method)));
          }
          else
          {
            throw new InvalidRouteException("Invalid route parameter: " + parameter + " for method: " + method);
          }
        }
      }
      else if (pathTerminal instanceof SymbolPathTerminal)
      {
        SymbolPathTerminal symbolTerminal = (SymbolPathTerminal)pathTerminal;
        if (routesConfig.symbolsToPatterns.containsKey(symbolTerminal.symbol))
        {
          RequestPathSegmentCriterion patternCriteria = new RequestPathSegmentCriterion(i, symbolTerminal.symbol, RequestPathSegmentCrierionType.PATTERN, routesConfig.symbolsToPatterns.get(symbolTerminal.symbol));
          urlParameterIndex += Math.min(1, patternCriteria.numberPatternGroups);
          pathCriteria.add(patternCriteria);
        }
        else
        {
          throw new InvalidRouteException("Invalid symbol: " + symbolTerminal.symbol + " in method: " + method);
        }
      }
      else
      {
        throw new InvalidRouteException("Unsupported PathTerminal class: " + pathTerminal.getClass());
      }
    }
    
    List<RequestParameterCriterion> parameterCriteria = new ArrayList<RequestParameterCriterion>();
    if (routeTree.parameterTerminals != null)
    {
      for (int i = 0; i < routeTree.parameterTerminals.size(); i++)
      {
        ParameterTerminal parameterTerminal = routeTree.parameterTerminals.get(i);
        
        if (parameterTerminal instanceof ExactParameterTerminal)
        {
          ExactParameterTerminal exactTerminal = (ExactParameterTerminal)parameterTerminal;
          parameterCriteria.add(new RequestParameterCriterion(exactTerminal.name, exactTerminal.value, RequestParameterType.FIXED, !parameterTerminal.optional, null));
        }
        else if (parameterTerminal instanceof PatternParameterTerminal)
        {
          PatternParameterTerminal patternParameter = (PatternParameterTerminal)parameterTerminal;
          RequestParameterCriterion parameterCriterion = new RequestParameterCriterion(patternParameter.name, patternParameter.pattern, RequestParameterType.PATTERN, !parameterTerminal.optional, compile(patternParameter.pattern, method));
          urlParameterIndex += Math.min(1, parameterCriterion.numberPatternGroups);
          parameterCriteria.add(parameterCriterion);
        }
        else if (parameterTerminal instanceof WildcardParameterTerminal)
        {
          ++urlParameterIndex;
          parameterCriteria.add(new RequestParameterCriterion(parameterTerminal.name, "*", RequestParameterType.PATTERN, !parameterTerminal.optional, compile(WILDCARD_PATTERN, method)));
        }
        else if (parameterTerminal instanceof MethodParameterParameterTerminal)
        {
          Pair<Type, Integer> parameterIndex = findDynamicParameterAndIndex(method, urlParameterIndex++);
          if (parameterIndex == null)
          {
            throw new InvalidRouteException("Route parameter pattern {} at index: " + (urlParameterIndex - 1) + " has no matching method parameter for method: " + method);
          }
          else
          {
            Type parameter = parameterIndex.x;
            Class parameterClass = typeToClass(parameter);
            if (parameterClass == List.class)
            {
              parameterClass = getListSingleParameterType(parameter);
            }
            
            if ((parameterClass != null) && typesToPatterns.containsKey(parameterClass))
            {
              String pattern = typesToPatterns.get(parameterClass);
              parameterCriteria.add(new RequestParameterCriterion(parameterTerminal.name, pattern, RequestParameterType.PATTERN, !parameterTerminal.optional, compile(pattern, method)));
            }
            else
            {
              throw new InvalidRouteException("Invalid route parameter: " + parameter + " for method: " + method);
            }
          }
        }
        else if (parameterTerminal instanceof SymbolParameterTerminal)
        {
          SymbolParameterTerminal symbolTerminal = (SymbolParameterTerminal)parameterTerminal;
          if (routesConfig.symbolsToPatterns.containsKey(symbolTerminal.symbol))
          {
            RequestParameterCriterion parameterCriterion = new RequestParameterCriterion(parameterTerminal.name, "", RequestParameterType.PATTERN, !parameterTerminal.optional, routesConfig.symbolsToPatterns.get(symbolTerminal.symbol));
            urlParameterIndex += Math.min(1, parameterCriterion.numberPatternGroups);
            parameterCriteria.add(parameterCriterion);
          }
          else
          {
            throw new InvalidRouteException("Invalid parameter symbol: " + symbolTerminal.symbol + " in method: " + method);
          }
        }
        else
        {
          throw new InvalidRouteException("Unsupported ParameterTerminal class: " + parameterTerminal.getClass());
        }
      }
    }

    return new RouteCriteria(pathCriteria, parameterCriteria, routeConfig, routesConfig);
  }
  
  static Pattern compile(String pattern, Method method)
  {
    try
    {
      return Pattern.compile(pattern);
    }
    catch (PatternSyntaxException e)
    {
      throw new InvalidRouteException("Invalid pattern: " + pattern + " for method: " + method);
    }
  }
  
  static Pair<Type, Integer> findDynamicParameterAndIndex(Method method, int dynamicIndex)
  {
    Type[] parameters = method.getGenericParameterTypes();
    int dynamicParameterCounter = 0;
    for (int i = 0; i < parameters.length; i++)
    {
      Type parameter = parameters[i];
      if (!routesParameterTypes.contains(parameter))
      {
        if (dynamicParameterCounter++ == dynamicIndex)
        {
          return Pair.pair(parameter, i);
        }
      }
    }
    return null;
  }
}
