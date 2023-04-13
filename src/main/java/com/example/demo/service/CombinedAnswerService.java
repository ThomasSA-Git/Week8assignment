package com.example.demo.service;

import com.example.demo.dto.Age;
import com.example.demo.dto.CombinedAnswerResponse;
import com.example.demo.dto.Gender;
import com.example.demo.dto.Nation;
import com.example.demo.entity.CombinedAnswer;
import com.example.demo.repository.CombinedAnswerRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

@Service
public class CombinedAnswerService {

  CombinedAnswerRepository repository;

  public CombinedAnswerService(CombinedAnswerRepository repository) {
    this.repository = repository;
  }


  Mono<Gender> getGenderForName(String name) {
    WebClient client = WebClient.create();
    Mono<Gender> gender = client.get()
        .uri("https://api.genderize.io?name="+name)
        .retrieve()
        .bodyToMono(Gender.class);
    return gender;
  }

  Mono<Age> getAgeForName(String name) {
    WebClient client = WebClient.create();
    Mono<Age> age = client.get()
        .uri("https://api.agify.io?name="+name)
        .retrieve()
        .bodyToMono(Age.class);
    return age;
  }

  Mono<Nation> getNationForName(String name) {
    WebClient client = WebClient.create();
    Mono<Nation> nation = client.get()
        .uri("https://api.nationalize.io?name="+name)
        .retrieve()
        .bodyToMono(JsonNode.class)
        .map(response -> {
          Nation nationObj = new Nation();
          nationObj.setName(response.get("name").asText());
          JsonNode firstCountry = response.get("country").get(0);
          nationObj.setCountry(firstCountry.get("country_id").asText());
          nationObj.setCountryProbability(firstCountry.get("probability").asDouble());
          return nationObj;
        });
    return nation;
  }


  public CombinedAnswerResponse getCombinedAnswer(String name){

    //needs check for caching of name
    if (!repository.existsByName(name)) {
      Mono<Age> age = getAgeForName(name);
      Mono<Gender> gender = getGenderForName(name);
      Mono<Nation> nation = getNationForName(name);


      Mono<Tuple3<Age, Gender, Nation>> combined = Mono.zip(age, gender, nation);

      CombinedAnswer answer = combined.map(tuple -> {
        Age ageData = tuple.getT1();
        Gender genderData = tuple.getT2();
        Nation nationData = tuple.getT3();

        CombinedAnswer combinedAnswer = new CombinedAnswer();

        combinedAnswer.setName(name);
        combinedAnswer.setGender(genderData.getGender());
        combinedAnswer.setGenderProbability(genderData.getProbability());
        combinedAnswer.setAge(ageData.getAge());
        combinedAnswer.setAgeCount(ageData.getCount());
        combinedAnswer.setCountry(nationData.getCountry());
        combinedAnswer.setCountryProbability(nationData.getCountryProbability());
        return combinedAnswer;
      }).block();


      CombinedAnswerResponse response = new CombinedAnswerResponse(answer);
      answer.setCached(true);
      repository.save(answer);
      return response;
    }
    else {
      CombinedAnswer answer = repository.findById(name).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name isn't cached"));

      CombinedAnswerResponse response = new CombinedAnswerResponse(answer);

      return response;
    }
  }
}
