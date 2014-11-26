package org.baswell.routes.meta;

import org.baswell.routes.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MetaHandler
{
  final RoutesConfig routesConfig;

  public MetaHandler(RoutesConfig routesConfig)
  {
    this.routesConfig = routesConfig;
  }

  public boolean handled(HttpServletRequest request, HttpServletResponse response, RequestPath path, RequestParameters parameters, HttpMethod httpMethod, Format format) throws IOException
  {
    if (path.startsWith(routesConfig.getRoutesMetaPath()))
    {
      path = path.pop();
      if ((format.type == Format.Type.HTML) && path.equals(""))
      {
        get(response);
        return true;
      }
    }

    return false;
  }

  public void get(HttpServletResponse response) throws IOException
  {
    response.getOutputStream().write(getIndexHtml("routes.html"));
  }

  byte[] getIndexHtml(String file) throws IOException
  {
    InputStream indexStream = MetaHandler.class.getResourceAsStream("/" + file);
    byte[] buffer = new byte[1024];
    ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
    int read;

    while ((read = indexStream.read(buffer)) != -1)
    {
      bytesOut.write(buffer, 0, read);
    }

    indexStream.close();

    return bytesOut.toByteArray();
  }
}
