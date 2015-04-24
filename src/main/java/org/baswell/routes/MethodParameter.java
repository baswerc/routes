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