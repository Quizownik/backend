package com.alibou.security.result;

import com.alibou.security.quiz.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultRepository  extends JpaRepository<Result, Integer> {
    Page<Result> findAllByUserId(Integer userId, Pageable pageable);
}
