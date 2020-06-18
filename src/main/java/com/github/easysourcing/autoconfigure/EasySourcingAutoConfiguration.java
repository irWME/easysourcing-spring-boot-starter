package com.github.easysourcing.autoconfigure;

import com.github.easysourcing.Config;
import com.github.easysourcing.EasySourcing;
import com.github.easysourcing.EasySourcingBuilder;
import com.github.easysourcing.GatewayBuilder;
import com.github.easysourcing.autoconfigure.annotations.EnableEasySourcing;
import com.github.easysourcing.messages.commands.CommandGateway;
import com.github.easysourcing.messages.events.EventGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Slf4j
@Configuration
@EnableConfigurationProperties(EasySourcingProperties.class)
public class EasySourcingAutoConfiguration {

  @Bean
  public Config config(EasySourcingProperties easySourcingProperties) {
    return Config.builder()
        .bootstrapServers(easySourcingProperties.getBootstrapServers())
        .applicationId(easySourcingProperties.getApplicationId())
        .replicas(easySourcingProperties.getReplicas())
        .partitions(easySourcingProperties.getPartitions())
        .securityProtocol(easySourcingProperties.getSecurityProtocol())
        .frequentCommits(easySourcingProperties.isFrequentCommits())
        .deleteLocalStateOnStartup(easySourcingProperties.isDeleteLocalStateOnStartup())

        .commandsRetention(easySourcingProperties.getCommandsRetention())
        .resultsRetention(easySourcingProperties.getResultsRetention())
        .snapshotsRetention(easySourcingProperties.getSnapshotsRetention())
        .eventsRetention(easySourcingProperties.getEventsRetention())
        .build();
  }

  @Bean
  public EasySourcingBuilder easySourcingBuilder(Config config) {
    return new EasySourcingBuilder()
        .withConfig(config);
  }

  @Bean
  public EasySourcingHandlerBeanPostProcessor handlerAnnotationProcessor(EasySourcingBuilder easySourcingBuilder) {
    return new EasySourcingHandlerBeanPostProcessor(easySourcingBuilder);
  }

  @Bean
  public EasySourcing easySourcing(EasySourcingBuilder easySourcingBuilder, ApplicationContext applicationContext) {
    EasySourcing app = null;

    Map<String, Object> beans = applicationContext.getBeansWithAnnotation(EnableEasySourcing.class);
    if (!beans.isEmpty()) {
      app = easySourcingBuilder.build();
      app.start();
    }

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
