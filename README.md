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
into an object that accepts HTTP GET requests at the path /api/users and renders the loaded users with the JSP file users.jsp.

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

### Servlet Configuration

The Routes filter is the entry point for mapping HTTP servlet requests to Java methods.

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

This filter should be placed last in your filter chain. No filters in the chain below this filter will be processed when route method matches are found (i.e. `chain.doFilter` is not called).
If no match is found, then chain.doFilter will be called so further processing can occur. This will allow, for example, to still serve up file resources (ex. html, jsp) directly as long as
none of your routes match the file resource URL.

In addition to the `filter-mapping` specification, you can control which HTTP requests are candidates for routes with the `ONLY` and `EXCEPT` filter parameters.

````xml
<init-param>
   <param-name>ONLY</param-name>
   <param-value>/api.*</param-value>
</init-param>
````

In this example all HTTP requests with URL paths that start with `/api` will be candidates for routes (note that as in `url-pattern' the context path should be left off the expression).

````xml
<init-param>
   <param-name>EXCEPT</param-name>
   <param-value>.*\.jsp$</param-value>
 </init-param>
````

In this example all HTTP requests except URL paths that end with with `.jsp` will be candidates for routes. Both `ONLY` and `EXCEPT` must be valid Java regular expressions or an exception
will be thrown when the `RoutesFilter` is initialized.

## How To Use


### Routes Annotations


### Convention Based Routing


