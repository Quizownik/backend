package com.alibou.security.question;

import com.alibou.security.quiz.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

    Page<Question> findByCategory(Category category, Pageable pageable);
    List<Question> findByIdInAndCategory(List<Integer> ids, Category category);
    List<Question> findByIdIn(List<Integer> ids);

    @Query("SELECT q FROM Question q WHERE q.category = :category ORDER BY RANDOM() LIMIT :numberOfQuestions")
    List<Question> findRandomByCategory(Category category, Integer numberOfQuestions);

    @Query("SELECT q FROM Question q ORDER BY RANDOM() LIMIT :numberOfQuestions")
    List<Question> findRandom(Integer numberOfQuestions);
}
