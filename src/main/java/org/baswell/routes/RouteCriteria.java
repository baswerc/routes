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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.baswell.routes.CriterionForPathSegment.RequestPathSegmentCrierionType;

import static org.baswell.routes.RoutesMethods.*;

class RouteCriteria implements Comparable<RouteCriteria>
{
  final List<HttpMethod> httpMethods;

  final List<Pattern> acceptTypePatterns;

  final List<CriterionForPathSegment> pathCriteria;
  
  final List<CriterionForParameter> parameterCriteria;

  final RouteData routeData;
  
  final RoutesConfiguration routesConfiguration;

  final boolean allCriteriaFixed;

  final boolean hasPattern;
  
  final boolean hasMultiPathCriterion;
  
  RouteCriteria(List<HttpMethod> httpMethods, List<CriterionForPathSegment> pathCriteria, List<CriterionForParameter> parameterCriteria, List<Pattern> acceptTypePatterns, RouteData routeData, RoutesConfiguration routesConfiguration)
  {
    this.httpMethods = httpMethods;
    this.acceptTypePatterns = acceptTypePatterns;
    this.pathCriteria = pathCriteria;
    this.parameterCriteria = parameterCriteria;
    this.routeData = routeData;
    this.routesConfiguration = routesConfiguration;

    boolean hasPattern = false;
    boolean hasMultiPathCriterion = false;
    if (pathCriteria != null)
    {
      for (CriterionForPathSegment pathCriterion : pathCriteria)
      {
        if (pathCriterion.type == RequestPathSegmentCrierionType.PATTERN)
        {
          hasPattern = true;
        }
        else if (pathCriterion.type == RequestPathSegmentCrierionType.MULTI)
        {
          hasMultiPathCriterion = true;
        }
      }
    }
    this.hasPattern = hasPattern;
    this.hasMultiPathCriterion = hasMultiPathCriterion;
    this.allCriteriaFixed = !this.hasPattern && !this.hasMultiPathCriterion;
  }

  boolean matches(HttpMethod httpMethod, RequestedMediaType requestedMediaType, RequestPath path, RequestParameters parameters)
  {
    return matches(httpMethod, requestedMediaType, path, parameters, new ArrayList<Matcher>(), new HashMap<String, Matcher>());
  }

  boolean matches(HttpMethod httpMethod, RequestedMediaType requestedMediaType, RequestPath path, RequestParameters parameters, List<Matcher> pathMatchers, Map<String, Matcher> parameterMatchers)
  {

    if (!httpMethods.contains(httpMethod)) {
      return false;
    }

    if (!acceptTypePatterns.isEmpty()) {
      boolean oneMatched = false;
      for (int i = 0; i < acceptTypePatterns.size(); i++) {
        if (acceptTypePatterns.get(i).matcher(requestedMediaType.mimeType).matches()) {
          oneMatched = true;
          break;
        }
      }

      if (!oneMatched) {
        return false;
      }
    }

    if (!hasMultiPathCriterion && (path.size() != pathCriteria.size()))
    {
      return false;
    }

    if (!matchSegments(0, path, 0, pathCriteria, routesConfiguration, pathMatchers))
    {
      return false;
    }
   
    if (parameterCriteria != null)
    {
      for (CriterionForParameter parameterCriterion : parameterCriteria)
      {
        List<String> parameterValues = parameters.getValues(parameterCriterion.name);

        if (parameterValues.isEmpty() && routeData.defaultParameters.containsKey(parameterCriterion.name))
        {
          parameterValues.addAll(routeData.defaultParameters.get(parameterCriterion.name));
        }

        if (parameterValues.isEmpty() && parameterCriterion.presenceRequired)
        {
          return false;
        }

        if (!parameterValues.isEmpty())
        {
          if (routesConfiguration.caseInsensitive)
          {
            for (int i = 0; i < parameterValues.size(); i++)
            {
              parameterValues.set(i, parameterValues.get(i).toLowerCase());
            }
          }

          switch (parameterCriterion.type)
          {
            case FIXED:
              String value = routesConfiguration.caseInsensitive ? parameterCriterion.value.toLowerCase() : parameterCriterion.value;
              if (!parameterValues.contains(value))
              {
                return false;
              }
              break;

            case PATTERN:
              boolean matchFound = false;
              for (String parameterValue : parameterValues)
              {
                Matcher matcher = parameterCriterion.pattern.matcher(parameterValue);
                if (matcher.matches())
                {
                  parameterMatchers.put(parameterCriterion.name, matcher);
                  matchFound = true;
                  break;
                }
              }
              if (!matchFound)
              {
                return false;
              }
              break;
          }
        }
      }
    }
    
    return true;
  }

  /**
   * The more specific the criteria the earlier in the Route instance evaluation chain this criteria will go.
   */
  @Override
  public int compareTo(RouteCriteria other)
  {
    if (allCriteriaFixed && !other.allCriteriaFixed)
    {
      return -1;
    }
    else if (!allCriteriaFixed && other.allCriteriaFixed)
    {
      return 1;
    }
    else
    {
      if (acceptTypePatterns.size() == acceptTypePatterns.size())
      {
        int numberParameters = size(parameterCriteria);
        int otherNumberParameters = size(other.parameterCriteria);

        if (numberParameters > otherNumberParameters)
        {
          return -1;
        }
        else if (otherNumberParameters > numberParameters)
        {
          return 1;
        }
        else
        {
          return 0;
        }
      }
      else if (acceptTypePatterns.isEmpty())
      {
        return 1;
      }
      else if (acceptTypePatterns.isEmpty())
      {
        return -1;
      }
      else
      {
        return acceptTypePatterns.size() - other.acceptTypePatterns.size();
      }
    }
  }

  static boolean matchSegments(int pathIndex, RequestPath path, int criteriaIndex, List<CriterionForPathSegment> criteria, RoutesConfiguration config, List<Matcher> matchers)
  {
    if ((pathIndex >= path.size()) && (criteriaIndex >= criteria.size()))
    {
      return true;
    }
    else if ((pathIndex >= path.size()) && (criteriaIndex < criteria.size()))
    {
      for (int i = criteriaIndex; i < criteria.size(); i++)
      {
        if (criteria.get(i).type != RequestPathSegmentCrierionType.MULTI)
        {
          return false;
        }
      }
      return true;
    }
    else if ((criteriaIndex >= criteria.size()))
    {
      return false;
    }
    else
    {
      String segment = path.get(pathIndex);
      CriterionForPathSegment criterion = criteria.get(criteriaIndex);
      
      switch (criterion.type)
      {
        case FIXED:
          if (config.caseInsensitive && !segment.equalsIgnoreCase(criterion.value))
          {
            return false;
          }
          else if (!config.caseInsensitive && !segment.equals(criterion.value))
          {
            return false;
          }
          else
          {
            matchers.add(null);
            return matchSegments(pathIndex + 1, path, criteriaIndex + 1, criteria, config, matchers);
          }
  
        case PATTERN:
          Matcher matcher = criterion.pattern.matcher(segment);
          if (!matcher.matches())
          {
            return false;
          }
          else
          {
            matchers.add(matcher);
            return matchSegments(pathIndex + 1, path, criteriaIndex + 1, criteria, config, matchers);
          }
          
        case MULTI:
        default:
          if (criteriaIndex == criteria.size() - 1)
          {
            return true;
          }
          else
          {
            int nextCriteriaIndex = criteriaIndex + 1;
            for (int nextPathIndex = pathIndex; nextPathIndex < path.size(); nextPathIndex++)
            {
              List<Matcher> subMatchers = new ArrayList<Matcher>();
              subMatchers.add(null);
              if (matchSegments(nextPathIndex, path, nextCriteriaIndex, criteria, config, subMatchers))
              {
                matchers.addAll(subMatchers);
                return true;
              }
            }
            return false;
          }
      }
    }
  }
}
