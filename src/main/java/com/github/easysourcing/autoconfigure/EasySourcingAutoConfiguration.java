package com.github.easysourcing.autoconfigure;

import com.github.easysourcing.Config;
import com.github.easysourcing.EasySourcing;
import com.github.easysourcing.EasySourcingBuilder;
import com.github.easysourcing.GatewayBuilder;
import com.github.easysourcing.autoconfigure.events.EasySourcingStateChangedEvent;
import com.github.easysourcing.autoconfigure.events.EasySourcingUncaughtExceptionEvent;
import com.github.easysourcing.messages.MessageGateway;
import com.github.easysourcing.messages.commands.CommandGateway;
import com.github.easysourcing.messages.events.EventGateway;
import com.github.easysourcing.messages.snapshots.SnapshotGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Slf4j
@Configuration
@EnableConfigurationProperties(EasySourcingProperties.class)
public class EasySourcingAutoConfiguration {

  private final ApplicationEventPublisher eventPublisher;

  @Autowired
  public EasySourcingAutoConfiguration(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  @Bean
  public Config config(EasySourcingProperties easySourcingProperties) {
    return Config.builder()
        .bootstrapServers(easySourcingProperties.getBootstrapServers())
        .applicationId(easySourcingProperties.getApplicationId())
        .replicas(easySourcingProperties.getReplicas())
        .partitions(easySourcingProperties.getPartitions())
        .securityProtocol(easySourcingProperties.getSecurityProtocol())
        .rebuildLocalState(easySourcingProperties.isRebuildLocalState())
        .inMemoryStateStore(easySourcingProperties.isInMemoryStateStore())

        .commandsRetention(easySourcingProperties.getCommandsRetention())
        .resultsRetention(easySourcingProperties.getResultsRetention())
        .snapshotsRetention(easySourcingProperties.getSnapshotsRetention())
        .eventsRetention(easySourcingProperties.getEventsRetention())
        .build();
  }

  @Bean
  public EasySourcingBuilder easySourcingBuilder(Config config) {
    return new EasySourcingBuilder()
        .withConfig(config)
        .withStateListener((newState, oldState) ->
            eventPublisher.publishEvent(EasySourcingStateChangedEvent.builder()
                .newState(newState)
                .oldState(oldState)
                .build()))
        .withUncaughtExceptionHandler((thread, throwable) ->
            eventPublisher.publishEvent(EasySourcingUncaughtExceptionEvent.builder()
                .thread(thread)
                .throwable(throwable)
                .build()));
  }

  @Bean
  public EasySourcingHandlerBeanPostProcessor handlerAnnotationProcessor(EasySourcingBuilder easySourcingBuilder) {
    return new EasySourcingHandlerBeanPostProcessor(easySourcingBuilder);
  }

  @Bean
  public EasySourcing easySourcing(EasySourcingBuilder easySourcingBuilder) {
    return easySourcingBuilder.build();
  }

  @Bean
  public MessageGateway messageGateway(Config config) {
    return new GatewayBuilder()
        .withConfig(config)
        .messageGateway();
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

  @Bean
  public SnapshotGateway snapshotGateway(Config config) {
    return new GatewayBuilder()
        .withConfig(config)
        .snapshotGateway();
  }

  @EventListener
  public void handleApplicationReadyEvent(ApplicationReadyEvent event) {
    EasySourcing app = event.getApplicationContext().getBean(EasySourcing.class);
    app.start();
  }

}
