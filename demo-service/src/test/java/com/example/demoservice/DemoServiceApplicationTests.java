package com.example.demoservice;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class DemoServiceApplicationTests {

	@Test
	public void getQuestionTest() {
		RestTemplate restTemplate = new RestTemplate();
		String text = restTemplate.getForObject("http://localhost:8080/ask?question={question}", String.class, "Test request");
		String message = "you asked \"Test request\", but our programmers still working on it";
		Assert.isTrue(message.equals(text), "Text should be equal");
	}

}
