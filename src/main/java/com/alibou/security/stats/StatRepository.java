package com.alibou.security.stats;

import com.alibou.security.quiz.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StatRepository extends JpaRepository<Statistics, Integer> {
    Optional<Statistics> findByQuiz(Quiz quiz);
    void deleteAllByQuiz(Quiz quiz);
}
