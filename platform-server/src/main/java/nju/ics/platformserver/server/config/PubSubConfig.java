package nju.ics.platformserver.server.config;

import nju.ics.platformserver.pubsub.AlivenessCheckerPlugin;
import nju.ics.platformserver.pubsub.AllTopicSolverPlugin;
import nju.ics.platformserver.pubsub.PubSub;
import nju.ics.platformserver.pubsub.PubSubPlugin;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.util.List;

@SpringBootConfiguration
public class PubSubConfig {
    @Bean
    public AlivenessCheckerPlugin alivenessCheckerPlugin() {
        Duration maxHeartBeatInterval = Duration.ofSeconds(20);
        return new AlivenessCheckerPlugin(maxHeartBeatInterval);
    }

    @Bean
    public AllTopicSolverPlugin allTopicSolverPlugin() {
        return new AllTopicSolverPlugin();
    }

    @Bean
    public PubSub pubsub(List<PubSubPlugin> plugins) {
        PubSub pubsub = new PubSub();
        plugins.forEach(pubsub::registerPlugin);
        return pubsub;
    }
}
