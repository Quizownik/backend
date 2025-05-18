package com.alibou.security.quiz;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Integer> {
    //Optional<Question> findById(Integer id);
}
