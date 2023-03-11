package com.treep.config.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GatewayConfig {

    private String serverPort;

    private String configLocation;

    private String serverThreadCount;

}
