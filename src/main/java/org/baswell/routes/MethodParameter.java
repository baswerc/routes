package org.baswell.routes;

class MethodParameter
{
  final MethodRouteParameterType type;
  
  final Integer segmentIndex;

  final Integer groupIndex;
  
  final String requestParameterName;
  
  final MethodPathParameterType routhPathParameterType;
  
  MethodParameter(MethodRouteParameterType type)
  {
    this.type = type;
    this.segmentIndex = null;
    this.groupIndex = null;
    this.requestParameterName = null;
    routhPathParameterType = null;
  }

  MethodParameter(MethodRouteParameterType type, Integer segmentIndex, Integer groupIndex, MethodPathParameterType routhPathParameterType)
  {
    this.type = type;
    this.segmentIndex = segmentIndex;
    this.groupIndex = groupIndex;
    this.routhPathParameterType = routhPathParameterType;
    this.requestParameterName = null;
  }

  MethodParameter(MethodRouteParameterType type, String requestParameterName, Integer groupIndex, MethodPathParameterType routhPathParameterType)
  {
    this.type = type;
    this.requestParameterName = requestParameterName;
    this.groupIndex = groupIndex;
    this.routhPathParameterType = routhPathParameterType;
    this.segmentIndex = null;
  }
}