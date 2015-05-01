Routes
======

Routes is a Java library for mapping HTTP requests to plain Java object methods. Routes runs within a Java servlet container and is alternative to processing HTTP requests with the Servlet
API. Routes makes it easy to turn this:

```Java
public class ApiRoutes
{
  public String getUsers(HttpServletRequest request)
  {
    ...
    request.setAttribute("users", users);
    return "users.jsp";
  }
}
```
into an object that accepts HTTP _GET_ requests at the path _/api/users_ and renders the loaded users with the JSP file _users.jsp_.

## Getting Started

### Direct Download
You can download <a href="https://github.com/baswerc/routes/releases/download/1.0/routes-1.0.jar">routes-1.0.jar</a> directly and place in your project.

### Using Maven
Add the following dependency into your Maven project:

````xml
<dependency>
    <groupId>org.baswell</groupId>
    <artifactId>routes</artifactId>
    <version>1.0</version>
</dependency>
````

### Dependencies
Routes requires the Java Servlet API 2.4 or greater. Routes has no other external dependencies.

## Servlet Container Configuration
There are three different ways Routes can be used within a Servlet container.

### RoutesServlet

The <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RoutesServlet.html">RoutesServlet</a> can be used to map HTTP requests to routes. Any HTTP request the `RoutesServlet` does not find a
matching route for is returned a 404 (`HttpServletResponse.setStatus(404)`).

````xml
<servlet>
    <servlet-name>RoutesServlet</servlet-name>
    <servlet-class>org.baswell.routes.RoutesServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>RoutesServlet</servlet-name>
    <url-pattern>/*</url-pattern>
</servlet-mapping>
````

### RoutesFilter

The `RoutesFilter` may work better when Routes is not the only means of serving content for your application.

````xml
<filter>
    <filter-name>RoutesFilter</filter-name>
    <filter-class>org.baswell.routes.RoutesFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>RoutesFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
````

This filter should be placed last in your filter chain. Chain processing will end in this filter when a route method match is found (`chain.doFilter` is not called).
If no match is found, then chain.doFilter will be called so further processing can occur. This will allow, for example, to still serve up file resources (ex. html, jsp) directly as long as
none of your routes match the file resource URL.

In addition to the `filter-mapping` configuration, you can control which HTTP requests are candidates for routes with the `ONLY` and `EXCEPT` filter parameters
(this can improve performance when it's known that certain HTTP paths won't match any routes).

````xml
<init-param>
   <param-name>ONLY</param-name>
   <param-value>/api/.*,/data/.*</param-value>
</init-param>
````
This parameter is a comma delimited list of Java regular expressions. In this example all HTTP requests with URL paths that start with `/api/` or `/data/` will be candidates for routes
(as in `url-pattern` the context path should be left off the expression).

````xml
<init-param>
   <param-name>EXCEPT</param-name>
   <param-value>.*\.html$,.*\.jsp$</param-value>
 </init-param>
````

In this example all HTTP requests except URL paths that end with with `.html` or `.jsp` will be candidates for routes. Both `ONLY` and `EXCEPT` must be a list of valid Java regular expressions or an exception
will be thrown when the `RoutesFilter` is initialized.

### RoutesEngine

Both `RoutesServlet` and `RoutesFilter` use `RoutesEngine` to match HTTP requests to routes. It can be used in your status to manually handle when routes should be used to process HTTP requests.

```Java
...
RoutesEngine routesEngine = new RoutesEngine();
...

if (routesEngine.process(servletRequest, servletResponse))
{
  // Request was processed, response has already been sent
}
else
{
 // Request was not processed, do something with the response
}
...
```

## RouteTable


## Routes By Examples

Routes imposes no class hierarchies or interfaces on your classes. There are two ways to tell Routes how your Java objects are matched to HTTP requests, by convention or by using the Routes
annotations. The following examples show how both these methods work.

### Example One: By Convention
```Java
public class LoginRoutes
{
  public String get(HttpServletRequest request)
  {
    ...
    return "login.jsp";
  }

  public void post(HttpServletRequest request, HttpServletResponse response)
  {...}

  public String getForgotPassword(HttpServletRequest request)
  {
    ...
    return "login/forgotpassword.jsp";
  }

  public void postForgotPassword(HttpServletRequest request)
  {
    ...
    throw new RedirectTo("/login");
  }
}
```

<table>
  <thead>
    <tr>
      <th align="left">HTTP Request</th>
      <th align="left">Method Called</t>
    </tr>
  </thead>
  <tbody>
    <tr>
       <td><pre>GET /login HTTP/1.0</pre></td>
       <td><pre>get(request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">By default the class name is used to form the first url segment, in this case <i>/login</i>. If the class name ends in <i>Routes</i>, <i>Route</i>, <i>Controller</i>, or
       <i>Handler</i> then this part of the class name will be removed from the path segment.Method names that just contain HTTP methods (ex. <i>get</i>, <i>post</i>)
      don't add anything to the matched path. The JSP file at _/WEB-INF/jsps/login.jsp_ will be rendered to the user.</td>
    </tr>
    <tr>
       <td><pre>POST /login HTTP/1.1</pre></td>
       <td><pre>post(request, response)</pre></td>
    </tr>
    <tr>
      <td colspan="2">Since this method does not return anything, it must handle the content sent back to the user with the HttpServletResponse object.</td>
    </tr>

    <tr>
       <td><pre>GET /login/forgotpassword HTTP/1.1</pre></td>
       <td><pre>getForgotPassword(request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">The remaining method name after all HTTP methods are removed from the begging forms the next url segment to match.
      The JSP file at _/WEB-INF/jsps/login.jsp_ will be rendered to the user.</td>
    </tr>

    <tr>
       <td><pre>/login/ForGotpasSworD HTTP/1.1</pre></td>
       <td><pre>getForgotPassword(request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">By default matching in Routes for paths and parameters is case insensitive. This can be changed in
      <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RoutesConfiguration.html#caseInsensitive">RoutesConfiguration.caseInsensitve</a>.</td>
    </tr>

    <tr>
       <td><pre><pre>POST /login/forgotpassword HTTP/1.1</pre></td>
       <td><pre>postForgotPassword(request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">You can use the helper class <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RedirectTo.html">RedirectTo</a> to redirect the
      client to another page.</td>
    </tr>
    <tr>
       <td><pre>PUT /login HTTP/1.0</pre></td>
       <td><i>404</i></td>
    </tr>
    <tr>
      <td colspan="2">Would need a <i>put()</i> method defined for this request to be matched. You can also combine HTTP methods together so for example the
      method <i>postPut()</i> would be called for both <i>POST</i> and <i>PUT</i> requests with the path <i>/login</i></td>
    </tr>


  </tbody>
</table>

### Example Two: Using Annotations
```Java
@Routes(value="/", forwardPath="login")
public class MyLoginRoutes
{
  @Route("/login")
  public String getLoginPage(HttpServletRequest request)
  {
    ...
    return "login.jsp";
  }

  @Route(value = "/login", respondsToMethods = {HttpMethod.POST, HttpMethod.PUT})
  public void doLogin(HttpServletRequest request, HttpServletResponse response)
  {...}

  @Route(value = "/forgot_password", respondsToMethods = {HttpMethod.GET})
  public String showForgotPassword(HttpServletRequest request)
  {
    ...
    return "forgotpassword.jsp";
  }

  public void postForgotPassword(HttpServletRequest request)
  {
    ...
    throw new RedirectTo("/login");
  }
}
```
<table>
  <thead>
    <tr>
      <th align="left">HTTP Request</th>
      <th align="left">Method Called</t>
    </tr>
  </thead>
  <tbody>
    <tr>
       <td><pre>GET /login HTTP/1.0</pre></td>
       <td><pre>get(request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">By default the class name is used to form the first url segment, in this case <i>/login</i>. If the class name ends in <i>Routes</i>, <i>Route</i>, <i>Controller</i>, or
       <i>Handler</i> then this part of the class name will be removed from the path segment.Method names that just contain HTTP methods (ex. <i>get</i>, <i>post</i>)
      don't add anything to the matched path. The JSP file at _/WEB-INF/jsps/login.jsp_ will be rendered to the user.</td>
    </tr>
    <tr>
  </tbody>
</table>

## Routes Configuration

## Routes Helpers

## Routes Meta Page

