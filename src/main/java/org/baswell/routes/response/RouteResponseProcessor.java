package org.baswell.routes.response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.google.gson.Gson;
import org.baswell.routes.Format;
import org.baswell.routes.RouteConfig;
import org.baswell.routes.RoutesConfig;
import org.json.simple.JSONObject;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class RouteResponseProcessor
{
  private final RoutesConfig routesConfig;

  private final AvailableLibraries availableLibraries = new AvailableLibraries();

  public RouteResponseProcessor(RoutesConfig routesConfig)
  {
    this.routesConfig = routesConfig;
  }
  
  public void processResponse(RouteResponseType routeResponseType, Object response, Format format, RouteConfig routeConfig, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException, ServletException
  {
    if (response != null)
    {
      switch (routeResponseType)
      {
        case BYTES_CONTENT:
          servletResponse.getOutputStream().write((byte[])response);
          break;

        case STREAM_CONTENT:
          fastChannelCopy((InputStream)response, servletResponse.getOutputStream(), routesConfig.streamBufferSize);
          break;
          
        case STRING_CONTENT:
          if (response instanceof String)
          {
            servletResponse.getWriter().write((String)response);
          }
          else
          {
            switch (format.type)
            {
              case JSON:
                sendJson(response, servletResponse);
                break;

              case XML:
                sendXML(response, servletResponse);
                break;

              default:
                servletResponse.getWriter().write(response.toString());
                break;
            }
          }
          break;
          
        case FORWARD_DISPATCH:
          String path = response.toString();
          if (!path.startsWith("/"))
          {
            path = routeConfig.forwardPath + path;
          }
          
          servletRequest.getRequestDispatcher(path).forward(servletRequest, servletResponse);
          break;
          
        case VOID:
          break;
      }
    }
  }

  void sendJson(Object response, HttpServletResponse servletResponse) throws IOException
  {
    if (availableLibraries.jsonSimpleAvailable() && (response instanceof JSONObject))
    {
      ((JSONObject)response).writeJSONString(servletResponse.getWriter());
    }
    else if (availableLibraries.gsonAvailable())
    {
      Gson gson = new Gson();
      servletResponse.getWriter().write(gson.toJson(response));
    }
    else
    {
      servletResponse.getWriter().write(response.toString());
    }
  }

  void sendXML(Object response, HttpServletResponse servletResponse) throws IOException
  {
    if (response instanceof Node)
    {
      try
      {
        TransformerFactory.newInstance().newTransformer().transform(new DOMSource((Node) response), new StreamResult(servletResponse.getOutputStream()));
      }
      catch (TransformerException e)
      {
        throw new RuntimeException(e);
      }
    }
    else if (response.getClass().getAnnotation(XmlRootElement.class) != null)
    {
      try
      {
        JAXBContext.newInstance(response.getClass()).createMarshaller().marshal(response, servletResponse.getOutputStream());
      }
      catch (JAXBException e)
      {
        throw new RuntimeException(e);
      }
    }
  }


    static void fastChannelCopy(InputStream inStream, OutputStream outStream, int bufferSize) throws IOException
  {
    final ReadableByteChannel src = Channels.newChannel(inStream);
    final WritableByteChannel dest = Channels.newChannel(outStream);
    final ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);
    while (src.read(buffer) != -1)
    {
      // prepare the buffer to be drained
      buffer.flip();
      // write to the channel, may block
      dest.write(buffer);
      // If partial transfer, shift remainder down
      // If buffer is empty, same as doing clear()
      buffer.compact();
    }
    // EOF will leave buffer in fill state
    buffer.flip();
    // make sure the buffer is fully drained.
    while (buffer.hasRemaining())
    {
      dest.write(buffer);
    }
  }

}
