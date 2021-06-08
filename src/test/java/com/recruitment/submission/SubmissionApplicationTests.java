package com.recruitment.submission;

import com.recruitment.submission.initializer.Postgres;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(initializers = {Postgres.Initializer.class})
class SubmissionApplicationTests {

	@Test
	void contextLoads() {
	}


}
