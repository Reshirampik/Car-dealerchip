package ru.lab1.dealership.observability;

import java.util.UUID;
import org.slf4j.MDC;

public final class TraceContext {
  public static final String TRACE_ID_HEADER = "X-Trace-Id";
  public static final String TRACE_ID_KEY = "traceId";

  private TraceContext() {}

  public static String currentOrNew() {
    return normalize(MDC.get(TRACE_ID_KEY));
  }

  public static String normalize(String traceId) {
    return traceId == null || traceId.isBlank() ? UUID.randomUUID().toString() : traceId;
  }
}
