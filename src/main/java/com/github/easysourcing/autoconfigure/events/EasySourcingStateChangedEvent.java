package com.github.easysourcing.autoconfigure.events;

import lombok.Builder;
import lombok.Value;
import org.apache.kafka.streams.KafkaStreams;

@Value
@Builder
public class EasySourcingStateChangedEvent {
  private KafkaStreams.State newState;
  private KafkaStreams.State oldState;
}
