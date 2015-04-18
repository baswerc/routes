package org.baswell.routes;

import java.util.List;

import static java.util.Arrays.*;

public enum MediaType
{
  HTML(asList("text/html", "application/xhtml+xml"), asList("html", "htm", "htmls", "html")),
  XML(asList("text/xml", "application/xml"), asList("xml")),
  JSON(asList("application/json"), asList("json")),
  JAVASCRIPT(asList("application/javascript", "text/javascript"), asList("js")),
  CSV(asList("text/csv"), asList("csv")),
  PDF(asList("application/pdf"), asList("pdf")),
  RSS(asList("application/rss+xml"), asList("rss")),
  ATOM(asList("application/atomcat+xml"), asList("atomcat")),
  ICS(asList("text/calendar"), asList("ics")),
  TEXT(asList("text/plain"), asList("text", "txt")),
  EXCEL(asList("application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), asList("xlsx", "xls")),
  WORD(asList("application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"), asList("doc", "docx"));

  public final List<String> mimeTypes;

  public final List<String> extensions;

  public static MediaType match(String mimeType, String extension)
  {
    if (mimeType != null)
    {
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

    if (mimeType != null)
    {
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
  }

}
