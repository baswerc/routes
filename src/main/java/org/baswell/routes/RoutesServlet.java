package org.baswell.routes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.baswell.routes.RoutingTable.*;

public class RoutesServlet extends HttpServlet
{
  private volatile static RoutesEngine routesEngine;

  @Override
  protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException
  {
    if (routesEngine == null)
    {
      synchronized (RoutesServlet.class)
      {
        if (routesEngine == null)
        {
          routesEngine = new RoutesEngine(theRoutingTable);
        }
      }
    }

    if (!routesEngine.process(servletRequest, servletResponse))
    {
      servletResponse.setStatus(404);
    }
  }
}