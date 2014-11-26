package org.baswell.routes.invoking;

import org.baswell.routes.Format;
import org.baswell.routes.InvalidRoutesMethodDeclaration;
import org.baswell.routes.RequestContext;
import org.baswell.routes.RequestParameters;
import org.baswell.routes.RequestPath;
import org.baswell.routes.criteria.InvalidRouteException;
import org.baswell.routes.invoking.RouteMethodParameter.RouteMethodParameterType;
import org.baswell.routes.parsing.ParameterTerminal;
import org.baswell.routes.parsing.PathTerminal;
import org.baswell.routes.parsing.RouteTree;
import org.baswell.routes.utils.RoutesMethods;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.baswell.routes.utils.RoutesMethods.typeToClass;

public class RouteMethodParametersBuilder
{
  public List<RouteMethodParameter> buildParameters(Method method)
  {
    return buildParameters(method, null);
  }
  
  public List<RouteMethodParameter> buildParameters(Method method, RouteTree routeTree) throws InvalidRouteException
  {
    List<RouteMethodParameter> routeParameters = new ArrayList<RouteMethodParameter>();
    Type[] parameters = method.getGenericParameterTypes();

    int routeIndex = 0;
    boolean pathSegmentsProcessed = false;
    
    PARAMETERS_LOOP: for (int i = 0; i < parameters.length; i++)
    {
      Type parameter = parameters[i];
      Class parameterClass = typeToClass(parameter);
      if (parameterClass == HttpServletRequest.class)
      {
        routeParameters.add(new RouteMethodParameter(RouteMethodParameterType.SERVLET_REQUEST));
      }
      else if (parameterClass == HttpServletResponse.class)
      {
        routeParameters.add(new RouteMethodParameter(RouteMethodParameterType.SERVLET_RESPONSE));
      }
      else if (parameterClass == HttpSession.class)
      {
        routeParameters.add(new RouteMethodParameter(RouteMethodParameterType.SESSION));
      }
      else if (parameterClass == RequestContext.class)
      {
        routeParameters.add(new RouteMethodParameter(RouteMethodParameterType.REQUEST_CONTEXT));
      }
      else if (parameterClass == RequestPath.class)
      {
        routeParameters.add(new RouteMethodParameter(RouteMethodParameterType.REQUEST_PATH));
      }
      else if (parameterClass == RequestParameters.class)
      {
        routeParameters.add(new RouteMethodParameter(RouteMethodParameterType.REQUEST_PARAMETERS));
      }
      else if (parameterClass == Format.class)
      {
        routeParameters.add(new RouteMethodParameter(RouteMethodParameterType.FORMAT));
      }
      else if (parameterClass == Map.class)
      {
        RouteMethodParameterType mapType = getRouteMapParameterType(parameter);
        if (mapType != null)
        {
          routeParameters.add(new RouteMethodParameter(mapType));
        }
        else
        {
          throw new InvalidRouteException("Unsupported Map parameter: " + parameter + " in method: " + method);
        }
      }
      else if (routeTree != null)
      {
        RoutePathParameterType routePathParameterType = getRouteMethodParameterType(parameter);
        if (routePathParameterType != null)
        {
          if (!pathSegmentsProcessed)
          {
            while (routeIndex < routeTree.pathTerminals.size())
            {
              PathTerminal pathTerminal = routeTree.pathTerminals.get(routeIndex);
              ++routeIndex;
              if (pathTerminal.canBeMappedToMethodParameter)
              {
                if (parameterClass != List.class)
                {
                  routeParameters.add(new RouteMethodParameter(RouteMethodParameterType.ROUTE_PATH, pathTerminal.pathIndex, routePathParameterType));
                  continue PARAMETERS_LOOP;
                }
                else
                {
                  throw new InvalidRouteException("List method parameter: " + parameter + " in method: " + method + " is mapped to path segment (" + pathTerminal.pathIndex + "). Path segments cannot be mapped to lists only parameters.");
                  
                }
              }
            }
            
            pathSegmentsProcessed = true;
            routeIndex = 0;
          }
          
          while (routeIndex < routeTree.parameterTerminals.size())
          {
            ParameterTerminal parameterTerminal = routeTree.parameterTerminals.get(routeIndex);
            ++routeIndex;
            if (parameterTerminal.canBeMappedToMethodParameter)
            {
              if (!parameterTerminal.optional || !parameterClass.isPrimitive())
              {
                RouteMethodParameterType routeMethodParameterType = parameterClass == List.class ? RouteMethodParameterType.ROUTE_PARAMETERS : RouteMethodParameterType.ROUTE_PARAMETER;
                routeParameters.add(new RouteMethodParameter(routeMethodParameterType, parameterTerminal.name, routePathParameterType));
                continue PARAMETERS_LOOP;
              }
              else
              {
                throw new InvalidRoutesMethodDeclaration("Primitive method parameter: " + parameter + " cannot be mapped to optional parameters in method: " + method);
              }
            }
          }
          
          throw new InvalidRoutesMethodDeclaration("Unmapped method parameter: " + parameter + " in method: " + method);
        }
        else
        {
          throw new InvalidRoutesMethodDeclaration("Unsupported method parameter: " + parameter + " in method: " + method);
        }
      }
      else
      {
        throw new InvalidRoutesMethodDeclaration("Unsupported method parameter: " + parameter + " in method: " + method);
      }
    }
    
    return routeParameters;
  }

  static RouteMethodParameterType getRouteMapParameterType(Type mapParameter)
  {
    if (mapParameter instanceof ParameterizedType)
    {
      Type[] types = ((ParameterizedType)mapParameter).getActualTypeArguments();
      if ((types.length == 2) && (types[0] == String.class))
      {
        if ((types[1] instanceof ParameterizedType))
        {
          ParameterizedType pt = (ParameterizedType)types[1];
          if (pt.getRawType() == List.class)
          {
            Type[] listTypes = pt.getActualTypeArguments();
            return ((listTypes.length == 1) && (listTypes[0] == String.class)) ? RouteMethodParameterType.PARAMETER_LIST_MAP : null;
          }
          else
          {
            return null;
          }
        }
        else if (types[1] == String.class)
        {
          return RouteMethodParameterType.PARAMETER_MAP;
        }
      }
      else
      {
        return null;
      }
    }
    if (mapParameter instanceof Class)
    {
      return RouteMethodParameterType.PARAMETER_LIST_MAP;
    }
    else
    {
      return null;
    }
  }
  
  static RoutePathParameterType getRouteMethodParameterType(Type parameter)
  {
    Class parameterClass = null;;
    if (parameter instanceof Class)
    {
      parameterClass = (Class)parameter;
    }
    else if (parameter instanceof ParameterizedType)
    {
      ParameterizedType pt = (ParameterizedType)parameter;
      if (pt.getRawType() == List.class)
      {
        Type[] types = ((ParameterizedType)parameter).getActualTypeArguments();
        if ((types.length == 1) && (types[0] instanceof Class))
        {
          parameterClass = (Class)types[0];
        }
      }
    }
    
    if (parameterClass != null)
    {
      if (parameterClass == String.class)
      {
        return RoutePathParameterType.STRING;
      }
      else if ((parameterClass == Character.class) || (parameterClass == char.class))
      {
        return RoutePathParameterType.CHARCTER;
      }
      else if ((parameterClass == Boolean.class) || (parameterClass == boolean.class))
      {
        return RoutePathParameterType.BOOLEAN;
      }
      else if ((parameterClass == Byte.class) || (parameterClass == byte.class))
      {
        return RoutePathParameterType.BYTE;
      }
      else if ((parameterClass == Short.class) || (parameterClass == short.class))
      {
        return RoutePathParameterType.SHORT;
      }
      else if ((parameterClass == Integer.class) || (parameterClass == int.class))
      {
        return RoutePathParameterType.INTEGER;
      }
      else if ((parameterClass == Long.class) || (parameterClass == long.class))
      {
        return RoutePathParameterType.LONG;
      }
      else if ((parameterClass == Float.class) || (parameterClass == float.class))
      {
        return RoutePathParameterType.FLOAT;
      }
      else if ((parameterClass == Double.class) || (parameterClass == double.class))
      {
        return RoutePathParameterType.DOUBLE;
      }
    }

    return null;
  }
}
