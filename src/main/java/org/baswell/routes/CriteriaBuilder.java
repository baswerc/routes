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

import static org.baswell.routes.RoutesMethods.*;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.baswell.routes.CriterionForParameter.RequestParameterType;
import org.baswell.routes.CriterionForPathSegment.RequestPathSegmentCrierionType;

class CriteriaBuilder
{
  RouteCriteria buildCriteria(Method method, ParsedRouteTree routeTree, RouteConfiguration routeConfiguration, RoutesConfiguration routesConfiguration) throws RoutesException
  {
    List<CriterionForPathSegment> pathCriteria = new ArrayList<CriterionForPathSegment>();
    int urlParameterIndex = 0;
    for (int i = 0; i < routeTree.pathTerminals.size(); i++)
    {
      ParsedPathTerminal pathTerminal = routeTree.pathTerminals.get(i);
      if (pathTerminal instanceof ParsedExactPathTerminal)
      {
        ParsedExactPathTerminal exactTerminal = (ParsedExactPathTerminal)pathTerminal;
        pathCriteria.add(new CriterionForPathSegment(i, exactTerminal.segment, RequestPathSegmentCrierionType.FIXED, null));
      }
      else if (pathTerminal instanceof ParsedPatternPathTerminal)
      {
        ParsedPatternPathTerminal patternTerminal = (ParsedPatternPathTerminal)pathTerminal;
        CriterionForPathSegment patternCriteria = new CriterionForPathSegment(i, patternTerminal.pattern, RequestPathSegmentCrierionType.PATTERN, compile(patternTerminal.pattern, method));
        urlParameterIndex += Math.min(1, patternCriteria.numberPatternGroups);
        pathCriteria.add(patternCriteria);
      }
      else if (pathTerminal instanceof ParsedWildcardPathTerminal)
      {
        ++urlParameterIndex;
        pathCriteria.add(new CriterionForPathSegment(i, "*", RequestPathSegmentCrierionType.PATTERN, compile(WILDCARD_PATTERN, method)));
      }
      else if (pathTerminal instanceof ParsedDoubleWildcardPathTerminal)
      {
        ++urlParameterIndex;
        pathCriteria.add(new CriterionForPathSegment(i, "**", RequestPathSegmentCrierionType.MULTI, null));
      }
      else if (pathTerminal instanceof ParsedMethodParameterPathTerminal)
      {
        RoutesPair<Type, Integer> parameterIndex = findDynamicParameterAndIndex(method, urlParameterIndex++);
        if (parameterIndex == null)
        {
          throw new RoutesException("Route path pattern {} at index: " + (urlParameterIndex - 1) + " has no matching method parameter for method: " + method);
        }
        else
        {
          Type parameter = parameterIndex.x;
          if (typesToPatterns.containsKey(parameter))
          {
            String pattern = typesToPatterns.get(parameter);
            pathCriteria.add(new CriterionForPathSegment(i, pattern, RequestPathSegmentCrierionType.PATTERN, compile(pattern, method), 0));
          }
          else
          {
            throw new RoutesException("Invalid route parameter: " + parameter + " for method: " + method);
          }
        }
      }
      else if (pathTerminal instanceof ParsedSymbolPathTerminal)
      {
        ParsedSymbolPathTerminal symbolTerminal = (ParsedSymbolPathTerminal)pathTerminal;
        if (routesConfiguration.symbolsToPatterns.containsKey(symbolTerminal.symbol))
        {
          CriterionForPathSegment patternCriteria = new CriterionForPathSegment(i, symbolTerminal.symbol, RequestPathSegmentCrierionType.PATTERN, routesConfiguration.symbolsToPatterns.get(symbolTerminal.symbol));
          urlParameterIndex += Math.min(1, patternCriteria.numberPatternGroups);
          pathCriteria.add(patternCriteria);
        }
        else
        {
          throw new RoutesException("Invalid symbol: " + symbolTerminal.symbol + " in method: " + method);
        }
      }
      else
      {
        throw new RoutesException("Unsupported PathTerminal class: " + pathTerminal.getClass());
      }
    }
    
    List<CriterionForParameter> parameterCriteria = new ArrayList<CriterionForParameter>();
    if (routeTree.parameterTerminals != null)
    {
      for (int i = 0; i < routeTree.parameterTerminals.size(); i++)
      {
        ParsedParameterTerminal parameterTerminal = routeTree.parameterTerminals.get(i);
        
        if (parameterTerminal instanceof ParsedExactParameterTerminal)
        {
          ParsedExactParameterTerminal exactTerminal = (ParsedExactParameterTerminal)parameterTerminal;
          parameterCriteria.add(new CriterionForParameter(exactTerminal.name, exactTerminal.value, RequestParameterType.FIXED, !parameterTerminal.optional, null));
        }
        else if (parameterTerminal instanceof ParsedPatternParameterTerminal)
        {
          ParsedPatternParameterTerminal patternParameter = (ParsedPatternParameterTerminal)parameterTerminal;
          CriterionForParameter parameterCriterion = new CriterionForParameter(patternParameter.name, patternParameter.pattern, RequestParameterType.PATTERN, !parameterTerminal.optional, compile(patternParameter.pattern, method));
          urlParameterIndex += Math.min(1, parameterCriterion.numberPatternGroups);
          parameterCriteria.add(parameterCriterion);
        }
        else if (parameterTerminal instanceof ParsedWildcardParameterTerminal)
        {
          ++urlParameterIndex;
          parameterCriteria.add(new CriterionForParameter(parameterTerminal.name, "*", RequestParameterType.PATTERN, !parameterTerminal.optional, compile(WILDCARD_PATTERN, method)));
        }
        else if (parameterTerminal instanceof ParsedMethodParameterParameterTerminal)
        {
          RoutesPair<Type, Integer> parameterIndex = findDynamicParameterAndIndex(method, urlParameterIndex++);
          if (parameterIndex == null)
          {
            throw new RoutesException("Route parameter pattern {} at index: " + (urlParameterIndex - 1) + " has no matching method parameter for method: " + method);
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
              parameterCriteria.add(new CriterionForParameter(parameterTerminal.name, pattern, RequestParameterType.PATTERN, !parameterTerminal.optional, compile(pattern, method), 0));
            }
            else
            {
              throw new RoutesException("Invalid route parameter: " + parameter + " for method: " + method);
            }
          }
        }
        else if (parameterTerminal instanceof ParsedSymbolParameterTerminal)
        {
          ParsedSymbolParameterTerminal symbolTerminal = (ParsedSymbolParameterTerminal)parameterTerminal;
          if (routesConfiguration.symbolsToPatterns.containsKey(symbolTerminal.symbol))
          {
            CriterionForParameter parameterCriterion = new CriterionForParameter(parameterTerminal.name, "", RequestParameterType.PATTERN, !parameterTerminal.optional, routesConfiguration.symbolsToPatterns.get(symbolTerminal.symbol));
            urlParameterIndex += Math.min(1, parameterCriterion.numberPatternGroups);
            parameterCriteria.add(parameterCriterion);
          }
          else
          {
            throw new RoutesException("Invalid parameter symbol: " + symbolTerminal.symbol + " in method: " + method);
          }
        }
        else
        {
          throw new RoutesException("Unsupported ParameterTerminal class: " + parameterTerminal.getClass());
        }
      }
    }

    List<Pattern> acceptTypePattern = new ArrayList<>();
    if (routeConfiguration.acceptTypePatterns != null) {
      for (int i = 0; i < routeConfiguration.acceptTypePatterns.size(); i++) {
        try
        {
          acceptTypePattern.add(Pattern.compile(routeConfiguration.acceptTypePatterns.get(i)));
        }
        catch (Exception e)
        {
          throw new RoutesException("Invalid acceptTypePattern: " + acceptTypePattern + " for method: " + method, e);
        }
      }
    }

    return new RouteCriteria(routeConfiguration.httpMethods, pathCriteria, parameterCriteria, acceptTypePattern, routeConfiguration, routesConfiguration);
  }
  
  static Pattern compile(String pattern, Method method)
  {
    try
    {
      return Pattern.compile(pattern, Pattern.DOTALL); // Support multi-lines for parameters
    }
    catch (PatternSyntaxException e)
    {
      throw new RoutesException("Invalid pattern: " + pattern + " for method: " + method);
    }
  }
  
  static RoutesPair<Type, Integer> findDynamicParameterAndIndex(Method method, int dynamicIndex)
  {
    Type[] parameters = method.getGenericParameterTypes();
    int dynamicParameterCounter = 0;
    for (int i = 0; i < parameters.length; i++)
    {
      Type parameter = parameters[i];
      if (!methodRouteParameterTypes.contains(parameter))
      {
        if (dynamicParameterCounter++ == dynamicIndex)
        {
          return RoutesPair.pair(parameter, i);
        }
      }
    }
    return null;
  }
}
