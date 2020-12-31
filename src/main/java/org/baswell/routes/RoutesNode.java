package org.baswell.routes;

import java.util.List;

public class RoutesNode {
    final RoutesCriteria routesCriteria;

    final List<RouteNode> routeNodes;

    public RoutesNode(RoutesCriteria routesCriteria, List<RouteNode> routeNodes) {
        this.routesCriteria = routesCriteria;
        this.routeNodes = routeNodes;
    }
}
