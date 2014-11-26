package org.baswell.routes.utils.http;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class TestRequestDispatcher implements RequestDispatcher
{
  public final String path;
  
  public boolean forwardCalled;
  
  public boolean includeCalled;
  
  public ServletException servletExceptionToThrow;
  
  public IOException ioExceptionToThrow;
  
  public TestRequestDispatcher(String path)
  {
    this.path = path;
  }

  @Override
  public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException
  {
    forwardCalled = true;
    if (servletExceptionToThrow != null)
    {
      throw servletExceptionToThrow;
    }
    else if (ioExceptionToThrow != null)
    {
      throw ioExceptionToThrow;
    }
  }

  @Override
  public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException
  {
    includeCalled = true;
    if (servletExceptionToThrow != null)
    {
      throw servletExceptionToThrow;
    }
    else if (ioExceptionToThrow != null)
    {
      throw ioExceptionToThrow;
    }
  }
}
