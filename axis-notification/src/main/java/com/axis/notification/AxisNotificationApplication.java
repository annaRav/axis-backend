package com.axis.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = {"com.axis.notification", "com.axis.common"})
@EnableJpaAuditing
public class AxisNotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AxisNotificationApplication.class, args);
    }

}
