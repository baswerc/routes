package org.baswell.routes.invoking;

public class RouteMethodParameter
{
  static enum RouteMethodParameterType
  {
    ROUTE_PATH,
    ROUTE_PARAMETER,
    ROUTE_PARAMETERS,
    SERVLET_REQUEST,
    SERVLET_RESPONSE,
    REQUEST_CONTEXT,
    REQUEST_PATH,
    REQUEST_PARAMETERS,
    PARAMETER_LIST_MAP,
    PARAMETER_MAP,
    SESSION,
    FORMAT;
  }
  
  final RouteMethodParameterType type;
  
  final Integer segmentIndex;

  final Integer groupIndex;
  
  final String requestParameterName;
  
  final RoutePathParameterType routhPathParameterType;
  
  RouteMethodParameter(RouteMethodParameterType type)
  {
    this.type = type;
    this.segmentIndex = null;
    this.groupIndex = null;
    this.requestParameterName = null;
    routhPathParameterType = null;
  }

  RouteMethodParameter(RouteMethodParameterType type, Integer segmentIndex, Integer groupIndex, RoutePathParameterType routhPathParameterType)
  {
    this.type = type;
    this.segmentIndex = segmentIndex;
    this.groupIndex = groupIndex;
    this.routhPathParameterType = routhPathParameterType;
    this.requestParameterName = null;
  }

  RouteMethodParameter(RouteMethodParameterType type, String requestParameterName, Integer groupIndex, RoutePathParameterType routhPathParameterType)
  {
    this.type = type;
    this.requestParameterName = requestParameterName;
    this.groupIndex = groupIndex;
    this.routhPathParameterType = routhPathParameterType;
    this.segmentIndex = null;
  }
}