package org.baswell.routes;

import org.junit.Test;

import static org.baswell.routes.MediaType.*;
import static junit.framework.Assert.*;

public class MediaTypeTest
{
  @Test
  public void testGuessMediaType()
  {
    assertEquals(MediaType.JSON, guessFromContent("{one: '1', two: 'three'}"));
    assertEquals(MediaType.JSON, guessFromContent("[1, 2, 3, 4]"));
    assertEquals(MediaType.XML, guessFromContent("<one><two>three</two></one>"));
    assertNull(guessFromContent("HELLO WORLD!"));
  }
}

