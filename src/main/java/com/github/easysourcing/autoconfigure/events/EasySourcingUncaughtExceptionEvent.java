package com.github.easysourcing.autoconfigure.events;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EasySourcingUncaughtExceptionEvent {
  private Thread thread;
  private Throwable throwable;
}
