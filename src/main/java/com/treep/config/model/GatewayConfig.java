package com.treep.config.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GatewayConfig {

    private int serverPort;

    private String configLocation;

}
