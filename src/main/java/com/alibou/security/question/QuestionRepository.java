package com.alibou.security.question;

import com.alibou.security.answer.Answer;
import com.alibou.security.quiz.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

    Optional<Question> findById(Long id);
    Page<Question> findByCategory(Category category, Pageable pageable);
    List<Question> findByIdInAndCategory(List<Integer> ids, Category category);
    List<Question> findByIdIn(List<Integer> ids);


}
