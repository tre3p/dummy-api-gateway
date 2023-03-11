package com.treep.config.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GatewayConfig {

    private String serverPort;

    private String configLocation;

    private String serverThreadCount;

}
