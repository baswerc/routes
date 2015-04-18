package org.baswell.routes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.w3c.dom.Node;

class ResponseProcessor
{
  private final RoutesConfiguration routesConfiguration;

  private final AvailableLibraries availableLibraries = new AvailableLibraries();

  ResponseProcessor(RoutesConfiguration routesConfiguration)
  {
    this.routesConfiguration = routesConfiguration;
  }
  
  void processResponse(ResponseType responseType, ResponseStringWriteStrategy responseStringWriteStrategy, Object response, String contentType, RouteConfiguration routeConfiguration, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException, ServletException
  {
    if (response != null)
    {
      switch (responseType)
      {
        case BYTES_CONTENT:
          servletResponse.getOutputStream().write((byte[])response);
          break;

        case STREAM_CONTENT:
          fastChannelCopy((InputStream)response, servletResponse.getOutputStream(), routesConfiguration.streamBufferSize);
          break;
          
        case STRING_CONTENT:
          switch(responseStringWriteStrategy)
          {
            case GSON:
              sendGson(response, servletResponse);
              break;

            case W3C_NODE:
              sendNode(response, servletResponse);
              break;

            case JAXB:
              sendJaxb(response, servletResponse);
              break;

            case TO_STRING:
            default:
              sendToString(response, servletResponse);
              break;
          }
          break;
          
        case FORWARD_DISPATCH:
          String path = response.toString();
          if (!path.startsWith("/"))
          {
            path = routeConfiguration.forwardPath + path;
          }
          
          servletRequest.getRequestDispatcher(path).forward(servletRequest, servletResponse);
          break;
          
        case VOID:
          break;
      }
    }
  }

  void sendGson(Object response, HttpServletResponse servletResponse) throws IOException
  {
    servletResponse.getWriter().write(new Gson().toJson(response));
  }

  void sendNode(Object response, HttpServletResponse servletResponse) throws IOException
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

  void sendJaxb(Object response, HttpServletResponse servletResponse) throws IOException
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

  void sendToString(Object response, HttpServletResponse servletResponse) throws IOException
  {
    servletResponse.getWriter().write(response.toString());
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
