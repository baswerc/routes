package org.baswell.routes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The format type of an HTTP request. Taken from the either the Accept-Type header of the request (if present) or the file
 * extension (if present) of the URL.
 */
public class RequestFormat
{
  public enum Type
  {
    HTML("HTML", "html", "htm"),
    XML("XML", "xml"),
    JSON("JSON", "json"),
    JAVASCRIPT("Javascript", "js"),
    CSV("CSV", "csv"),
    PDF("PDF", "pdf"),
    RSS("RSS"),
    ATOM("ATOM"),
    ICS("ICS", "ics"),
    TEXT("Text", "text", "txt"),
    EXCEL("Excel", "xlsx", "xls"),
    WORD("Word", "doc"),
    OTHER("Other");

    public final String name;

    public final List<String> fileExtensions;

    private Type(String name, String... fileExtensions)
    {
      this.name = name;

      List<String> fileExtensionsList = new ArrayList<String>();
      if (fileExtensions != null)
      {
        for (String fileExtension : fileExtensions)
        {
          fileExtensionsList.add(fileExtension);
        }
      }
      this.fileExtensions = Collections.unmodifiableList(fileExtensionsList);
    }
  }

  public final String acceptType;

  public final String fileExtension;
  
  public final Type type;

  public RequestFormat(String acceptType)
  {
    this(acceptType, null);
  }

  public RequestFormat(String acceptType, RequestPath requestPath)
  {
    this.acceptType = acceptType;
    fileExtension = requestPath == null ? null : requestPath.getFileExtension();

    if (acceptType == null)
    {
      if (fileExtension != null)
      {
        for (Type formatType : Type.values())
        {
          if (formatType.fileExtensions.contains(fileExtension))
          {
            type = formatType;
            return;
          }
        }
      }

      type = Type.OTHER;
    }
    else
    {
      acceptType = acceptType.toLowerCase();
      if (acceptType.contains("json"))
      {
        type = Type.JSON;
      }
      else if (acceptType.contains("javascript"))
      {
        type = Type.JAVASCRIPT;
      }
      else if (acceptType.contains("csv"))
      {
        type = Type.CSV;
      }
      else if (acceptType.contains("pdf"))
      {
        type = Type.PDF;
      }
      else if (acceptType.contains("rss"))
      {
        type = Type.RSS;
      }
      else if (acceptType.contains("atom"))
      {
        type = Type.ATOM;
      }
      else if (acceptType.contains("xml"))
      {
        type = Type.XML;
      }
      else if (acceptType.equals("text/calendar"))
      {
        type = Type.ICS;
      }
      else if (acceptType.contains("excel") || acceptType.contains("xls"))
      {
        type = Type.EXCEL;
      }
      else if (acceptType.contains("doc"))
      {
        type = Type.WORD;
      }
      else
      {
        if (fileExtension != null)
        {
          for (Type formatType : Type.values())
          {
            if (formatType.fileExtensions.contains(fileExtension))
            {
              type = formatType;
              return;
            }
          }
        }
        if (acceptType.contains("html"))
        {
          type = Type.HTML;
        }
        else if (acceptType.contains("text"))
        {
          type = Type.TEXT;
        }
        else
        {
          type = Type.OTHER;
        }
      }
    }
  }

  @Override
  public String toString()
  {
    return type.name;
  }
}
