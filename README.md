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


## Routes By Examples

Routes imposes no class hierarchies or interfaces on your classes. There are two ways to tell Routes how your Java objects are matched to HTTP requests, by convention or by using the Routes
annotations. The following examples show how both these methods work.

### Example One
```Java
public class ApiRoutes
{
  public String get(HttpServletRequest request, HttpServletResponse)
  {...}

  public String getLogin(HttpServletRequest request, HttpServletResponse)
  {...}

  public String postLogin(HttpServletRequest request, HttpServletResponse)
  {...}
}
```

| HTTP Request        | Matched Method | Remarks  |
| ------------------- |:-------------: | :--------|
| `GET /api HTTP/1.0` | `get`          |          |





#### Matched HTTP Requests

```

...
```