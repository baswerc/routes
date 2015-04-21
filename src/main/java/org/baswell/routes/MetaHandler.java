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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

class MetaHandler
{
  final RoutingTable routingTable;

  final RoutesConfiguration routesConfiguration;

  MetaHandler(RoutingTable routingTable, RoutesConfiguration routesConfiguration)
  {
    this.routingTable = routingTable;
    this.routesConfiguration = routesConfiguration;
  }

  boolean handled(HttpServletRequest request, HttpServletResponse response, RequestPath path, RequestParameters parameters, HttpMethod httpMethod, RequestFormat requestFormat) throws IOException, ServletException
  {
    String routesMetaPath = routesConfiguration.routesMetaPath;
    if (!routesMetaPath.startsWith("/")) routesMetaPath = "/" + routesMetaPath;

    if (path.startsWith(routesMetaPath))
    {
      if ((routesConfiguration.metaAuthenticator == null) || routesConfiguration.metaAuthenticator.metaRequestAuthenticated(request, response))
      {
        path = path.substring(routesMetaPath.length());

        if (requestFormat.mediaType == MediaType.HTML)
        {
          if (path.equals(""))
          {
            getRoutesPage(response);
            return true;
          }

        }
        else if (requestFormat.mediaType == MediaType.JSON)
        {
          if (path.equals(""))
          {
            getRoutes(request, response, parameters);
            return true;
          }
        }
        return false;
      }
      else
      {
        return true;
      }
    }

    return false;
  }

  void getRoutesPage(HttpServletResponse response) throws IOException
  {
    response.getOutputStream().write(getIndexHtml("routes.html"));
  }

  void getRoutes(HttpServletRequest request, HttpServletResponse response, RequestParameters parameters) throws IOException
  {
    List<RouteTableRow> rows = new ArrayList<RouteTableRow>();
    if (parameters.contains("path"))
    {
      RequestPath requestPath = new RequestPath(parameters.get("path"));
      HttpMethod httpMethod = HttpMethod.fromServletMethod(parameters.get("httpMethod"));
      String acceptType = parameters.get("acceptType");
      RequestParameters requestParameters = new RequestParameters(parameters.get("parameters"));

      for (RouteNode routeNode : routingTable.getRouteNodes())
      {
        if ((routeNode.criteria.matches(httpMethod, new RequestFormat(acceptType), requestPath, requestParameters)))
        {
          rows.add(new RouteTableRow(routeNode, request));
        }
      }
    }
    else
    {
      for (RouteNode routeNode : routingTable.getRouteNodes())
      {
        rows.add(new RouteTableRow(routeNode, request));
      }
    }

    response.setContentType("application/json");
    PrintWriter writer = response.getWriter();
    writer.write("[");
    for (int i = 0; i < rows.size(); i++)
    {
      if (i != 0)
      {
        writer.write(",");
      }
      rows.get(i).toJson(writer);
    }

    writer.write("]");
  }

  byte[] getIndexHtml(String file) throws IOException
  {
    InputStream indexStream = MetaHandler.class.getResourceAsStream("/" + file);
    byte[] buffer = new byte[1024];
    ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
    int read;

    while ((read = indexStream.read(buffer)) != -1)
    {
      bytesOut.write(buffer, 0, read);
    }

    indexStream.close();

    return bytesOut.toByteArray();
  }

  class RouteTableRow
  {
    String link;

    String path;

    String httpMethods;

    String acceptFormats;

    String classMethod;

    RouteTableRow(RouteNode routeNode, HttpServletRequest request)
    {
      path = routeNode.routeConfiguration.route;
      link = request.getContextPath() + path;

      if ((routeNode.routeConfiguration.respondsToMethods == null) || routeNode.routeConfiguration.respondsToMethods.isEmpty())
      {
        httpMethods = "";
      }
      else
      {
        for (HttpMethod httpMethod : routeNode.routeConfiguration.respondsToMethods)
        {
          if (httpMethods == null)
          {
            httpMethods = httpMethod.toString();
          }
          else
          {
            httpMethods += ", " + httpMethod;
          }
        }
      }

      if ((routeNode.routeConfiguration.respondsToMedia == null) || routeNode.routeConfiguration.respondsToMedia.isEmpty())
      {
        acceptFormats = "";
      }
      else
      {
        for (MediaType mediaType : routeNode.routeConfiguration.respondsToMedia)
        {
          if (acceptFormats == null)
          {
            acceptFormats = mediaType.toString();
          }
          else
          {
            acceptFormats += ", " + mediaType.toString();
          }
        }
      }

      classMethod = routeNode.method.getDeclaringClass().getSimpleName() + ":" + routeNode.method.getName();
    }

    void toJson(PrintWriter writer) throws IOException
    {
      writer.write("{\"link\": \"" + link + "\", \"path\": \"" + path + "\", \"respondsToMethods\": \"" + httpMethods + "\", \"acceptFormats\": \"" + acceptFormats + "\", \"classMethod\": \"" + classMethod + "\"}");
    }
  }


}
