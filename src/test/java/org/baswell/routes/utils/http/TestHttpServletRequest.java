package org.baswell.routes.utils.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

public class TestHttpServletRequest implements HttpServletRequest
{
  public Map<String, Object> attributes = new HashMap<String, Object>();
  
  public Map<String, String[]> parameters = new HashMap<String, String[]>();
  
  public String method = "GET";
  
  public String hostName;
  
  public String contextPath = "/";
  
  public String requestUri;
  
  public String contentType = "text/html";
  
  public int contentLength;
  
  public TestServletInputStream servletInputStream;
  
  public String protocol = "HTTP/1.1";
  
  public String scheme = "http";
  
  public String serverName = "localhost";
  
  public int serverPort = 8080;
  
  public TestRequestDispatcher requestDispatcher;

  public TestHttpSession session = new TestHttpSession();

  private String pathInfo;

  private String pathTranslated;

  private String queryString;

  private String requestUrl;

  private String servletPath;
  
  public TestHttpServletRequest()
  {}
  
  public TestHttpServletRequest(String method, String contextPath, String requestUri, String... parameters)
  {
    this.method = method;
    this.contextPath = contextPath;
    this.requestUri = requestUri;
    
    for (int i = 0; i < parameters.length; i += 2)
    {
      this.parameters.put(parameters[i], new String[] {parameters[i + 1]});
    }
  }

  @Override
  public Object getAttribute(String name)
  {
    return attributes.get(name);
  }

  @Override
  public Enumeration<String> getAttributeNames()
  {
    return Collections.enumeration(attributes.keySet());
  }

  @Override
  public String getCharacterEncoding()
  {
    return null;
  }

  @Override
  public void setCharacterEncoding(String env) throws UnsupportedEncodingException
  {}

  @Override
  public int getContentLength()
  {
    return contentLength;
  }

  @Override
  public String getContentType()
  {
    return contentType;
  }

  @Override
  public ServletInputStream getInputStream() throws IOException
  {
    return servletInputStream;
  }

  @Override
  public String getParameter(String name)
  {
    return parameters.containsKey(name) ? parameters.get(name)[0] : null;
  }

  @Override
  public Enumeration<String> getParameterNames()
  {
    return Collections.enumeration(parameters.keySet());
  }

  @Override
  public String[] getParameterValues(String name)
  {
    return parameters.get(name);
  }

  @Override
  public Map<String, String[]> getParameterMap()
  {
    return new HashMap<String, String[]>(parameters);
  }

  @Override
  public String getProtocol()
  {
    return protocol;
  }

  @Override
  public String getScheme()
  {
    return scheme;
  }

  @Override
  public String getServerName()
  {
    return serverName;
  }

  @Override
  public int getServerPort()
  {
    return serverPort;
  }

  @Override
  public BufferedReader getReader() throws IOException
  {
    return new BufferedReader(new InputStreamReader(servletInputStream));
  }

  @Override
  public String getRemoteAddr()
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public String getRemoteHost()
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public void setAttribute(String name, Object value)
  {
    attributes.put(name, value);
  }

  @Override
  public void removeAttribute(String name)
  {
    attributes.remove(name);
  }

  @Override
  public Locale getLocale()
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public Enumeration<Locale> getLocales()
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public boolean isSecure()
  {
    return scheme.equalsIgnoreCase("https");
  }

  @Override
  public RequestDispatcher getRequestDispatcher(String path)
  {
    requestDispatcher = new TestRequestDispatcher(path);
    return requestDispatcher;
  }

  @Override
  public String getRealPath(String path)
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public int getRemotePort()
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public String getLocalName()
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public String getLocalAddr()
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public int getLocalPort()
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public ServletContext getServletContext()
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public AsyncContext startAsync() throws IllegalStateException
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public boolean isAsyncStarted()
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public boolean isAsyncSupported()
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public AsyncContext getAsyncContext()
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public DispatcherType getDispatcherType()
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public String getAuthType()
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public Cookie[] getCookies()
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public long getDateHeader(String name)
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public String getHeader(String name)
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public Enumeration<String> getHeaders(String name)
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public Enumeration<String> getHeaderNames()
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public int getIntHeader(String name)
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public String getMethod()
  {
    return method;
  }

  @Override
  public String getPathInfo()
  {
    return pathInfo;
  }

  @Override
  public String getPathTranslated()
  {
    return pathTranslated;
  }

  @Override
  public String getContextPath()
  {
    return contextPath;
  }

  @Override
  public String getQueryString()
  {
    return queryString;
  }

  @Override
  public String getRemoteUser()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isUserInRole(String role)
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public Principal getUserPrincipal()
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public String getRequestedSessionId()
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public String getRequestURI()
  {
    return requestUri;
  }

  @Override
  public StringBuffer getRequestURL()
  {
    return new StringBuffer(requestUrl);
  }

  @Override
  public String getServletPath()
  {
    return servletPath;
  }

  @Override
  public HttpSession getSession(boolean create)
  {
    return session;
  }

  @Override
  public HttpSession getSession()
  {
    return session;
  }

  @Override
  public boolean isRequestedSessionIdValid()
  {
    return true;
  }

  @Override
  public boolean isRequestedSessionIdFromCookie()
  {
    return true;
  }

  @Override
  public boolean isRequestedSessionIdFromURL()
  {
    return false;
  }

  @Override
  public boolean isRequestedSessionIdFromUrl()
  {
    return false;
  }

  @Override
  public boolean authenticate(HttpServletResponse response) throws IOException, ServletException
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public void login(String username, String password) throws ServletException
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public void logout() throws ServletException
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public Collection<Part> getParts() throws IOException, ServletException
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public Part getPart(String name) throws IOException, ServletException
  {
    throw new RuntimeException("Not implemented.");
  }

}
