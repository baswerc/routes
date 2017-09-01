Routes
======

Routes is a Java library for mapping HTTP requests to Java object methods. Routes runs within a Java servlet container and is an alternative to processing HTTP requests with the Servlet
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
into an object that accepts HTTP _GET_ requests at the path _/api/users_ and renders the loaded users with the JSP file _/WEB-INF/jsps/users.jsp_.

## Getting Started

### Direct Download
You can download <a href="https://github.com/baswerc/routes/releases/download/v1.3/routes-1.3.jar">routes-1.3.jar</a> directly and place in your project.

### Using Maven
Add the following dependency into your Maven project:

````xml
<dependency>
    <groupId>org.baswell</groupId>
    <artifactId>routes</artifactId>
    <version>1.3</version>
</dependency>
````

### Dependencies
Routes runs within a Java Servlet container at API 2.4 or greater. Routes has no other external dependencies.

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

This filter should be placed last in your filter chain because chain processing will end here when a route match is found (`chain.doFilter` is not called).
If no match is found, then `chain.doFilter` will be called so further processing can occur. This allows, for example, your application to serve up file resources 
(ex. html, jsp) directly as long as none of your routes match the file resource URL.

In addition to the `filter-mapping` configuration, you can control which HTTP requests are candidates for routes with the `ONLY` and `EXCEPT` filter parameters
(this can improve performance when it's known that certain HTTP paths won't map to routes).

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
The <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RoutingTable.html">RoutingTable</a> is where you tell Routes which of your classes are candidates for HTTP requests. After your objects have
been added call the `build()` method to build the routing table. This method will throw a `RoutesException` if you have any invalid route configuration.

```Java
RoutingTable routingTable = new RoutingTable();
routingTable.add(new IndexRoutes(), new HomeRoutes(), new HelpRoutes()).build();
```

The `RoutingTable` should be treated as a singleton in your application. A static attribute will be set by the `RoutingTable` when constructed that the `RoutingServlet` and `RoutingFilter` will use when called. 

You can either add your route class objects or instance objects to the `RoutingTable`.

```Java
// Both of these are acceptable
routingTable.add(IndexRoutes.class);
routingTable.add(new HomeRoutes());
```

If you add a class object then Routes will instantiate a new instance of this class (using the default constructor) for each matched request (the instantiation strategy can be controlled by using
<a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RouteInstancePool.html">RouteInstancePool</a>). If you add an instance object then that instance object will be used for every
matched HTTP request. This means the route class *must be thread safe*.

If you are using Spring for dependency injection you can configure the `RoutingTable` using the `setRoutes()` method as:

```XML
<bean id="routingTable" class="org.baswell.routes.RoutingTable" init-method="build">
  <property name="routes">
    <list>
      <ref bean="loginRoutes"/>
      <ref bean="homeRoutes"/>
      <ref bean="helpRoutes"/>
    </list>
  </property>
</bean>
```

## Routes By Example

Routes imposes no class hierarchies or interfaces on your classes. There are two ways to tell Routes how your Java objects are matched to HTTP requests, by convention or by using annotations. If your class has no
Routes annotations then all public methods are candidates to being matched to incoming HTTP requests (only the immediate class, public methods of any super classes are not candidates).
Routes use a convention for converting unannotated classes and methods to HTTP candidates that you can override using <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RouteByConvention.html">RouteByConvention</a>.

The annotations <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/Routes.html">Routes</a> (class level) and <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/Route.html">Route</a> (method level)
provide full control over how methods are matched to HTTP requests. By default if your class has at least one of these annotations then only methods with the `Route` annotations can be matched to HTTP requests (public, unannotated
methods will not be candidates). This can be override for the entire class with <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/Routes.html#routeUnannotatedPublicMethods()">Routes.routeUnannotatedPublicMethods</a> or
globally with <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RoutesConfiguration.html#routeUnannotatedPublicMethods">RoutesConfiguration.routeUnannotatedPublicMethods</a>.

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

  public String postForgotPassword(HttpServletRequest request)
  {
    ...
    throw new RedirectTo("/login");
  }
}
```

<table>
  <tbody>
    <tr>
      <th align="left">HTTP Request</th>
      <th align="left">Method Called</t>
    </tr>
    <tr>
       <td><pre>GET /login HTTP/1.1</pre></td>
       <td><pre>get(request)</pre></td>
    </tr>
<tr>
  <td colspan="2">By default the class name is used to form the first url segment, in this case <i>/login</i>. If the class name ends in <i>Routes</i>, <i>Route</i>, <i>Controller</i>, or
   <i>Handler</i> then this part of the class name will be removed from the path segment.Method names that just contain HTTP methods (ex. <i>get</i>, <i>post</i>)
  don't add anything to the matched path. The JSP file at <i>/WEB-INF/jsps/login.jsp</i> will be rendered to the user. You can change the root JSP directory with
  <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RoutesConfiguration.html#rootForwardPath">RoutesConfiguration.rootForwardPath</a></td>
</tr>
<tr>
   <td><pre>POST /login HTTP/1.1</pre></td>
   <td><pre>post(request, response)</pre></td>
</tr>
<tr>
  <td colspan="2">Since this method does not return anything, it must handle the content sent back to the user with the <code>HttpServletResponse</code> object.</td>
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
  <td colspan="2">By default matching in Routes for paths and parameters is case insensitive. This can be changed with
  <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RoutesConfiguration.html#caseInsensitive">RoutesConfiguration.caseInsensitve</a>.</td>
</tr>
<tr>
   <td><pre>POST /login/forgotpassword HTTP/1.1</pre></td>
   <td><pre>postForgotPassword(request)</pre></td>
</tr>
<tr>
  <td colspan="2">You can use the helper class <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RedirectTo.html">RedirectTo</a> to redirect the
  client to another page. Note that when this exception is thrown any <i>@AfterRoute</i> methods will not be called. If you want the after route callbacks to still take
  place you can return a string starting with the key <i>redirect:</i> such as <i>redirect:/login</i> or call <i>HttpServletResponse.sendRedirect</i> directly.</td>
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
  <tbody>
    <tr>
      <th align="left">HTTP Request</th>
      <th align="left">Method Called</t>
    </tr>
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
    <tr><td colspan="2"></td></tr>
    <tr>
       <td><pre>PUT /login HTTP/1.1</pre></td>
       <td><pre>doLogin(request, response)</pre></td>
    </tr>
    <tr>
      <td colspan="2">Since both <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/Routes.html#value()">Routes.value</a> and <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/Route.html#respondsToMethods()">Route.respondsToMethod</a> are
       specified the method name has no impact on which HTTP requests are matched.</td>
    </tr>
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
      <td colspan="2">Since <code>postForgotPassword</code> is not annotated, it is not a candidate for HTTP requests. This can be overridden using <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/Routes.html#routeUnannotatedPublicMethods()">Routes.routeUnannotatedPublicMethods</a>
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

  public String postForgotPassword(HttpServletRequest request)
  {
    ...
    return "redirect:/login";
  }
}
```
<table>
  <tbody>
    <tr>
      <th align="left">HTTP Request</th>
      <th align="left">Method Called</t>
    </tr>
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
      from the method name. Since @Routes.value is specified the path taken from this method name is appended to this value to form the full match path (<i>/login/forgotpassword</i>). If the returned string starts with
      <i>redirect:</i> then a redirect (302) will be returned to the client with text after the <i>redirect:</i> key sent as the URL to redirect to.</td>
    </tr>
    <tr>
       <td><pre>GET /login/someresource HTTP/1.1</pre></td>
       <td><pre>404</pre></td>
    </tr>
    <tr><td colspan="2"></td></tr>
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
  <tbody>
    <tr>
      <th align="left">HTTP Request</th>
      <th align="left">Method Called</t>
    </tr>
    <tr>
       <td><pre>GET /api/users?expired=true&mediaType=xml HTTP/1.1</pre></td>
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
       <td><pre>GET /api/users?mediaType=json HTTP/1.1</pre></td>
       <td><pre>getActiveUsers(request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">If default values are specified for parameters using <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/Route.html#defaultParameters()">@Route.defaultParameters</a>
      then the default parameter value will be used for the match comparison if the parameter is not provided in the request.</td>
    </tr>
    <tr>
       <td><pre>GET /api/users?expired=false&admin=true HTTP/1.1</pre></td>
       <td><pre>getActiveAdministrators(
request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">Multiple parameters are delimited with <i>&</i>. Route methods with more parameter checks will be checked first which is why <code>getActiveAdministrators</code> is called here instead of
      <code>getActiveUsers</code> even though <code>getActiveUsers</code> is listed first.</td>
    </tr>
    <tr>
       <td><pre>POST /api/users HTTP/1.1
Content-Type: application/x-www-form-urlencoded

expired=false&active=true</pre></td>
       <td><pre>postActiveAdministrators(
request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">Parameters can be specified from the query string or from the body content when form encoded.</td>
    </tr>

  </tbody>
</table>


### Example Five: Pattern Matching
Routes supports the use of regular expressions for url path and parameter matching. To use a regular expression place the value between curly brackets `{}` such as `/users/{\d+}`. The value
between these brackets must be a valid  <a href="http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html">Java regular expressions</a> with the following exceptions:

* `{*}` Matches any value. Shortcut for `{.*}`.
* `{**}` A double wildcard can only be used for url paths. It matches one or more url path segments of any value.
* `{}` An empty set of brackets indicates that the regular expression is specified by a method parameter (<a href="#user-content-example-six-method-parameter-patterns">next section</a>).

The value matched against a path or parameter pattern can be passed in as a method parameter when the route method is invoked. The following basic Java types are supported for path or parameter
values.

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

Routes uses the standard parse methods (`Boolean.parseBoolean`, `Integer.parseInt`, `Float.parseFlow`) to coerce a pattern value into a method parameter. A `String` method parameter type will receive the matched value
unmodified and a `Character` parameter will receive the first character of the matched value.

Routes matches patterns to method parameters by order. So for example in the route criteria `/users/{\d+}/profile/{*}` the first pattern `{\d+}` will map to the first method parameter that is one of the the types above. The second pattern
`{*}` will match to the second method parameter of those types. You don't have to specify method parameters for each pattern but because they are assigned in order, if for example you want the value for the second pattern assigned to a
method parameter you have to map the first pattern to a method parameter too.

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
  <tbody>
    <tr>
      <th align="left">HTTP Request</th>
      <th align="left">Method Called</t>
    </tr>
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
    <tr><td colspan="2"></td></tr>
    <tr>
       <td><pre>GET /users/23A HTTP/1.1</pre></td>
       <td><pre>getUserByName("23A", request)</pre></td>
    </tr>
    <tr><td colspan="2"></td></tr>
    <tr>
       <td><pre>GET /users HTTP/1.1</pre></td>
       <td><pre>404</pre></td>
    </tr>
    <tr><td colspan="2"></td></tr>
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
    <tr><td colspan="2"></td></tr>
    <tr>
       <td><pre>GET /users/baswerc/changepassword HTTP/1.1</pre></td>
       <td><pre>throw new RoutesException()</pre></td>
    </tr>
    <tr>
      <td colspan="2">Routes does not try to verify that path or parameter patterns are correctly mapped to method parameter types. If a path or parameter value cannot be coerced into
      method parameter value (ex. NumberFormatException) a <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RoutesException.html">RoutesException</a> will be thrown
      and the request will end in error. <a href="#user-content-example-six-method-parameter-patterns">Method parameter patterns</a> discussed in this section can help with this.</td>
    </tr>
    <tr>
       <td><pre>GET /users/something/else/goes/here HTTP/1.1</pre></td>
       <td><pre>getCustomUsersNotFoundPage(request)</pre></td>
    </tr>
    <tr><td colspan="2"></td></tr>
    <tr>
       <td><pre>GET /users/23/abc HTTP/1.1</pre></td>
       <td><pre>getCustomUsersNotFoundPage(request)</pre></td>
    </tr>
    <tr><td colspan="2"></td></tr>
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

### Example Six: Method Parameter Patterns
Method parameter patterns are specified by an empty set of curly brackets `{}`. The regular expression used for these is inferred from the method parameter this pattern
is mapped to. The method parameter must be one of the standard types defined in the previous section (`Boolean`, `Byte`, `Short`, `Integer`, `Long`, etc.) and it must be present in the method
declaration. An exception will be thrown when `{}` is used in the route criteria and there is no matching method parameter. For example the following routes class is invalid.
```Java
public class InvalidRoutes
{
  // {} matches to the method parameter id
  @Route("/valid/{}") 
  public String getValidRoute(int id, HttpServletRequest request)
  {...}

  // No matching method parameter to specify what regular expression is used
  @Route("/invalid/{}") 
  public String getInvalidRoute(HttpServletRequest request)
  {...}
}
```
The following will result in a <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RoutesException.html">RoutesException</a> being thrown `routingTable.add(InvalidRoutes.class).build()`.

```Java
@Routes("/users")
public class UserRoutes
{
  @Route("{}?showDetails={}")
  public String getUserByIdInPath(int userId, 
                                  boolean showDetails, 
                                  HttpServletRequest request)
  {...}

  @Route("?id={}")
  public String getUserByIdInParamter(int userId, HttpServletRequest request)
  {...}

  @Route("/{}")
  public String getUserByName(String userName, HttpServletRequest request)
  {...}

  @Route("/{}/{.+-.+}")
  public String getShowUserProfileInPath(int userId,
                                         String profile,
                                         HttpServletRequest request)
  {...}

  @Route("/{}?profileName={}")
  public String getShowUserProfileInParameter(int userId,
                                              HttpServletRequest request,
                                              String profileName)
  {...}
}
```
<table>
  <tbody>
    <tr>
      <th align="left">HTTP Request</th>
      <th align="left">Method Called</t>
    </tr>
    <tr>
       <td><pre>GET /users/23?showDetails=true HTTP/1.1</pre></td>
       <td><pre>getUserByIdInPath(23, true, request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">Since userId is of type <code>int</code>, the first pattern used with match any value that can be coerced into a <code>int</code>. For the <code>boolean</code> type showDetails, the pattern used will match any (case insentive) value of <code>true</code> or <code>false</code>.</td>
    </tr>
    <tr>
       <td><pre>GET /users?id=23 HTTP/1.1</pre></td>
       <td><pre>getUserByIdInParamter(23, request)</pre></td>
    </tr>
    <tr><td colspan="2"></td></tr>
    <tr>
       <td><pre>GET /users?id=baswerc HTTP/1.1</pre></td>
       <td><pre>404</pre></td>
    </tr>
    <tr>
      <td colspan="2">Method parameter patterns can be used for url path and parameter matching. "baswerc" cannot be coerced into an int so a match is not made.</td>
    </tr>
    <tr>
      <td><pre>GET /users/baswerc HTTP/1.1</pre></td>
      <td><pre>getUserByName("baswerc", request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">String method parameters will match any value. It's the same as using the wildcard pattern <code>{*}</code>.</td>
    </tr>
    <tr>
       <td><pre>GET /users/23/basic-blue HTTP/1.1</pre></td>
       <td><pre>getShowUserProfileInPath(23,
"basic-blue", request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">Method parameter pattern and explicit patterns can be mixed.</td>
    </tr>
    <tr>
       <td><pre>GET /users/23?profile=basic-blue HTTP/1.1</pre></td>
       <td><pre>getShowUserProfileInParameter(23,
request, "profile-basic")</pre></td>
    </tr>
    <tr>
      <td colspan="2">Method pattern parameters can be mix in with other allowed parameter types.</td>
    </tr>
  </tbody>
</table>

### Example Six: Responding To Media Type Requests
An additional, optional criteria that can be placed on Route methods is the <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/MediaType.html">MediaType</a> the client is expecting for the response.
<a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/Route.html#respondsToMediaRequests()">@Route.responsesToMediaRequests</a> is used to specify which type of media this route method is capable of serving.
There are three different ways that Routes determines the type of media the client is expecting in the response. These are listed in the order of precedence.

* The value of the _mediaType_ parameter (if present). For example the url request _/api/users?mediaType=xml_ will map to <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/MediaType.html#XML">XML</a>
* File extension (if present) of the URL path. For example a request for _/test.pdf_ will map to <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/MediaType.html#PDF">PDF</a>.
* The value of the _Accept_ header (if present). For example the header _Accept: application/json_ will map to <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/MediaType.html#JSON">JSON</a>.


```Java
@Routes("/api")
public class APIRoutes
{
  @Route(value="/users", respondsToMediaRequests=MediaType.JSON)
  public String getUsersInJSON(HttpServletRequest request)
  {...}

  @Route(value="/users", respondsToMediaRequests=MediaType.XML)
  public String getUsersInXML(HttpServletRequest request)
  {...}

  @Route(value="/users", respondsToMediaRequests={MediaType.HTML, MediaType.PDF})
  public String getUsersInHTMLOrPDF(HttpServletRequest request)
  {...}

  @Route(value="/users/{*}", respondsToMediaRequests=MediaType.PDF)
  public String getUsersReportPDF(String reportName, HttpServletRequest request)
  {...}
}
```
<table>
  <tbody>
    <tr>
      <th align="left">HTTP Request</th>
      <th align="left">Method Called</t>
    </tr>
    <tr>
       <td><pre>GET /users?mediaType=json HTTP/1.1</pre></td>
       <td><pre>getUsersInJSON(request)</pre></td>
    </tr>
    <tr><td colspan="2"></td></tr>
    <tr>
       <td><pre>GET /users HTTP/1.1
Accept: application/json</pre></td>
       <td><pre>getUsersInJSON(request)</pre></td>
    </tr>
    <tr><td colspan="2"></td></tr>
    <tr>
       <td><pre>GET /users HTTP/1.1
Accept: application/xhtml+xml</pre></td>
       <td><pre>404</pre></td>
    </tr>
    <tr>
      <td colspan="2">The value of the <i>mediaType</i> parameter should be the value of the <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/MediaType.html">MediaType</a> enumeration. The value of the <i>Accept</i>
      header should be one of the <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/MediaType.html">MIMETypes</a>. If none of the media criteria matches then the route method will not be matched</td>
    </tr>
    <tr>
       <td><pre>GET /users?mediaType=xml HTTP/1.1
Accept: application/json</pre></td>
       <td><pre>getUsersInXML(request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">The <i>mediaType</i> parameter takes precedence over the <i>Accept</i> header.</td>
    </tr>
    <tr>
       <td><pre>GET /users HTTP/1.1
Accept: application/xhtml+xml</pre></td>
       <td><pre>getUsersInHTMLOrPDF(request)</pre></td>
    </tr>
    <tr><td colspan="2"></td></tr>
    <tr>
       <td><pre>GET /users HTTP/1.1
Accept: application/pdf</pre></td>
       <td><pre>getUsersInHTMLOrPDF(request)</pre></td>
    </tr>
    <tr>
      <td colspan="2">If multiple <code>MediaType</code> are specified in the route critieria then a match will be made if any of the specified <code>MediaType</code>s are requested.</td>
    </tr>
    <tr>
       <td><pre>GET /users/active.pdf HTTP/1.1
       <td><pre>getUsersReportPDF("active.pdf",
request)</pre></td>
    </tr>
    <tr><td colspan="2"></td></tr>
    <tr>
       <td><pre>GET /users/active.pdf?mediaType=xml HTTP/1.1
       <td><pre>404</pre></td>
    </tr>
    <tr>
      <td colspan="2">The <i>mediaType</i> parameter takes precedence over the file extension.</td>
    </tr>
  </tbody>
</table>

## Route Method Parameters

Your route methods can have the following parameter types.

* A simple type (`Integer`, `boolean`, etc.) mapped to a pattern in the route criteria. You can also define a `List` of one of these simple types (ex. `List<Short>`) if you expect the same parameter name to be used multiple times.
* <a href="http://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletRequest.html">HttpServletRequest</a>
* <a href="http://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletResponse.html">HttpServletResponse</a>
* <a href="http://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpSession.html">HttpSession</a>
* Request <a href="https://docs.oracle.com/javase/7/docs/api/java/net/URL.html">URL<a>
* <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RequestPath.html">RequestPath</a>
* <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RequestParameters.html">RequestParameters</a>
* <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RequestContent.html">RequestContent</a>
* <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RequestedMediaType.html">RequestedMediaType</a>
* Request parameters as `Map<String, List<String>>`
* Request parameters as `Map<String, String>`

If a route method declares any other parameter types then a <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RoutesException.html">RoutesException</a> will be thrown when the RoutingTable is built.

## Request Content

You can process the content a client sends in the request by using <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RequestContent.html">RequestContent</a> as a parameter to your route method. When you define the `RequestContent` container
you should specify the class that the request content will be mapped to.

```Java

@Routes("/api")
public class APIRoutes()
{
  @Route(expectedRequestMediaType = MediaType.JSON)
  public void putUser(RequestContent<User> requestContent) throws IOException
  {
    User user = requestContent.get();
    ...
  }
}
```

In this example Routes will try to map the submitted request content to the specified `User` class. Routes current supports the following libraries for automatic conversion of request content.

* <a href="https://code.google.com/p/json-simple/">JSONSimple</a>
* <a href="https://github.com/google/gson">GSON</a>
* <a href="https://github.com/FasterXML/jackson-core">Jackson</a>
* <a href="https://docs.oracle.com/javase/tutorial/jaxb/intro/">JAXB</a>
* <a href="http://docs.oracle.com/javase/7/docs/api/org/w3c/dom/package-summary.html">W3C DOM</a>
* <a href="http://www.jdom.org/">JDOM2</a>

To automatically convert the request content, Routes must determine the content type of request. The request content type is determined in the following order.

1. If the type of `RequestContent` requires a certain content type then Routes will use that content type. For example `RequestContent<org.json.simple.JSONObject>` dictates that the request content is JSON and `RequestContent<org.w3c.Node>` dictates the request content is XML.
2. If <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/Route.html#expectedRequestMediaType()">Route.expectedRequestMediaType</a> is specified then Routes will use that content type.
3. If the _Content-Type_ header is set it the request, Routes will use this content type.
4. Routes will (very rudimentary) try to guess the content type from the content submitted.

## Response Content

If your routes method returns something other than `String`, Routes will try to determine the type of the object and the correction response action based upon that type. For example if the routes method returns
a `org.json.simple.JSONObject`, Routes will set the content type of the response (if not already set) to _application/json_ and send `toJSONString()` as the response of the HTTP request.

For libraries such as <a href="https://github.com/google/gson">GSON</a> and <a href="https://github.com/FasterXML/jackson-core">Jackson</a> that operate on plain Java objects, you can give a hint to Routes on how to convert the returned object by using <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/Route.html#contentType()">@Route.contentType</a>.

```Java
@Routes("/api")
public class APIRoutes()
{
  // If GSON is available on your application's classpath, then Routes will send back the
  // users list as servletResponse.getWriter().write(new Gson().toJson(users));
  //
  // Otherwise if Jackson is available on your application's classpath then Routes will
  // send back the users list as new
  // ObjectMapper().writeValue(servletResponse.getWriter(), users);
  @Route(value="/users", contentType = MIMETypes.JSON)
  public List<User> getUsers()
  {
    ...
    return users;
  }
}
```

If Routes can not figure out how to convert your complex object it will simple call `toString()` on the returned object and return that as the content of the HTTP response.

Routes currently supports the following libraries for automatic conversion.

* <a href="https://code.google.com/p/json-simple/">JSONSimple</a>
* <a href="https://github.com/google/gson">GSON</a>
* <a href="https://github.com/FasterXML/jackson-core">Jackson</a>
* <a href="https://docs.oracle.com/javase/tutorial/jaxb/intro/">JAXB</a>
* <a href="http://docs.oracle.com/javase/7/docs/api/org/w3c/dom/package-summary.html">W3C DOM</a>
* <a href="http://www.jdom.org/">JDOM2</a>

## Pre & Post Route Events

You can tell Routes to make calls before and after a route method is invoked using the <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/BeforeRoute.html">@BeforeRoute</a> and
<a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/AfterRoute.html">@AfterRoute</a> annotations. Methods with these annotations must be in the same class hiearchy as the route method to be invoked.

@BeforeRoute and @AfterRoute methods can use all the same method parameter types as route methods except the criteria pattern parameters (`Integer`, `double`, etc.) are not allowed.

```Java
abstract class AuthenticatedRoutes
{
  @BeforeRoute(exceptTags="anonymousAllowed")
  public void requireLoggedIn(HttpServletRequest request)
  {
    ...
    if (!loggedIn)
    {
      // Request processing will end here, route method will
      // not be invoked.
      throw new RedirectTo("/login");
    }
  }

  @AfterRoute(exceptTags="anonymousAllowed")
  public void afterLoggedInRequest(URL url)
  {
    log.info("Logged in request: " + url);
  }
}

@Routes("/")
public class HomeRoutes extends AuthenticatedRoutes
{
  // BeforeRoute and AfterRoute methods called.
  @Route
  public String get(HttpServletRequest request)
  {...}

  // BeforeRoute and AfterRoute methods not called.
  @Route(value="/faq", tags="anonymousAllowed")
  public String getFAQ()
  {...}
}
```

## Routes Configuration

All of configuration for Routes is contained in <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RoutesConfiguration.html">RoutesConfiguration</a>. If you need to customize the default configuration then pass in this object to the
`RoutingTable` upon creation.

```Java
RoutesConfiguration configuration = new RoutesConfiguration();
configuration.routeUnannotatedPublicMethods = true
configuration.rootForwardPath = "/jsps";

RoutingTable routingTable = new RoutingTable(configuration);
...
```

## Routes Meta Page

Routes can serve up a web page that allows you test various URL paths, parameter and media type combinations to see which of your route methods will be selected. To enable this tool specify the path
for the meta page with <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/RoutesConfiguration.html#routesMetaPath">RoutesConfiguration.routesMetaPath</a>. You must make sure this meta
page does not collide with any of your route method criteria (the route methods will always win when this happens). If you want to deploy this utility in a non-development environment you can enable
authentication and authorization for this utility using <a href="http://baswerc.github.io/routes/javadoc/org/baswell/routes/MetaAuthenticator.html">MetaAuthenticator</a>.

![Image of Meta Page](http://baswerc.github.io/routes/meta.png)

# Additional Documentation

* <a href="http://baswerc.github.io/routes/javadoc/">Javadoc</a>

# Developed By

Corey Baswell - <a href="mailto:corey.baswell@gmail.com">corey.baswell@gmail.com</a>

# License
````
Copyright 2015 Corey Baswell

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
````
