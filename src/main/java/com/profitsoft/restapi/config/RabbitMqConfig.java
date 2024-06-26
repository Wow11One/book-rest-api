package com.profitsoft.restapi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitMqConfig {

    @Value("${rabbitmq.mailQueue.name}")
    String mailQueueName;

}
