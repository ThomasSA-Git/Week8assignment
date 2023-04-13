package com.example.demo;

import com.example.demo.dto.Age;
import com.example.demo.dto.CombinedAnswerResponse;
import com.example.demo.dto.Gender;
import com.example.demo.dto.Nation;
import com.example.demo.entity.CombinedAnswer;
import com.example.demo.repository.CombinedAnswerRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class RemoteApiTester implements CommandLineRunner {


  CombinedAnswerRepository repository;

  public RemoteApiTester(CombinedAnswerRepository repository) {
    this.repository = repository;
  }

  private Mono<String> callSlowEndpoint(){
    Mono<String> slowResponse = WebClient.create()
        .get()
        .uri("http://localhost:8080/random-string-slow")
        .retrieve()
        .bodyToMono(String.class)
        .doOnError(e-> System.out.println("UUUPS : "+e.getMessage()));
    return slowResponse;
  }

  public void callSlowEndpointBlocking(){
    long start = System.currentTimeMillis();
    List<String> ramdomStrings = new ArrayList<>();

    Mono<String> slowResponse = callSlowEndpoint();
    ramdomStrings.add(slowResponse.block()); //Three seconds spent

    slowResponse = callSlowEndpoint();
    ramdomStrings.add(slowResponse.block());//Three seconds spent

    slowResponse = callSlowEndpoint();
    ramdomStrings.add(slowResponse.block());//Three seconds spent
    long end = System.currentTimeMillis();
    ramdomStrings. add(0,"Time spent BLOCKING (ms): "+(end-start));

    System.out.println(ramdomStrings.stream().collect(Collectors.joining(",")));
  }

  public void callSlowEndpointNonBlocking(){
    long start = System.currentTimeMillis();
    Mono<String> sr1 = callSlowEndpoint();
    Mono<String> sr2 = callSlowEndpoint();
    Mono<String> sr3 = callSlowEndpoint();

    var rs = Mono.zip(sr1,sr2,sr3).map(t-> {
      List<String> randomStrings = new ArrayList<>();
      randomStrings.add(t.getT1());
      randomStrings.add(t.getT2());
      randomStrings.add(t.getT3());
      long end = System.currentTimeMillis();
      randomStrings.add(0,"Time spent NON-BLOCKING (ms): "+(end-start));
      return randomStrings;
    });
    List<String> randoms = rs.block(); //We only block when all the three Mono's has fulfilled
    System.out.println(randoms.stream().collect(Collectors.joining(",")));
  }
/*
  Mono<Gender> getGenderForName(String name) {
    WebClient client = WebClient.create();
    Mono<Gender> gender = client.get()
        .uri("https://api.genderize.io?name="+name)
        .retrieve()
        .bodyToMono(Gender.class);
    return gender;
  }

  List<String> names = Arrays.asList("lars", "peter", "sanne", "kim", "david", "maja");


  public void getGendersBlocking() {
    long start = System.currentTimeMillis();
    List<Gender> genders = names.stream().map(name -> getGenderForName(name).block()).collect(Collectors.toList());
    long end = System.currentTimeMillis();
    System.out.println("Time for six external requests, BLOCKING: "+ (end-start));
  }

  public void getGendersNonBlocking() {
    long start = System.currentTimeMillis();
    var genders = names.stream().map(name -> getGenderForName(name)).collect(Collectors.toList());
    Flux<Gender> flux = Flux.merge(Flux.concat(genders));
    List<Gender> res = flux.collectList().block();
    long end = System.currentTimeMillis();
    System.out.println("Time for six external requests, NON-BLOCKING: "+ (end-start));
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
  }*/


  @Override
  public void run(String... args) throws Exception {
    //System.out.println(callSlowEndpoint().toString());


/*    String randomStr = callSlowEndpoint().block();
    System.out.println(randomStr);*/


/*
    callSlowEndpointBlocking();

    callSlowEndpointNonBlocking();
*/


/*    Gender gender = getGenderForName("poul").block();
    System.out.println(gender.getGender());*/


/*    getGendersBlocking();
    getGendersNonBlocking();*/


  }

}

