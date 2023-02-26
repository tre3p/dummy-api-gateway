package com.treep.config.model;

import lombok.*;

import java.util.List;

/**
 * POJO for storing collection of {@link RouteDefinition}
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Routes {

    private List<RouteDefinition> routes;
}
