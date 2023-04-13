package com.example.demo.dto;

import com.example.demo.entity.CombinedAnswer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CombinedAnswerResponse {
 String name;
 String gender;
 double genderProbability;
 int age;
 int ageCount;
 String country;
 double countryProbability;
 boolean cached;


 public CombinedAnswerResponse(CombinedAnswer answer){
  this.name = answer.getName();
  this.gender = answer.getGender();
  this.genderProbability = answer.getGenderProbability();
  this.age = answer.getAge();
  this.ageCount = answer.getAgeCount();
  this.country = answer.getCountry();
  this.countryProbability = answer.getCountryProbability();
  this.cached = answer.isCached();

 }
}
