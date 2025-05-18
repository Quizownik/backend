package com.alibou.security.question;

import com.alibou.security.answer.Answer;
import com.alibou.security.answer.AnswerRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@Builder
public class QuestionRequest {

    private String question;
    private List<AnswerRequest> stringAnswers;

}
