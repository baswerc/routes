package org.baswell.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Format
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

  public final String mimeType;

  public final String fileExtension;
  
  public final Type type;

  public Format(String mimeType)
  {
    this(mimeType, null);
  }

  public Format(String mimeType, RequestPath requestPath)
  {
    this.mimeType = mimeType;
    fileExtension = requestPath == null ? null : requestPath.getFileExtension();

    if (mimeType == null)
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
      mimeType = mimeType.toLowerCase();
      if (mimeType.contains("json"))
      {
        type = Type.JSON;
      }
      else if (mimeType.contains("javascript"))
      {
        type = Type.JAVASCRIPT;
      }
      else if (mimeType.contains("csv"))
      {
        type = Type.CSV;
      }
      else if (mimeType.contains("pdf"))
      {
        type = Type.PDF;
      }
      else if (mimeType.contains("rss"))
      {
        type = Type.RSS;
      }
      else if (mimeType.contains("atom"))
      {
        type = Type.ATOM;
      }
      else if (mimeType.contains("xml"))
      {
        type = Type.XML;
      }
      else if (mimeType.equals("text/calendar"))
      {
        type = Type.ICS;
      }
      else if (mimeType.contains("excel") || mimeType.contains("xls"))
      {
        type = Type.EXCEL;
      }
      else if (mimeType.contains("doc"))
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
        if (mimeType.contains("html"))
        {
          type = Type.HTML;
        }
        else if (mimeType.contains("text"))
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

}
