package org.baswell.routes;

import org.baswell.routes.ParsedExactParameterTerminal;
import org.baswell.routes.ParsedExactPathTerminal;
import org.baswell.routes.ParsedRouteTree;
import org.baswell.routes.Parser;
import org.junit.Test;

import static org.junit.Assert.*;

public class ParserTest
{
  @Test
  public void testSimplePath()
  {
    Parser parser = new Parser();
    
    ParsedRouteTree tree = parser.parse("/test/this/out");
    
    assertEquals(tree.pathTerminals.size(), 3);
    assertEquals(tree.parameterTerminals.size(), 0);
    
    assertTrue(tree.pathTerminals.get(0) instanceof ParsedExactPathTerminal);
    ParsedExactPathTerminal exactPathTerminal = (ParsedExactPathTerminal)tree.pathTerminals.get(0);
    assertEquals(exactPathTerminal.routeIndex, 0);
    assertEquals(exactPathTerminal.pathIndex, 0);
    assertEquals(exactPathTerminal.segment, "test");
    
    assertTrue(tree.pathTerminals.get(1) instanceof ParsedExactPathTerminal);
    exactPathTerminal = (ParsedExactPathTerminal)tree.pathTerminals.get(1);
    assertEquals(exactPathTerminal.routeIndex, 1);
    assertEquals(exactPathTerminal.pathIndex, 1);
    assertEquals(exactPathTerminal.segment, "this");
    
    assertTrue(tree.pathTerminals.get(2) instanceof ParsedExactPathTerminal);
    exactPathTerminal = (ParsedExactPathTerminal)tree.pathTerminals.get(2);
    assertEquals(exactPathTerminal.routeIndex, 2);
    assertEquals(exactPathTerminal.pathIndex, 2);
    assertEquals(exactPathTerminal.segment, "out");
  }

  @Test
  public void testSimplePathParameters()
  {
    Parser parser = new Parser();
    
    ParsedRouteTree tree = parser.parse("/test/this/out?one=two&three=four");
    
    assertEquals(tree.pathTerminals.size(), 3);
    
    assertTrue(tree.pathTerminals.get(0) instanceof ParsedExactPathTerminal);
    ParsedExactPathTerminal exactPathTerminal = (ParsedExactPathTerminal)tree.pathTerminals.get(0);
    assertEquals(exactPathTerminal.routeIndex, 0);
    assertEquals(exactPathTerminal.pathIndex, 0);
    assertEquals(exactPathTerminal.segment, "test");
    
    assertTrue(tree.pathTerminals.get(1) instanceof ParsedExactPathTerminal);
    exactPathTerminal = (ParsedExactPathTerminal)tree.pathTerminals.get(1);
    assertEquals(exactPathTerminal.routeIndex, 1);
    assertEquals(exactPathTerminal.pathIndex, 1);
    assertEquals(exactPathTerminal.segment, "this");
    
    assertTrue(tree.pathTerminals.get(2) instanceof ParsedExactPathTerminal);
    exactPathTerminal = (ParsedExactPathTerminal)tree.pathTerminals.get(2);
    assertEquals(exactPathTerminal.routeIndex, 2);
    assertEquals(exactPathTerminal.pathIndex, 2);
    assertEquals(exactPathTerminal.segment, "out");
    
    assertEquals(tree.parameterTerminals.size(), 2);
    
    assertTrue(tree.parameterTerminals.get(0) instanceof ParsedExactParameterTerminal);
    ParsedExactParameterTerminal exactParameterTermainl = (ParsedExactParameterTerminal)tree.parameterTerminals.get(0);
    assertEquals(exactParameterTermainl.routeIndex, 3);
    assertEquals(exactParameterTermainl.name, "one");
    assertEquals(exactParameterTermainl.value, "two");

    assertTrue(tree.parameterTerminals.get(1) instanceof ParsedExactParameterTerminal);
    exactParameterTermainl = (ParsedExactParameterTerminal)tree.parameterTerminals.get(1);
    assertEquals(exactParameterTermainl.routeIndex, 4);
    assertEquals(exactParameterTermainl.name, "three");
    assertEquals(exactParameterTermainl.value, "four");

  }

}
