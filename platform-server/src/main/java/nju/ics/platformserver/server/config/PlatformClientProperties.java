package nju.ics.platformserver.server.config;

import lombok.Data;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
@Data
public class PlatformClientProperties {
    @Value("${platform.docker-uri}")
    private URI dockerUri;

    @Value("${platform.target-servers-host}")
    private String targetServersHost;

    @Value("${platform.origin-port}")
    private int originPort;

    @Value("${platform.bound-port}")
    private int boundPort;
}
