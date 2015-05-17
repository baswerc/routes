package org.baswell.routes;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;

public class JDOMBridge
{
  static <RequestContentType extends Object> RequestContentType parseRequestContent(byte[] contentBytes, ContentConversionType contentConversionType, Class<RequestContentType> contentClass) throws IOException
  {
    try
    {
      Document document = new SAXBuilder().build(new StringReader(new String(contentBytes)));
      return (RequestContentType) ((contentConversionType == ContentConversionType.JDOM2_DOCUMENT) ? document : document.getRootElement());
    }
    catch (JDOMException e)
    {
      throw new RoutesException("Unable to create RequestContent class: " + contentClass.getName(), e);
    }
  }

 static void sendJdom2Document(Object response, HttpServletResponse servletResponse) throws IOException
  {
    new XMLOutputter().output((Document)response, servletResponse.getWriter());
  }

  static void sendJdom2Element(Object response, HttpServletResponse servletResponse) throws IOException
  {
    new XMLOutputter().output((Element)response, servletResponse.getWriter());
  }

}
