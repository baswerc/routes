package org.baswell.routes.criteria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.baswell.routes.Format;
import org.baswell.routes.HttpMethod;
import org.baswell.routes.RequestParameters;
import org.baswell.routes.RequestPath;
import org.baswell.routes.RouteConfig;
import org.baswell.routes.RoutesConfig;
import org.baswell.routes.criteria.RequestPathSegmentCriterion.RequestPathSegmentCrierionType;

public class RouteCriteria implements Comparable<RouteCriteria>
{
  public final List<RequestPathSegmentCriterion> pathCriteria;
  
  public final List<RequestParameterCriterion> parameterCriteria;

  final RouteConfig routeConfig;
  
  final RoutesConfig routesConfig;

  final boolean allCriteriaFixed;

  final boolean hasPattern;
  
  final boolean hasMultiPathCriterion;
  
  public RouteCriteria(List<RequestPathSegmentCriterion> pathCriteria, List<RequestParameterCriterion> parameterCriteria, RouteConfig routeConfig, RoutesConfig routesConfig)
  {
    this.pathCriteria = pathCriteria;
    this.parameterCriteria = parameterCriteria;
    this.routeConfig = routeConfig;
    this.routesConfig = routesConfig;

    boolean hasPattern = false;
    boolean hasMultiPathCriterion = false;
    if (pathCriteria != null)
    {
      for (RequestPathSegmentCriterion pathCriterion : pathCriteria)
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

  public boolean matches(HttpMethod httpMethod, Format format, RequestPath path, RequestParameters parameters)
  {
    return matches(httpMethod, format, path, parameters, new ArrayList<Matcher>(), new HashMap<String, Matcher>());
  }

  public boolean matches(HttpMethod httpMethod, Format format, RequestPath path, RequestParameters parameters, List<Matcher> pathMatchers, Map<String, Matcher> parameterMatchers)
  {
    if (!routeConfig.httpMethods.contains(httpMethod))
    {
      return false;
    }
    else if (!routeConfig.acceptedFormats.isEmpty() && !routeConfig.acceptedFormats.contains(format.type))
    {
      return false;
    }
    else if (!hasMultiPathCriterion && (path.size() != pathCriteria.size()))
    {
      return false;
    }
    else if (!matchSegments(0, path, 0, pathCriteria, routesConfig, pathMatchers))
    {
      return false;
    }
   
    if (parameterCriteria != null)
    {
      for (RequestParameterCriterion parameterCriterion : parameterCriteria)
      {
        List<String> parameterValues = parameters.getValues(parameterCriterion.name);
        
        if (parameterValues.isEmpty() && parameterCriterion.presenceRequired)
        {
          return false;
        }
        
        if (routesConfig.caseInsensitive)
        {
          for (int i = 0; i < parameterValues.size(); i++)
          {
            parameterValues.set(i, parameterValues.get(i).toLowerCase());
          }
        }

        switch (parameterCriterion.type)
        {
          case FIXED:
            String value = routesConfig.caseInsensitive ? parameterCriterion.value.toLowerCase() : parameterCriterion.value;
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
    
    return true;
  }

  @Override
  public int compareTo(RouteCriteria routeCriteria)
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

  static boolean matchSegments(int pathIndex, RequestPath path, int criteriaIndex, List<RequestPathSegmentCriterion> criteria, RoutesConfig config, List<Matcher> matchers)
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
      RequestPathSegmentCriterion criterion = criteria.get(criteriaIndex);
      
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
