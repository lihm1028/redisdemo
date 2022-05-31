package com.example.redisdemo;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelloControllerIT {


    @Autowired
    private TestRestTemplate testRestTemplate;


    @Test
    public void getHello() {
        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity("/hello?name=World", String.class);
        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Hello World");
    }
}
