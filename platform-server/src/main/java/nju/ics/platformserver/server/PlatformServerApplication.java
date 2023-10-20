package nju.ics.platformserver.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PlatformServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlatformServerApplication.class, args);
    }
}
