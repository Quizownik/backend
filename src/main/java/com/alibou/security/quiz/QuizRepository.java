package com.alibou.security.quiz;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Integer> {
    Page<Quiz> findAllByCategory(Category category, Pageable pageable);

    Page<Quiz> findAllByCategoryAndLevel(Category category, Level level, Pageable pageable);

    Page<Quiz> findAllByLevel(Level level, Pageable pageable);

    Quiz findByNameContainingIgnoreCase(String quizName);

    Optional<Quiz> findByName(String quizName);
}
