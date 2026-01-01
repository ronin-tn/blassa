package com.blassa.config;

import io.sentry.Sentry;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Manual Sentry configuration for Spring Boot 4.0.0 compatibility.
 * The Sentry Spring Boot starter is not yet compatible with Spring Boot 4.0.0.
 */
@Configuration
public class SentryConfig {

    @Value("${sentry.dsn}")
    private String dsn;

    @Value("${sentry.send-default-pii:false}")
    private boolean sendDefaultPii;

    @Value("${sentry.traces-sample-rate:1.0}")
    private double tracesSampleRate;

    @PostConstruct
    public void init() {
        Sentry.init(options -> {
            options.setDsn(dsn);
            options.setSendDefaultPii(sendDefaultPii);
            options.setTracesSampleRate(tracesSampleRate);
            options.setDebug(false);
        });
    }
}
