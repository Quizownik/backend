package com.alibou.security.result;

import com.alibou.security.quiz.Category;
import com.alibou.security.quiz.Quiz;
import com.alibou.security.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResultRepository  extends JpaRepository<Result, Integer> {
    Page<Result> findAllByUserId(Integer userId, Pageable pageable);


    void deleteAllByQuiz(Quiz quiz);

    List<Result> getResultsByUserAndCategory(User user, Category category);
}
