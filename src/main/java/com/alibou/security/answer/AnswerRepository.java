package com.alibou.security.answer;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Integer>{
    //<Answer,Integer> <-typ pola ID
    Optional<Answer> findById(Long id);

    List<Answer> findByQuestionId(Integer id);
}
