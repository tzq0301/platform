package nju.ics.platformserver.server.config;

import jakarta.annotation.Nonnull;
import nju.ics.platformserver.application.ApplicationManager;
import nju.ics.platformserver.docker.DockerManager;
import nju.ics.platformserver.proxy.ProxyServerManager;
import nju.ics.platformserver.proxy.strategy.GetLatestProxyStrategy;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import java.net.URISyntaxException;

@SpringBootConfiguration
public class ApplicationManagerConfig {
    @Bean
    public ApplicationManager applicationManager(@Nonnull ProxyServerManager proxyServerManager,
                                                 @Nonnull DockerManager dockerManager,
                                                 @Nonnull ApplicationManagerProperties applicationManagerProperties) {
        return new ApplicationManager(proxyServerManager, dockerManager, applicationManagerProperties);
    }

    @Bean
    public ProxyServerManager proxyServerManage() {
        return new ProxyServerManager("127.0.0.1", new GetLatestProxyStrategy());
    }

    @Bean
    public ApplicationManagerProperties applicationManagerProperties() throws URISyntaxException {
        return new ApplicationManagerProperties();
    }

    @Bean
    public DockerManager dockerManager(ApplicationManagerProperties applicationManagerProperties) {
        return new DockerManager(applicationManagerProperties.dockerUri());
    }
}