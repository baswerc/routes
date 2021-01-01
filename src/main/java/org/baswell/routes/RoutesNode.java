package org.baswell.routes;

import java.util.List;

public class RoutesNode {
    final RoutesCriteria criteria;

    final RoutesData data;

    final List<RouteNode> nodes;

    public RoutesNode(RoutesCriteria criteria, RoutesData data, List<RouteNode> nodes) {
        this.criteria = criteria;
        this.data = data;
        this.nodes = nodes;
    }
}
