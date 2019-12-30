package com.github.easysourcing.autoconfigure;

import com.github.easysourcing.Config;
import com.github.easysourcing.EasySourcing;
import com.github.easysourcing.EasySourcingBuilder;
import com.github.easysourcing.GatewayBuilder;
import com.github.easysourcing.message.aggregates.annotations.ApplyEvent;
import com.github.easysourcing.message.commands.CommandGateway;
import com.github.easysourcing.message.commands.annotations.HandleCommand;
import com.github.easysourcing.message.events.EventGateway;
import com.github.easysourcing.message.events.annotations.HandleEvent;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableConfigurationProperties(EasySourcingProperties.class)
public class EasySourcingAutoConfiguration {

  @Autowired
  private ConfigurableApplicationContext applicationContext;


  private String getHostPackageName() {
    Map<String, Object> annotatedBeans = applicationContext.getBeansWithAnnotation(SpringBootApplication.class);
    return annotatedBeans.isEmpty() ? null : annotatedBeans.values().toArray()[0].getClass().getPackage().getName();
  }

  @Bean
  public Reflections reflections() {
    return new Reflections(getHostPackageName(),
        new TypeAnnotationsScanner(),
        new SubTypesScanner(),
        new MethodAnnotationsScanner(),
        new MethodParameterScanner()
    );
  }

  @Bean
  public Config config(EasySourcingProperties easySourcingProperties) {
    return Config.builder()
        .bootstrapServers(easySourcingProperties.getBootstrapServers())
        .applicationId(easySourcingProperties.getApplicationId())
        .replicas(easySourcingProperties.getReplicas())
        .partitions(easySourcingProperties.getPartitions())
        .cleanupOnStop(easySourcingProperties.isCleanUpOnStop())
        .build();
  }

  @Bean
  public EasySourcingBuilder easySourcingBuilder(Reflections reflections, Config config) {
    Set<Class<?>> commandListeners = reflections.getMethodsAnnotatedWith(HandleCommand.class).stream()
        .map(Method::getDeclaringClass)
        .collect(Collectors.toSet());

    Set<Class<?>> aggregateListeners = reflections.getMethodsAnnotatedWith(ApplyEvent.class).stream()
        .map(Method::getDeclaringClass)
        .collect(Collectors.toSet());

    Set<Class<?>> eventListeners = reflections.getMethodsAnnotatedWith(HandleEvent.class).stream()
        .map(Method::getDeclaringClass)
        .collect(Collectors.toSet());

    Set<Class<?>> listenerTypes = new HashSet<>();
    listenerTypes.addAll(commandListeners);
    listenerTypes.addAll(aggregateListeners);
    listenerTypes.addAll(eventListeners);

    EasySourcingBuilder builder = new EasySourcingBuilder(config);
    listenerTypes.forEach(tye ->
        builder.registerHandler(applicationContext.getBean(tye)));

    return builder;
  }

  @Bean
  public EasySourcing easySourcing(EasySourcingBuilder builder) {
    EasySourcing easySourcing = builder.build();
    easySourcing.start();

    return easySourcing;
  }

  @Bean
  public GatewayBuilder gatewayBuilder(Config config) {
    return new GatewayBuilder(config);
  }

  @Bean
  public CommandGateway commandGateway(GatewayBuilder gatewayBuilder) {
    return gatewayBuilder.commandGateway();
  }

  @Bean
  public EventGateway eventGateway(GatewayBuilder gatewayBuilder) {
    return gatewayBuilder.eventGateway();
  }

}
