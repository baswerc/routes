package org.baswell.routes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class MethodInvoker
{
  private HttpServletRequest request;
  
  private HttpServletResponse response; 
  
  private HttpMethod httpMethod;

  private RequestPath requestPath; 
  
  private RequestParameters requestParameters; 
  
  private RouteConfiguration routeConfiguration;
  
  private RequestContext requestContext;
  
  private Map<String, List<String>> parameterListMap;

  private Map<String, String> parameterMap;
  
  private RequestFormat requestFormat;

  MethodInvoker(HttpServletRequest request, HttpServletResponse response, HttpMethod httpMethod, RequestPath requestPath, RequestParameters requestParameters, RequestFormat requestFormat, RouteConfiguration routeConfiguration)
  {
    this.request = request;
    this.response = response;
    this.httpMethod = httpMethod;
    this.requestPath = requestPath;
    this.requestParameters = requestParameters;
    this.requestFormat = requestFormat;
    this.routeConfiguration = routeConfiguration;
  }
  
  Object invoke(Object routeInstance, Method method, List<MethodParameter> methodParameters, List<Matcher> pathMatchers, Map<String, Matcher> parameterMatchers) throws RouteMappingException, InvocationTargetException
  {
    Object[] invokeParameters = new Object[methodParameters.size()];
    for (int i = 0; i < methodParameters.size(); i++)
    {
      try
      {
        MethodParameter methodParameter = methodParameters.get(i);
        switch (methodParameter.type)
        {
          case REQUEST_CONTEXT:
            if (requestContext == null)
            {
              requestContext = new RequestContext(request, response, httpMethod, requestPath, requestParameters, requestFormat);
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
            invokeParameters[i] = requestFormat;
            break;
            
          case REQUEST_PATH:
            invokeParameters[i] = requestPath;
            break;
            
          case ROUTE_PATH:
            switch (methodParameter.routhPathParameterType)
            {
              case STRING:
                if (methodParameter.groupIndex != null)
                {
                  Matcher matcher = pathMatchers.get(methodParameter.segmentIndex);
                  if ((matcher != null) && (methodParameter.groupIndex < matcher.groupCount()))
                  {
                    invokeParameters[i] = matcher.group(methodParameter.groupIndex + 1);
                    break;
                  }
                }

                invokeParameters[i] = requestPath.get(methodParameter.segmentIndex);
                break;
                
              case CHARCTER:
                invokeParameters[i] = requestPath.getCharacter(methodParameter.segmentIndex);
                break;
                
              case BOOLEAN:
                invokeParameters[i] = requestPath.getBoolean(methodParameter.segmentIndex);
                break;
                
              case BYTE:
                invokeParameters[i] = requestPath.getByte(methodParameter.segmentIndex);
                break;
                
              case SHORT:
                invokeParameters[i] = requestPath.getShort(methodParameter.segmentIndex);
                break;
                
              case INTEGER:
                invokeParameters[i] = requestPath.getInteger(methodParameter.segmentIndex);
                break;
                
              case LONG:
                invokeParameters[i] = requestPath.getLong(methodParameter.segmentIndex);
                break;
                
              case FLOAT:
                invokeParameters[i] = requestPath.getFloat(methodParameter.segmentIndex);
                break;
                
              case DOUBLE:
                invokeParameters[i] = requestPath.getDouble(methodParameter.segmentIndex);
                break;
            }
            break;
            
          case ROUTE_PARAMETER:
            switch (methodParameter.routhPathParameterType)
            {
              case STRING:
                invokeParameters[i] = requestParameters.get(methodParameter.requestParameterName);
                break;
                
              case CHARCTER:
                invokeParameters[i] = requestParameters.getCharacter(methodParameter.requestParameterName);
                break;
                
              case BOOLEAN:
                invokeParameters[i] = requestParameters.getBoolean(methodParameter.requestParameterName);
                break;
                
              case BYTE:
                invokeParameters[i] = requestParameters.getByte(methodParameter.requestParameterName);
                break;
                
              case SHORT:
                invokeParameters[i] = requestParameters.getShort(methodParameter.requestParameterName);
                break;
                
              case INTEGER:
                invokeParameters[i] = requestParameters.getInteger(methodParameter.requestParameterName);
                break;
                
              case LONG:
                invokeParameters[i] = requestParameters.getLong(methodParameter.requestParameterName);
                break;
                
              case FLOAT:
                invokeParameters[i] = requestParameters.getFloat(methodParameter.requestParameterName);
                break;
                
              case DOUBLE:
                invokeParameters[i] = requestParameters.getDouble(methodParameter.requestParameterName);
                break;
            }
            break;
  
          case ROUTE_PARAMETERS:
            switch (methodParameter.routhPathParameterType)
            {
              case STRING:
                invokeParameters[i] = requestParameters.getValues(methodParameter.requestParameterName);
                break;
                
              case CHARCTER:
                invokeParameters[i] = requestParameters.getCharacters(methodParameter.requestParameterName);
                break;
                
              case BOOLEAN:
                invokeParameters[i] = requestParameters.getBooleans(methodParameter.requestParameterName);
                break;
                
              case BYTE:
                invokeParameters[i] = requestParameters.getBytes(methodParameter.requestParameterName);
                break;
                
              case SHORT:
                invokeParameters[i] = requestParameters.getShorts(methodParameter.requestParameterName);
                break;
                
              case INTEGER:
                invokeParameters[i] = requestParameters.getIntegers(methodParameter.requestParameterName);
                break;
                
              case LONG:
                invokeParameters[i] = requestParameters.getLongs(methodParameter.requestParameterName);
                break;
                
              case FLOAT:
                invokeParameters[i] = requestParameters.getFloats(methodParameter.requestParameterName);
                break;
                
              case DOUBLE:
                invokeParameters[i] = requestParameters.getDoubles(methodParameter.requestParameterName);
                break;
            }
            break;
        }
      }
      catch (NumberFormatException e)
      {
        throw new RoutesException("Unable to map route parameter " + method.getGenericParameterTypes()[i] + " for method: " + method, e);
      }
    }
    
    try
    {
      return method.invoke(routeInstance, invokeParameters);
    }
    catch (IllegalAccessException e)
    {
      throw new RoutesException("Unable to call map route method: " + method, e);
    }
    catch (IllegalArgumentException e)
    {
      throw new RoutesException("Invalid parameter mapping for route method: " + method, e);
    }
  }
}
