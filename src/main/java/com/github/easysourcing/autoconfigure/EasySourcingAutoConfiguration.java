package com.github.easysourcing.autoconfigure;

import com.github.easysourcing.Config;
import com.github.easysourcing.EasySourcing;
import com.github.easysourcing.EasySourcingBuilder;
import com.github.easysourcing.GatewayBuilder;
import com.github.easysourcing.messages.annotations.Handler;
import com.github.easysourcing.messages.commands.CommandGateway;
import com.github.easysourcing.messages.events.EventGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Slf4j
@Configuration
@EnableConfigurationProperties(EasySourcingProperties.class)
public class EasySourcingAutoConfiguration {

  @Autowired
  private ApplicationContext applicationContext;

  @Bean
  public Config config(EasySourcingProperties easySourcingProperties) {
    return Config.builder()
        .bootstrapServers(easySourcingProperties.getBootstrapServers())
        .securityProtocol(easySourcingProperties.getSecurityProtocol())
        .applicationId(easySourcingProperties.getApplicationId())
        .replicas(easySourcingProperties.getReplicas())
        .partitions(easySourcingProperties.getPartitions())
        .build();
  }

  @Bean
  public EasySourcing easySourcing(Config config) {
    Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Handler.class);

    EasySourcingBuilder builder = new EasySourcingBuilder()
        .withConfig(config);

    beans.values()
        .forEach(builder::registerHandler);

    EasySourcing app = builder.build();
    app.start();

    return app;
  }

  @Bean
  public CommandGateway commandGateway(Config config) {
    return new GatewayBuilder()
        .withConfig(config)
        .commandGateway();
  }

  @Bean
  public EventGateway eventGateway(Config config) {
    return new GatewayBuilder()
        .withConfig(config)
        .eventGateway();
  }

}
