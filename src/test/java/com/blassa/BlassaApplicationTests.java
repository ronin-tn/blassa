package com.blassa;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import io.sentry.Sentry;

@SpringBootTest
class BlassaApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void testSentryIntegration() {
		try {
			throw new Exception("This is a Sentry test exception.");
		} catch (Exception e) {
			Sentry.captureException(e);
		}
	}
}
