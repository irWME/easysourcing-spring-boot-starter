package com.github.easysourcing.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "easysourcing")
public class EasySourcingProperties {
//  private String bootstrapServers;
//  private String applicationId;
}
