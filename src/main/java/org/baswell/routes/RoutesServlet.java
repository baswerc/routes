package org.baswell.routes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RoutesServlet extends HttpServlet
{
  private static RoutesEntry routesEntry = new RoutesEntry();

  @Override
  protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException
  {
    if (!routesEntry.process(servletRequest, servletResponse))
    {
      servletResponse.setStatus(404);
    }
  }
}