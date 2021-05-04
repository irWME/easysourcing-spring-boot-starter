package com.github.easysourcing.autoconfigure;

import com.github.easysourcing.EasySourcing;
import com.github.easysourcing.EasySourcingBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Slf4j
@Configuration
@ConditionalOnBean(EasySourcingBuilder.class)
@EnableConfigurationProperties(EasySourcingProperties.class)
public class EasySourcingAutoConfiguration implements ApplicationListener<ApplicationReadyEvent>, ApplicationContextAware {

  private ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    if (event.getApplicationContext().equals(this.applicationContext)) {
      EasySourcingBuilder builder = event.getApplicationContext().getBean(EasySourcingBuilder.class);
      EasySourcing app = builder.build();
      app.start();
    }
  }

  @Bean
  public EasySourcingBeanPostProcessor easySourcingBeanPostProcessor(EasySourcingBuilder builder) {
    return new EasySourcingBeanPostProcessor(builder);
  }

}
