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

### Routes Servlet

The <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RoutesServlet.html">RoutesServlet</a> can be used to map HTTP requests to routes. Any HTTP request the `RoutesServlet` does not find a
matching route for is returned a 404 (`HttpServletResponse.setStatus(404)`).

````xml
<servlet>
    <servlet-name>RoutesServlet</servlet-name>
    <servlet-class>org.baswell.routes.RoutesServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>RoutesServlet</servlet-name>
    <url-pattern>/routes/*</url-pattern>
</servlet-mapping>
````

### Routes Filter

The <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RoutesFilter.html">RoutesFilter</a> may work better when Routes is not the only means of serving content for your application.

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

### Routes Engine

Both `RoutesServlet` and `RoutesFilter` use `RoutesEngine` to match HTTP requests to routes. It can be used directly to manually handle when routes should be used to process HTTP requests.

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

## The Routing Table


## Routes By Example

Routes imposes no class hierarchies or interfaces on your classes. There are two ways to tell Routes how your Java objects are matched to HTTP requests, by convention or by using annotations. If your class has no
Routes annotations then all public methods are candidates to being matched to incoming HTTP requests (only the immediate class, public methods of any super classes are not candidates).
Routes use a convention for converting unannotated classes and methods to HTTP candidates that you can override using <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RouteByConvention.html">RouteByConvention</a>.

The annotations <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/Routes.html">Routes</a> (class level) and <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/Route.html">Route</a> (method level)
provide full control over how methods are matched to HTTP requests. By default if your class has at least one of these annotations then only methods with the `Route` annotations can be matched to HTTP requests (public, unannotated
methods will not be candidates). This can be override for the entire class with <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/Routes.html#routeUnannotatedPublicMethods()">Routes.routeUnannotatedPublicMethods</a> or
globally with <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RoutesConfiguration.html#routeUnannotatedPublicMethods">RoutesConfiguration.routeUnannotatedPublicMethods</a>.

The following examples show how both these methods work.

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
       <td><pre>GET /login HTTP/1.1</pre></td>
       <td><pre>get(request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">By default the class name is used to form the first url segment, in this case <i>/login</i>. If the class name ends in <i>Routes</i>, <i>Route</i>, <i>Controller</i>, or
       <i>Handler</i> then this part of the class name will be removed from the path segment.Method names that just contain HTTP methods (ex. <i>get</i>, <i>post</i>)
      don't add anything to the matched path. The JSP file at <i>/WEB-INF/jsps/login.jsp</i> will be rendered to the user.</td>
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
       <td><pre>PUT /login HTTP/1.1</pre></td>
       <td><pre>404</pre></td>
    </tr>
    <tr>
      <td colspan="2">Would need a <i>put()</i> method defined for this request to be matched. You can also combine HTTP methods together so for example the
      method <i>postPut()</i> would be called for both <i>POST</i> and <i>PUT</i> requests with the path <i>/login</i></td>
    </tr>


  </tbody>
</table>

### Example Two: Using Annotations
```Java
@Routes(forwardPath="login")
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

  @Route(value = "/login/forgotpassword", respondsToMethods = {HttpMethod.GET})
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
       <td><pre>GET /login HTTP/1.1</pre></td>
       <td><pre>get(request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">If no root level path annotation is specified (<a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/Routes.html#value()">Routes.value</a>) then the path specified in
      <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/Route.html#value()">Root.value</a> will form the full match path. Since no <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/Route.html#respondsToMethods()">Route.respondsToMethod</a>
      was specified in the annotation the accepted HTTP methods are taken from the method name as in the convention based approach.</td>
    </tr>
    <tr>

    <tr>
       <td><pre>POST /login HTTP/1.1</pre></td>
       <td><pre>doLogin(request, response)</pre></td>
    </tr>
    <tr>
      <td colspan="2">Since both <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/Routes.html#value()">Routes.value</a> and <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/Route.html#respondsToMethods()">Route.respondsToMethod</a> are
       specified the method name has no impact on which HTTP requests are matched.</td>
    </tr>
    <tr>

    <tr>
       <td><pre>PUT /login HTTP/1.1</pre></td>
       <td><pre>doLogin(request, response)</pre></td>
    </tr>
    <tr>
      <td colspan="2">All HTTP methods specified in the <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/Route.html#respondsToMethods()">Route.respondsToMethod</a> array will be candidates for matches.</td>
    </tr>
    <tr>

    <tr>
       <td><pre>GET /login/forgotpassword HTTP/1.1</pre></td>
       <td><pre>showForgotPassword(request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">The JSP file at <i>/WEB-INF/jsps/login/forgotpassword.jsp</i> will be rendered to the user since <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/Routes.html#forwardPath()">Routes.forwardPath</a>
      is specified. If the forward path does not begin with a <i>/</i> then the value is appended to <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RoutesConfiguration.html#rootForwardPath">RoutesConfiguration.rootForwardPath</a>. If
      the forward path was <i>/login</i> then the the JSP file <i>/login/forgotpassword.jsp</i> would be rendered.</td>
    </tr>
    <tr>
    <tr>
       <td><pre>POST /forgotpassword HTTP/1.1</pre></td>
       <td><pre>404</pre></td>
    </tr>
    <tr>
      <td colspan="2">Since <i>postForgotPassword</i> is not annotated, it is not a candidate for HTTP requests. This can be overridden using <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/Routes.html#routeUnannotatedPublicMethods()">Routes.routeUnannotatedPublicMethods</a>
      or <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RoutesConfiguration.html#routeUnannotatedPublicMethods">RoutesConfiguration.routeUnannotatedPublicMethods</a>.</td>
    </tr>
    <tr>
  </tbody>
</table>


### Example Three: Mixed
```Java
abstract public class BaseRoutes
{
  public String getSomeResource(HttpServletRequest request)
  {...}

  @Route(value="faq")
  public String getFAQ(HttpServletRequest request)
  {...}
}

@Routes(value="/login", routeUnannotatedPublicMethods=true, forwardPath="/login")
public class MyLoginRoutes extends BaseRoutes
{
  @Route
  public String getLoginPage(HttpServletRequest request)
  {
    ...
    return "login.jsp";
  }

  @Route(value = "/forgotpassword", respondsToMethods = {HttpMethod.GET})
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
       <td><pre>GET /login HTTP/1.1</pre></td>
       <td><pre>get(request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">The root level path match is specified as <i>/login</i> in (<a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/Routes.html#value()">Routes.value</a>)
      since @Route doesn't specify a value, this is the full path matched for this method.</td>
    </tr>
    <tr>
       <td><pre>GET /login/forgotpassword HTTP/1.1</pre></td>
       <td><pre>showForgotPassword(request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">The path specified @Route is appended onto the path specified in @Routes to form the full match path <i>/login/forgotpassword</li>. The JSP file at <i>/login/forgotpassword.jsp</i> will be rendered to the user
      since @Routes.forwardPath starts with a <i>/</i>.</td>
    </tr>
    <tr>
       <td><pre>POST /login/forgotpassword HTTP/1.1</pre></td>
       <td><pre>postForgotPassword(request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">Since @Routes.routeUnannotatedPublicMethods is true, this public method is a candidate for HTTP requests. Both the path and accepted HTTP methods are taken by convention
      from the method name. Since @Routes.value is specified the path taken from this method name is appended to this value to form the full match path (<i>/login/forgotpassword</i>).</td>
    </tr>
    <tr>
       <td><pre>GET /login/someresource HTTP/1.1</pre></td>
       <td><pre>404</pre></td>
    </tr>
    <tr>
       <td><pre>GET /someresource HTTP/1.1</pre></td>
       <td><pre>404</pre></td>
    </tr>
    <tr>
      <td colspan="2">@Routes.routeUnannotatedPublicMethods only applies to the immediate class. Public, unannotated methods from super classes are not HTTP request candidates.</td>
    </tr>
    <tr>
       <td><pre>GET /login/faq HTTP/1.1</pre></td>
       <td><pre>getFAQ(request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">Annotated super class methods are candidates for HTTP requests. If BaseRoutes declared a @Routes it would be ignored, @Routes is only used if present on the immediate
      class (MyLoginRoutes).</td>
    </tr>

  </tbody>
</table>


### Example Four: Parameter Matching
The previous examples use path matching exclusively to determine how HTTP requests are mapped. Request parameters can also be used as a criteria for method matching.

```Java
@Routes("/api")
public class ApiRoutes
{
  @Route("/users?expired=true")
  public String getExpiredUsers(HttpServletRequest request)
  {
    ...
    return "expiredUsers.jsp";
  }

  @Route("/users?expired=false", defaultParameters="expired=false")
  public String getActiveUsers(HttpServletRequest request)
  {
    ...
    return "activeUsers.jsp";
  }

  @Route("/users?expired=false&admin=true", defaultParameters="expired=false")
  public String getActiveAdministrators(HttpServletRequest request)
  {
    ...
    return "activeAdministrators.jsp";
  }

  @Route("/users?expired=false&admin=true", defaultParameters="expired=false")
  public String postActiveAdministrators(HttpServletRequest request)
  {
    ...
    return "activeAdministrators.jsp";
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
       <td><pre>GET /api/users?expired=true&format=xml HTTP/1.1</pre></td>
       <td><pre>getExpiredUsers(request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">Parameter matching is specified by adding a query string to the end @Route.value. All parameters specified in this query must be present and equal
      to the value specified for a match to be made on the method. Any additional parameters provided in the request that aren't specified in the query @Route.value query
      string will be ignored.</td>
    </tr>
    <tr>
       <td><pre>GET /api/users?expired=TRUE HTTP/1.1</pre></td>
       <td><pre>getExpiredUsers(request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">By default matching in Routes for paths and parameters is case insensitive. This can be changed in
      <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RoutesConfiguration.html#caseInsensitive">RoutesConfiguration.caseInsensitve</a>.</td>
    </tr>
    <tr>
       <td><pre>GET /api/users?format=json HTTP/1.1</pre></td>
       <td><pre>getActiveUsers(request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">If default values are specified for parameters using <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/Route.html#defaultParameters()">@Route.defaultParameters</a>
      then the default parameter value will be used for the match comparison if the parameter is not provided in the request.</td>
    </tr>
    <tr>
       <td><pre>GET /api/users?expired=false&admin=true HTTP/1.1</pre></td>
       <td><pre>getActiveAdministrators(request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">Multiple parameters are delimited with <i>&</i>. Route methods with more parameter checks will be checked first which is why getActiveAdministrators is called here instead of
      getActiveUsers even though getActiveUsers is listed first.</td>
    </tr>
    <tr>
       <td><pre>POST /api/users HTTP/1.1
Content-Type: application/x-www-form-urlencoded

expired=false&active=true</pre></td>
       <td><pre>postActiveAdministrators(request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">Parameters can be specified from the query string or from the body content when form encoded.</td>
    </tr>

  </tbody>
</table>


### Example Four: Pattern Matching
So far all path and parameter matching has been with fixed values but Routes also supports regular expressions. To use a regular expression place the value between curly brackets `{}`. The value
between these brackets must be a valid <a href="http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html">Java regular expressions</a> with the following exceptions:

* `{*}` The single wildcard is a shortcut for `{.*}`
* `{**}` A double wildcard can only be used for url paths and indicates it match against more than one path segment.
* `{}` An empty set of brackets indicates that the regular expression is specified by a method parameter (next section).

Since the exact value of path segments and parameters aren't known when a Routes method is invoked (all that is known is that the value matches the expression) these
values can be input parameters to the method. Routes doesn't use method parameter annotations since it's the author's opinion that these hurt the readability of code.
Instead method pattern values are set in the order they are specified in @Route. You can any of the following types for pattern parameters:

* String
* Character
* char
* Boolean
* boolean
* Byte
* byte
* Short
* short
* Integer
* int
* Long
* long
* Float
* float
* Double
* double

```Java
@Routes("/users")
public class UserRoutes
{
  @Route("{\d+}")
  public String getUserByIdInPath(int userId, HttpServletRequest request)
  {...}

  @Route("?id={\d+}")
  public String getUserByIdInParamter(int userId, HttpServletRequest request)
  {...}

  @Route("/{*}")
  public String getUserByName(String userName, HttpServletRequest request)
  {...}

  @Route("/{\d+}/{.+-.+}")
  public String getShowUserProfileInPath(int userId, HttpServletRequest request)
  {...}

  @Route("/{\d+}?profileName={*}")
  public String getShowUserProfileInParameter(int userId,
                                              HttpServletRequest request,
                                              String profileName)
  {...}

  @Route("/{*}/changepassword", defaultParameters="expired=false")
  public String getChangePasswordById(int userId, HttpServletRequest request)
  {...}

  @Route("/{**}")
  public String getCustomUsersNotFoundPage(HttpServletRequest request)
  {...}
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
       <td><pre>GET /users/23 HTTP/1.1</pre></td>
       <td><pre>getUserByIdInPath(23, request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">The regular expression <i>{\d+}</i> will match any numeric value. The numeric value matched will be provided in the <i>userId</i> parameter when invoked. If this method had no
      Integer parameter the HTTP request would still be matched and the method would be invoked without the value being provided.</td>
    </tr>
    <tr>
       <td><pre>GET /users?id=23 HTTP/1.1</pre></td>
       <td><pre>getUserByIdInParamter(23, request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">Regular expression for parameters work the same as paths.</td>
    </tr>
    <tr>
       <td><pre>GET /users?id=baswerc HTTP/1.1</pre></td>
       <td><pre>404</pre></td>
    </tr>
    <tr>
      <td colspan="2">The HTTP request does match any Routes because <i>baswerc</i> does not match the pattern <i>{\d+}</i>.</td>
    </tr>
    <tr>
       <td><pre>GET /users/baswerc HTTP/1.1</pre></td>
       <td><pre>getUserByName("baswerc", request)</pre></td>
    </tr>
    <tr>
       <td><pre>GET /users/23A HTTP/1.1</pre></td>
       <td><pre>getUserByName("23A", request)</pre></td>
    </tr>
    <tr>
       <td><pre>GET /users HTTP/1.1</pre></td>
       <td><pre>404</pre></td>
    </tr>
    <tr>
       <td><pre>GET /users/ HTTP/1.1</pre></td>
       <td><pre>404</pre></td>
    </tr>
    <tr>
      <td colspan="2">The wildcard pattern <i>{*}</i> will match against any value. In this example any value in the segment after <i>/users</i> that is not numeric will be matched
      to this method (since the numeric pattern method <i>getUsersByIdInPath</i> comes first in the class declaration it takes precedence). Note a wildcard declaration in a path or
      parameter will match against any value but the value must present (empty is not a match.</td>
    </tr>
    <tr>
       <td><pre>GET /users/23/basic-blue HTTP/1.1</pre></td>
       <td><pre>getShowUserProfileInPath(23, request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">Pattern values are supplied as method parameters in the order they were specified. You don't have to specify method parameters for all patterns in the route.</td>
    </tr>
    <tr>
       <td><pre>GET /users/23?profile=basic-blue HTTP/1.1</pre></td>
       <td><pre>getShowUserProfileInParameter(23,
request, "profile-basic")</pre></td>
    </tr>
    <tr>
      <td colspan="2">Pattern value parameters can be intermingled with the other allowed route method parameter types (ex. HttpServletRequest).</td>
    </tr>
    <tr>
       <td><pre>GET /users/23/changepassword HTTP/1.1</pre></td>
       <td><pre>getChangePasswordById(23, request)</pre></td>
    </tr>
    <tr>
       <td><pre>GET /users/baswerc/changepassword HTTP/1.1</pre></td>
       <td><pre>throw new RoutesException()</pre></td>
    </tr>
    <tr>
      <td colspan="2">Routes does not try to verify that path or parameter patterns are correctly mapped to method parameter types. If a path or parameter value cannot be coerced into
      method parameter value (ex. NumberFormatException) a <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RoutesException.html">RoutesException</a> will be thrown
      and the request will end in error. Method parameter patterns discussed in this section can help with this.</td>
    </tr>
    <tr>
       <td><pre>GET /users/something/else/goes/here HTTP/1.1</pre></td>
       <td><pre>getCustomUsersNotFoundPage(request)</pre></td>
    </tr>
    <tr>
       <td><pre>GET /users/23/abc HTTP/1.1</pre></td>
       <td><pre>getCustomUsersNotFoundPage(request)</pre></td>
    </tr>
    <tr>
       <td><pre>GET /users?one=1 HTTP/1.1</pre></td>
       <td><pre>getCustomUsersNotFoundPage(request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">The double wildcard will match any arbitrary number of path segments that aren't matched by other criteria. Since the double wildcard can consume multiple path
      segments it will not match to any method parameters.</td>
    </tr>
  </tbody>
</table>

### Example Five: Method Parameter Patterns
Method parameter patterns are specified by an empty set of curly brackets _{}_. When these are used it means the pattern to be used should be inferred from the method parameter this pattern
value is mapped to. The method parameter type must be one of the standard types defined in the previous section (Boolean, Byte, Short, Integer, Long, etc.) and it must be present in the method
declaration. A Routes exception will be thrown when the RoutingTable is built if a method parameter pattern is specified in the path or parameter section and there is no matching method parameter. For
example the following routes class is invalid.
```Java
@Routes("/invalid")
public class InvalidRoutes
{
  @Route("{}") // No matching method parameter to specify what regular expression is used
  public String getInvalidRoute(HttpServletRequest request)
  {...}
}
```
And the following will result in a RoutesException `routingTable.add(InvalidRoutes.class).build()`.
```Java
@Routes("/users")
public class UserRoutes
{
  @Route("{}")
  public String getUserByIdInPath(int userId, HttpServletRequest request)
  {...}

  @Route("?id={}")
  public String getUserByIdInParamter(int userId, HttpServletRequest request)
  {...}

  @Route("/{}")
  public String getUserByName(String userName, HttpServletRequest request)
  {...}

  @Route("/{}/{.+-.+}")
  public String getShowUserProfileInPath(int userId, String profile, HttpServletRequest request)
  {...}

  @Route("/{}?profileName={}")
  public String getShowUserProfileInParameter(int userId,
                                              HttpServletRequest request,
                                              String profileName)
  {...}
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
       <td><pre>GET /users/23 HTTP/1.1</pre></td>
       <td><pre>getUserByIdInPath(23, request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">The .</td>
    </tr>
    <tr>
       <td><pre>GET /users?id=23 HTTP/1.1</pre></td>
       <td><pre>getUserByIdInParamter(23, request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">Regular expression for parameters work the same as paths.</td>
    </tr>
    <tr>
       <td><pre>GET /users?id=baswerc HTTP/1.1</pre></td>
       <td><pre>404</pre></td>
    </tr>
    <tr>
      <td colspan="2">The HTTP request does match any Routes because <i>baswerc</i> does not match the pattern <i>{\d+}</i>.</td>
    </tr>
    <tr>
       <td><pre>GET /users/baswerc HTTP/1.1</pre></td>
       <td><pre>getUserByName("baswerc", request)</pre></td>
    </tr>
    <tr>
       <td><pre>GET /users/23A HTTP/1.1</pre></td>
       <td><pre>getUserByName("23A", request)</pre></td>
    </tr>
    <tr>
       <td><pre>GET /users HTTP/1.1</pre></td>
       <td><pre>404</pre></td>
    </tr>
    <tr>
       <td><pre>GET /users/ HTTP/1.1</pre></td>
       <td><pre>404</pre></td>
    </tr>
    <tr>
      <td colspan="2">The wildcard pattern <i>{*}</i> will match against any value. In this example any value in the segment after <i>/users</i> that is not numeric will be matched
      to this method (since the numeric pattern method <i>getUsersByIdInPath</i> comes first in the class declaration it takes precedence). Note a wildcard declaration in a path or
      parameter will match against any value but the value must present (empty is not a match.</td>
    </tr>
    <tr>
       <td><pre>GET /users/23/basic-blue HTTP/1.1</pre></td>
       <td><pre>getShowUserProfileInPath(23, request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">Pattern values are supplied as method parameters in the order they were specified. You don't have to specify method parameters for all patterns in the route.</td>
    </tr>
    <tr>
       <td><pre>GET /users/23?profile=basic-blue HTTP/1.1</pre></td>
       <td><pre>getShowUserProfileInParameter(23,
request, "profile-basic")</pre></td>
    </tr>
    <tr>
      <td colspan="2">Pattern value parameters can be intermingled with the other allowed route method parameter types (ex. HttpServletRequest).</td>
    </tr>
    <tr>
       <td><pre>GET /users/23/changepassword HTTP/1.1</pre></td>
       <td><pre>getChangePasswordById(23, request)</pre></td>
    </tr>
    <tr>
       <td><pre>GET /users/baswerc/changepassword HTTP/1.1</pre></td>
       <td><pre>throw new RoutesException()</pre></td>
    </tr>
    <tr>
      <td colspan="2">Routes does not try to verify that path or parameter patterns are correctly mapped to method parameter types. If a path or parameter value cannot be coerced into
      method parameter value (ex. NumberFormatException) a <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RoutesException.html">RoutesException</a> will be thrown
      and the request will end in error. Method parameter patterns discussed in this section can help with this.</td>
    </tr>
  </tbody>
</table>


### Example Six: Responding To Media Types

## Routes Helpers

## Pre & Post Route Events

## Routes Configuration

## Supported Libraries

## Routes Meta Page

