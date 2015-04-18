package org.baswell.routes;

import java.util.List;

import static java.util.Arrays.*;

public enum MediaType
{
  ATOM(asList(MIMETypes.ATOM), asList("atomcat")),
  CSV(asList(MIMETypes.CSV), asList("csv")),
  EXCEL(asList(MIMETypes.EXCEL, MIMETypes.EXCEL2, MIMETypes.EXCEL3, MIMETypes.EXCEL4, MIMETypes.EXCEL5, MIMETypes.EXCEL6, MIMETypes.EXCEL7), asList("xlsx", "xls")),
  HTML(asList(MIMETypes.HTML, MIMETypes.HTML2), asList("html", "htm", "htmls", "html")),
  ICS(asList(MIMETypes.ICS), asList("ics")),
  JAVASCRIPT(asList(MIMETypes.JAVASCRIPT, MIMETypes.JAVASCRIPT2, MIMETypes.JAVASCRIPT3), asList("js")),
  JSON(asList(MIMETypes.JSON, MIMETypes.JSON2), asList("json")),
  PDF(asList(MIMETypes.PDF), asList("pdf")),
  RSS(asList(MIMETypes.RSS), asList("rss")),
  TEXT(asList(MIMETypes.TEXT), asList("text", "txt")),
  WORD(asList(MIMETypes.WORD, MIMETypes.WORD2), asList("doc", "docx")),
  XML(asList(MIMETypes.XML, MIMETypes.XML2), asList("xml"));

  public final String mimeType;

  public final List<String> mimeTypes;

  public final List<String> extensions;

  public static MediaType findFromMimeType(String mimeType)
  {
    if (mimeType != null)
    {
      mimeType = mimeType.toLowerCase();
      for (MediaType mediaType : values())
      {
        for (String mediaTypeMimeType : mediaType.mimeTypes)
        {
          if (mediaTypeMimeType.equals(mimeType))
          {
            return mediaType;
          }
        }
      }
    }

    return null;
  }

  public static MediaType findFromExtension(String extension)
  {
    if (extension != null)
    {
      extension = extension.toLowerCase();
      for (MediaType mediaType : values())
      {
        for (String mediaTypeExtension : mediaType.extensions)
        {
          if (mediaTypeExtension.equals(extension))
          {
            return mediaType;
          }
        }
      }
    }

    return null;
  }


  private MediaType(List<String> mimeTypes, List<String> extensions)
  {
    this.mimeTypes = mimeTypes;
    this.extensions = extensions;

    mimeType = mimeTypes.get(0);
  }
}
