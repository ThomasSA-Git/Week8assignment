package com.example.demo.repository;

import com.example.demo.entity.CombinedAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CombinedAnswerRepository extends JpaRepository<CombinedAnswer, String> {

  boolean existsByName(String name);
}
