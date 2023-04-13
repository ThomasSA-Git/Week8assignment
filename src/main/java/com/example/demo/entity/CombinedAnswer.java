package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class CombinedAnswer {

  @Id
  String name;
  String gender;
  double genderProbability;
  int age;
  int ageCount;
  String Country;
  double countryProbability;
  boolean cached;

}