package nju.ics.platformclientspringbootstarter.autoconfiguration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "platform")
public class PlatformClientProperties {
    private String serverUrl;

    private String clientType;

    private String clientName;

    private Integer clientPort;
}
