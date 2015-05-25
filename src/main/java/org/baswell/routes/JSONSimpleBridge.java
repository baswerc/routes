package org.baswell.routes;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.lang.reflect.Type;

class JSONSimpleBridge
{
  static <RequestContentType extends Object> RequestContentType parseJson(byte[] contentBytes) throws IOException
  {
    try
    {
      return (RequestContentType) new JSONParser().parse(new String(contentBytes));
    }
    catch (ParseException e)
    {
      throw new IOException(e);
    }
  }
}
