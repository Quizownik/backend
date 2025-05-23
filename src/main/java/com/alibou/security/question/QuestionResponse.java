package com.alibou.security.question;

import com.alibou.security.answer.AnswerResponse;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@Builder
public class QuestionResponse {
    private Integer id;
    private String question;
    private List<AnswerResponse> answers;
}
