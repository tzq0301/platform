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
                                                 @Nonnull PlatformClientProperties platformClientProperties) {
        return new ApplicationManager(proxyServerManager, dockerManager, platformClientProperties);
    }

    @Bean
    public ProxyServerManager proxyServerManage(@Nonnull PlatformClientProperties properties) {
        return new ProxyServerManager(properties.getTargetServersHost(), new GetLatestProxyStrategy());
    }

    @Bean
    public DockerManager dockerManager(PlatformClientProperties platformClientProperties) {
        return new DockerManager(platformClientProperties.getDockerUri());
    }
}
