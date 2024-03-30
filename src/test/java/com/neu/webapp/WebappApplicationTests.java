package com.neu.webapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
@SpringBootTest(properties = {
		"GOOGLE_CLOUD_PROJECT=dev-project-415121",
		"PUBSUB_TOPIC=verify_email"
})
class WebappApplicationTests {

	@Test
	void contextLoads() {
	}

}
