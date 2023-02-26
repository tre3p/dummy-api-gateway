package com.treep.config.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * POJO for storing collection of {@link RouteDefinition}
 */
@Getter
@Setter
public class Routes {

    private List<RouteDefinition> routes;
}
