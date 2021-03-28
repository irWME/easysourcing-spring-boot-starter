package com.github.easysourcing.autoconfigure;

import com.github.easysourcing.EasySourcing;
import com.github.easysourcing.EasySourcingBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Slf4j
@Configuration
@ConditionalOnBean(EasySourcingBuilder.class)
@EnableConfigurationProperties(EasySourcingProperties.class)
public class EasySourcingAutoConfiguration {

  @Bean
  public EasySourcingBeanPostProcessor easySourcingBeanPostProcessor(EasySourcingBuilder builder) {
    return new EasySourcingBeanPostProcessor(builder);
  }

  @EventListener
  public void handleApplicationReadyEvent(ApplicationReadyEvent event) {
    EasySourcingBuilder builder = event.getApplicationContext().getBean(EasySourcingBuilder.class);
    EasySourcing app = builder.build();
    app.start();
  }

}
