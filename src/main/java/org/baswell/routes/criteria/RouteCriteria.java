package org.baswell.routes.criteria;

import java.util.List;

import org.baswell.routes.Format;
import org.baswell.routes.HttpMethod;
import org.baswell.routes.RequestParameters;
import org.baswell.routes.RequestPath;
import org.baswell.routes.RouteConfig;
import org.baswell.routes.RoutesConfig;
import org.baswell.routes.criteria.RequestPathSegmentCriterion.RequestPathSegmentCrierionType;

public class RouteCriteria implements Comparable<RouteCriteria>
{
  final List<RequestPathSegmentCriterion> pathCriteria;
  
  final List<RequestParameterCriterion> parameterCriteria;

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
    else if (!matchSegments(0, path, 0, pathCriteria, routesConfig))
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
              if (parameterCriterion.pattern.matcher(parameterValue).matches())
              {
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

  static boolean matchSegments(int pathIndex, RequestPath path, int criteriaIndex, List<RequestPathSegmentCriterion> criteria, RoutesConfig config)
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
            return matchSegments(pathIndex + 1, path, criteriaIndex + 1, criteria, config);
          }
  
        case PATTERN:
          if (!criterion.pattern.matcher(segment).matches())
          {
            return false;
          }
          else
          {
            return matchSegments(pathIndex + 1, path, criteriaIndex + 1, criteria, config);
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
              if (matchSegments(nextPathIndex, path, nextCriteriaIndex, criteria, config))
              {
                return true;
              }
            }
            return false;
          }
      }
    }
  }
}
