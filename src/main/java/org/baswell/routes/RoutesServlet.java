package org.baswell.routes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.baswell.routes.RoutingTable.*;

public class RoutesServlet extends HttpServlet
{
  private volatile static RoutesEntry routesEntry;

  @Override
  protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException
  {
    if (routesEntry == null)
    {
      synchronized (RoutesServlet.class)
      {
        if (routesEntry == null)
        {
          routesEntry = new RoutesEntry(theRoutingTable);
        }
      }
    }

    if (!routesEntry.process(servletRequest, servletResponse))
    {
      servletResponse.setStatus(404);
    }
  }
}