package com.example.demo;

import com.example.demo.dto.CombinedAnswerResponse;
import com.example.demo.service.CombinedAnswerService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {


  CombinedAnswerService service;

  public DemoController(CombinedAnswerService service) {
    this.service = service;
  }

  private final int SLEEP_TIME = 1000*3;

  @GetMapping(value = "/random-string-slow")
  public String slowEndpoint() throws InterruptedException {
    Thread.sleep(SLEEP_TIME);
    return RandomStringUtils.randomAlphanumeric(10);
  }

  @GetMapping(value = "/combined-answer/{name}")
  public CombinedAnswerResponse combinedAnswer(@PathVariable String name) throws InterruptedException {

    return service.getCombinedAnswer(name);
  }

}
