package org.baswell.routes;

public class Format
{
  public enum Type
  {
    HTML("HTML"),
    XML("XML"),
    JSON("JSON"),
    JAVASCRIPT("Javascript"),
    CSV("CSV"),
    PDF("PDF"),
    RSS("RSS"),
    ATOM("ATOM"),
    ICS("ICS"),
    TEXT("Text"),
    OTHER("Other");

    public final String name;

    private Type(String name)
    {
      this.name = name;
    }
  }

  public final String mimeType;
  
  public final Type type;
  
  public Format(String mimeType)
  {
    this.mimeType = mimeType;
    
    if (mimeType == null)
    {
      type = Type.OTHER;
    }
    else
    {
      mimeType = mimeType.toLowerCase();
      if (mimeType.contains("html"))
      {
        type = Type.HTML;
      }
      else if (mimeType.contains("json"))
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
