package com.alibou.security.question;

import com.alibou.security.answer.AnswerRequest;
import com.alibou.security.quiz.Category;
import com.alibou.security.quiz.Level;
import lombok.Builder;

import java.util.List;
@Builder
public record QuestionRequest(
       // Integer id,
        String question,
        Level level,
        Category category,
        List<AnswerRequest>answers) {

}
