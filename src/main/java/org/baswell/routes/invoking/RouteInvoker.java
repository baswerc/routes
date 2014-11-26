package org.baswell.routes.invoking;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.baswell.routes.Format;
import org.baswell.routes.HttpMethod;
import org.baswell.routes.RequestContext;
import org.baswell.routes.RequestParameters;
import org.baswell.routes.RequestPath;
import org.baswell.routes.RouteConfig;
import org.baswell.routes.RouteMappingException;

public class RouteInvoker
{
  private HttpServletRequest request;
  
  private HttpServletResponse response; 
  
  private HttpMethod httpMethod;

  private RequestPath requestPath; 
  
  private RequestParameters requestParameters; 
  
  private RouteConfig routeConfig; 
  
  private RequestContext requestContext;
  
  private Map<String, List<String>> parameterListMap;

  private Map<String, String> parameterMap;
  
  private Format format;

  public RouteInvoker(HttpServletRequest request, HttpServletResponse response, HttpMethod httpMethod, RequestPath requestPath, RequestParameters requestParameters, Format format, RouteConfig routeConfig)
  {
    this.request = request;
    this.response = response;
    this.httpMethod = httpMethod;
    this.requestPath = requestPath;
    this.requestParameters = requestParameters;
    this.format = format;
    this.routeConfig = routeConfig;
  }
  
  public Object invoke(Object routeInstance, Method method, List<RouteMethodParameter> routeMethodParameters) throws RouteMappingException, InvocationTargetException 
  {
    Object[] invokeParameters = new Object[routeMethodParameters.size()];
    for (int i = 0; i < routeMethodParameters.size(); i++)
    {
      try
      {
        RouteMethodParameter routeMethodParameter = routeMethodParameters.get(i);
        switch (routeMethodParameter.type)
        {
          case REQUEST_CONTEXT:
            if (requestContext == null)
            {
              requestContext = new RequestContext(request, response, httpMethod, requestPath, requestParameters, format);
            }
            invokeParameters[i] = requestContext;
            break;
          
          case PARAMETER_LIST_MAP:
            if (parameterListMap == null)
            {
              parameterListMap = Collections.unmodifiableMap(requestParameters.getParameterListMap());
            }
            invokeParameters[i] = parameterListMap;
            break;
  
          case PARAMETER_MAP:
            if (parameterMap == null)
            {
              parameterMap = Collections.unmodifiableMap(requestParameters.getParameterMap());
            }
            invokeParameters[i] = parameterMap;
            break;
  
          case SERVLET_REQUEST:
            invokeParameters[i] = request;
            break;
            
          case REQUEST_PARAMETERS:
            invokeParameters[i] = requestParameters;
            break;
            
          case SERVLET_RESPONSE:
            invokeParameters[i] = response;
            break;
            
          case SESSION:
            invokeParameters[i] = request.getSession();
            break;
            
          case FORMAT:
            invokeParameters[i] = format;
            break;
            
          case REQUEST_PATH:
            invokeParameters[i] = requestPath;
            break;
            
          case ROUTE_PATH:
            switch (routeMethodParameter.routhPathParameterType)
            {
              case STRING:
                invokeParameters[i] = requestPath.get(routeMethodParameter.segmentIndex);
                break;
                
              case CHARCTER:
                String s = requestPath.get(routeMethodParameter.segmentIndex);
                if (s.length() != 1)
                {
                  throw new RouteOutputTypeMismatch();
                }
                else
                {
                  invokeParameters[i] = s.charAt(0);
                }
                break;
                
              case BOOLEAN:
                invokeParameters[i] = requestPath.getBoolean(routeMethodParameter.segmentIndex);
                break;
                
              case BYTE:
                invokeParameters[i] = requestPath.getByte(routeMethodParameter.segmentIndex);
                break;
                
              case SHORT:
                invokeParameters[i] = requestPath.getShort(routeMethodParameter.segmentIndex);
                break;
                
              case INTEGER:
                invokeParameters[i] = requestPath.getInteger(routeMethodParameter.segmentIndex);
                break;
                
              case LONG:
                invokeParameters[i] = requestPath.getLong(routeMethodParameter.segmentIndex);
                break;
                
              case FLOAT:
                invokeParameters[i] = requestPath.getFloat(routeMethodParameter.segmentIndex);
                break;
                
              case DOUBLE:
                invokeParameters[i] = requestPath.getDouble(routeMethodParameter.segmentIndex);
                break;
            }
            break;
            
          case ROUTE_PARAMETER:
            switch (routeMethodParameter.routhPathParameterType)
            {
              case STRING:
                invokeParameters[i] = requestParameters.get(routeMethodParameter.requestParameterName);
                break;
                
              case CHARCTER:
                invokeParameters[i] = requestParameters.getCharacter(routeMethodParameter.requestParameterName);
                break;
                
              case BOOLEAN:
                invokeParameters[i] = requestParameters.getBoolean(routeMethodParameter.requestParameterName);
                break;
                
              case BYTE:
                invokeParameters[i] = requestParameters.getByte(routeMethodParameter.requestParameterName);
                break;
                
              case SHORT:
                invokeParameters[i] = requestParameters.getShort(routeMethodParameter.requestParameterName);
                break;
                
              case INTEGER:
                invokeParameters[i] = requestParameters.getInteger(routeMethodParameter.requestParameterName);
                break;
                
              case LONG:
                invokeParameters[i] = requestParameters.getLong(routeMethodParameter.requestParameterName);
                break;
                
              case FLOAT:
                invokeParameters[i] = requestParameters.getFloat(routeMethodParameter.requestParameterName);
                break;
                
              case DOUBLE:
                invokeParameters[i] = requestParameters.getDouble(routeMethodParameter.requestParameterName);
                break;
            }
            break;
  
          case ROUTE_PARAMETERS:
            switch (routeMethodParameter.routhPathParameterType)
            {
              case STRING:
                invokeParameters[i] = requestParameters.getValues(routeMethodParameter.requestParameterName);
                break;
                
              case CHARCTER:
                invokeParameters[i] = requestParameters.getCharacters(routeMethodParameter.requestParameterName);
                break;
                
              case BOOLEAN:
                invokeParameters[i] = requestParameters.getBooleans(routeMethodParameter.requestParameterName);
                break;
                
              case BYTE:
                invokeParameters[i] = requestParameters.getBytes(routeMethodParameter.requestParameterName);
                break;
                
              case SHORT:
                invokeParameters[i] = requestParameters.getShorts(routeMethodParameter.requestParameterName);
                break;
                
              case INTEGER:
                invokeParameters[i] = requestParameters.getIntegers(routeMethodParameter.requestParameterName);
                break;
                
              case LONG:
                invokeParameters[i] = requestParameters.getLongs(routeMethodParameter.requestParameterName);
                break;
                
              case FLOAT:
                invokeParameters[i] = requestParameters.getFloats(routeMethodParameter.requestParameterName);
                break;
                
              case DOUBLE:
                invokeParameters[i] = requestParameters.getDoubles(routeMethodParameter.requestParameterName);
                break;
            }
            break;
        }
      }
      catch (NumberFormatException e)
      {
        throw new RouteMappingException("Unable to map route parameter " + method.getGenericParameterTypes()[i] + " for method: " + method, e);
      }
    }
    
    try
    {
      return method.invoke(routeInstance, invokeParameters);
    }
    catch (IllegalAccessException e)
    {
      throw new RouteMappingException("Unable to call map route method: " + method, e);
    }
    catch (IllegalArgumentException e)
    {
      throw new RouteMappingException("Invalid parameter mapping for route method: " + method, e);
    }
  }
}
