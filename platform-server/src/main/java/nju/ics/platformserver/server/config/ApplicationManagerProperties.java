package nju.ics.platformserver.server.config;

import lombok.Data;

import java.net.URI;
import java.net.URISyntaxException;

@Data
public class ApplicationManagerProperties {
    private URI dockerUri = new URI("tcp://localhost:2375");

    public ApplicationManagerProperties() throws URISyntaxException {
    }

    public URI dockerUri() {
        return dockerUri;
    }
}
