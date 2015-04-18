package org.baswell.routes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.baswell.routes.CriterionForPathSegment.RequestPathSegmentCrierionType;

class Criteria implements Comparable<Criteria>
{
  final List<CriterionForPathSegment> pathCriteria;
  
  final List<CriterionForParameter> parameterCriteria;

  final RouteConfiguration routeConfiguration;
  
  final RoutesConfiguration routesConfiguration;

  final boolean allCriteriaFixed;

  final boolean hasPattern;
  
  final boolean hasMultiPathCriterion;
  
  Criteria(List<CriterionForPathSegment> pathCriteria, List<CriterionForParameter> parameterCriteria, RouteConfiguration routeConfiguration, RoutesConfiguration routesConfiguration)
  {
    this.pathCriteria = pathCriteria;
    this.parameterCriteria = parameterCriteria;
    this.routeConfiguration = routeConfiguration;
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

  boolean matches(HttpMethod httpMethod, RequestFormat requestFormat, RequestPath path, RequestParameters parameters)
  {
    return matches(httpMethod, requestFormat, path, parameters, new ArrayList<Matcher>(), new HashMap<String, Matcher>());
  }

  boolean matches(HttpMethod httpMethod, RequestFormat requestFormat, RequestPath path, RequestParameters parameters, List<Matcher> pathMatchers, Map<String, Matcher> parameterMatchers)
  {
    if (!routeConfiguration.respondsToMethods.contains(httpMethod))
    {
      return false;
    }
    else if (!routeConfiguration.respondsToMedia.isEmpty() && !routeConfiguration.respondsToMedia.contains(requestFormat.mediaType))
    {
      return false;
    }
    else if (!hasMultiPathCriterion && (path.size() != pathCriteria.size()))
    {
      return false;
    }
    else if (!matchSegments(0, path, 0, pathCriteria, routesConfiguration, pathMatchers))
    {
      return false;
    }
   
    if (parameterCriteria != null)
    {
      for (CriterionForParameter parameterCriterion : parameterCriteria)
      {
        List<String> parameterValues = parameters.getValues(parameterCriterion.name);

        if (parameterValues.isEmpty() && routeConfiguration.defaultParameters.containsKey(parameterCriterion.name))
        {
          parameterValues.addAll(routeConfiguration.defaultParameters.get(parameterCriterion.name));
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

  @Override
  public int compareTo(Criteria routeCriteria)
  {
    if (allCriteriaFixed && !routeCriteria.allCriteriaFixed)
    {
      return -1;
    }
    else if (!allCriteriaFixed && routeCriteria.allCriteriaFixed)
    {
      return 1;
    }
    else
    {
      return 0;
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
