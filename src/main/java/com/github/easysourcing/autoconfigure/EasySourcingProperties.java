package com.github.easysourcing.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "easysourcing")
public class EasySourcingProperties {
  private String bootstrapServers;
  private String applicationId;
  private int replicas = 1;
  private int partitions = 1;
  private String stateDir = "/tmp/kafka-streams";
  private String securityProtocol = "PLAINTEXT";

  private boolean rebuildLocalState = false;
  private boolean inMemoryStateStore = false;

  private long commandsRetention = 604800000; // 7 days
  private long resultsRetention = 604800000; // 7 days
  private long snapshotsRetention = 86400000; // 1 day
  private long eventsRetention = -1; // infinite
}
