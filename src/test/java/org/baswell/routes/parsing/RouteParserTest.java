package org.baswell.routes.parsing;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

public class RouteParserTest
{
  @Test
  public void testSimplePath()
  {
    RouteParser parser = new RouteParser();
    
    RouteTree tree = parser.parse("/test/this/out");
    
    assertEquals(tree.pathTerminals.size(), 3);
    assertEquals(tree.parameterTerminals.size(), 0);
    
    assertTrue(tree.pathTerminals.get(0) instanceof ExactPathTerminal);
    ExactPathTerminal exactPathTerminal = (ExactPathTerminal)tree.pathTerminals.get(0);
    assertEquals(exactPathTerminal.routeIndex, 0);
    assertEquals(exactPathTerminal.pathIndex, 0);
    assertEquals(exactPathTerminal.segment, "test");
    
    assertTrue(tree.pathTerminals.get(1) instanceof ExactPathTerminal);
    exactPathTerminal = (ExactPathTerminal)tree.pathTerminals.get(1);
    assertEquals(exactPathTerminal.routeIndex, 1);
    assertEquals(exactPathTerminal.pathIndex, 1);
    assertEquals(exactPathTerminal.segment, "this");
    
    assertTrue(tree.pathTerminals.get(2) instanceof ExactPathTerminal);
    exactPathTerminal = (ExactPathTerminal)tree.pathTerminals.get(2);
    assertEquals(exactPathTerminal.routeIndex, 2);
    assertEquals(exactPathTerminal.pathIndex, 2);
    assertEquals(exactPathTerminal.segment, "out");
  }

  @Test
  public void testSimplePathParameters()
  {
    RouteParser parser = new RouteParser();
    
    RouteTree tree = parser.parse("/test/this/out?one=two&three=four");
    
    assertEquals(tree.pathTerminals.size(), 3);
    
    assertTrue(tree.pathTerminals.get(0) instanceof ExactPathTerminal);
    ExactPathTerminal exactPathTerminal = (ExactPathTerminal)tree.pathTerminals.get(0);
    assertEquals(exactPathTerminal.routeIndex, 0);
    assertEquals(exactPathTerminal.pathIndex, 0);
    assertEquals(exactPathTerminal.segment, "test");
    
    assertTrue(tree.pathTerminals.get(1) instanceof ExactPathTerminal);
    exactPathTerminal = (ExactPathTerminal)tree.pathTerminals.get(1);
    assertEquals(exactPathTerminal.routeIndex, 1);
    assertEquals(exactPathTerminal.pathIndex, 1);
    assertEquals(exactPathTerminal.segment, "this");
    
    assertTrue(tree.pathTerminals.get(2) instanceof ExactPathTerminal);
    exactPathTerminal = (ExactPathTerminal)tree.pathTerminals.get(2);
    assertEquals(exactPathTerminal.routeIndex, 2);
    assertEquals(exactPathTerminal.pathIndex, 2);
    assertEquals(exactPathTerminal.segment, "out");
    
    assertEquals(tree.parameterTerminals.size(), 2);
    
    assertTrue(tree.parameterTerminals.get(0) instanceof ExactParameterTerminal);
    ExactParameterTerminal exactParameterTermainl = (ExactParameterTerminal)tree.parameterTerminals.get(0);
    assertEquals(exactParameterTermainl.routeIndex, 3);
    assertEquals(exactParameterTermainl.name, "one");
    assertEquals(exactParameterTermainl.value, "two");

    assertTrue(tree.parameterTerminals.get(1) instanceof ExactParameterTerminal);
    exactParameterTermainl = (ExactParameterTerminal)tree.parameterTerminals.get(1);
    assertEquals(exactParameterTermainl.routeIndex, 4);
    assertEquals(exactParameterTermainl.name, "three");
    assertEquals(exactParameterTermainl.value, "four");

  }

}
