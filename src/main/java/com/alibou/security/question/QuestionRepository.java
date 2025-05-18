package com.alibou.security.question;

import com.alibou.security.answer.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

    Optional<Question> findById(Long id);
}
